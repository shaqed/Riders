var map;
var directionsService;
var directionsDisplay;

var displayedPath = [];
var tspAnswer = null;

function reset() {
    if (directionsDisplay === undefined) {
        directionsDisplay = new google.maps.DirectionsRenderer();
    }

    directionsDisplay.set("directions", null); // Clear directions if given any

    var elements = $("input[type=text]");
    for (var i = 0; i < elements.length; i++) {
        var ele = elements[i];
        if (ele.gMarker !== undefined) {
            ele.gMarker.setMap(null);
            ele.gMarker = undefined;
            ele.value = "";
        } else {


        }
    }

    $("#radius-ip").val("");

    $("#passengers-container").empty();
    passengersIds = 0;
}

/**
 * @param data array of points given from the main TSP API
 * */
function displayRouteOnMap(data, onlineStateObject) {
    tspAnswer = data;
    onlineObject.mainPoints = data; // For later use of the onlie algorithm

    // if (onlineStateObject !== undefined) {
    //     onlineStateObject.mainPoints = data.route;
    // }

    var source = data[0];
    var dest = data[data.length - 1];
    var waypoints = [];
    for (var i = 1; i < data.length - 1; i++) {
        waypoints.push({
            location: data[i],
            stopover: true
        });
    }


    if (directionsService === undefined) {
        directionsService = new google.maps.DirectionsService();
    }

    if (directionsDisplay === undefined) {
        directionsDisplay = new google.maps.DirectionsRenderer();
    }
    directionsDisplay.setMap(map);
    directionsService.route({
        origin : source,
        destination : dest,
        waypoints: waypoints,
        travelMode : google.maps.TravelMode["DRIVING"]
    }, function (response, status) {
        if (status === "OK") {
            console.log("directions are: " , response);

            // Extract the points in set them in the displayed route
            var pathFromAnswer = response.routes[0].overview_path;
            for (var i = 0; i < pathFromAnswer.length; i++) {
                var p = pathFromAnswer[i];
                displayedPath.push({
                    lat : p.lat(),
                    lng : p.lng()
                });
            }

            // compute the total plength
            var legsArray = response.routes[0].legs;
            var plengthSum = 0;
            for (var j = 0; j < legsArray.length; j++) {
                plengthSum += legsArray[j].distance.value;
            }

            console.log("displayed_path is: ", displayedPath, " its total length is " + plengthSum);

            // Remove this
            if (onlineStateObject !== undefined) {
                onlineUpdateChange(onlineStateObject, pathFromAnswer, plengthSum);
            }


            // Online object construction
            // Online object should be ready by now
            globalOnlineInitPath(displayedPath, plengthSum);


            // This is the end of the GetRoute API call
            directionsDisplay.setDirections(response);
            carDisplay();
        } else {
            console.log("Error while displaying final route");
            console.log(response);
        }
    });

}


/**
 * This function ONLY computes the path from source to destination from Google
 * */
function directionsOnMap(data, waypoints) {
    var directionsService = new google.maps.DirectionsService();
    var directionsDisplay = new google.maps.DirectionsRenderer();
    // var haight = new google.maps.LatLng(37.7699298, -122.4469157);
    // var oceanBeach = new google.maps.LatLng(37.7683909618184, -122.51089453697205);

    console.log("DirectionOnMap");
    console.log(data);
    console.log(waypoints);

    directionsDisplay.setMap(map);

    directionsService.route({
        origin: data.source,
        destination: data.dest,
        waypoints : waypoints, // undefined
        travelMode : google.maps.TravelMode["DRIVING"]
    }, function (response, status) {
        if (status === "OK") {
            console.log("GOOD");
            console.log(response);
            //directionsDisplay.setDirections(response);

            // Extract route from response
            // Add it to the JSON
            var points = response.routes[0].overview_path;
            var path = [];
            for (var i = 0; i < points.length; i++) {
                path.push({
                    lat : points[i].lat(),
                    lng : points[i].lng()
                });
            }
            data.path = path;
            // compute leg
            data.plength = response.routes[0].legs[0].distance.value; // length in meters

            console.log("data.plength: " , data.plength);
            sendRequest(data);

        } else {
            console.log("BAD");
            console.log(response);
        }
    });
}

/**
 * Called by Google Maps
 * */
function initMap() {

    var mapOptions = {
        zoom: 14,
        center: new google.maps.LatLng(31.526771, 34.599354) // Sderot
    };

    map = new google.maps.Map(document.getElementById('map'), mapOptions);
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) {
            initialLocation = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
            map.setCenter(initialLocation);
        });
    }


    // Set map's onclick events
    map.addListener("click", function (e) {
        var lat = e.latLng.lat();
        var lng = e.latLng.lng();

        var coords = {
            lat: lat,
            lng : lng
        };

        var onlineButton = document.getElementById("online");

        if (statusText.currentEdit === "online-si") {

            globalOnlineState.newPassenger.si = {
                lat: lat,
                lng: lng
            };
            if (onlineButton.sigMarker === undefined) {
                onlineButton.sigMarker = new google.maps.Marker({
                    position: coords,
                    zIndex: -100,
                    map: map
                })
            }

            doneEditing(coords);

        } else if (statusText.currentEdit === "online-ti") {
            globalOnlineState.newPassenger.ti = {
                lat: lat,
                lng: lng
            };
            if (onlineButton.tigMarker === undefined) {
                onlineButton.tigMarker = new google.maps.Marker({
                    position: coords,
                    zIndex: -100,
                    map: map
                })
            }

            doneEditing(coords);
        } else if (statusText.currentEdit !== undefined) {

            var passengerIndex = statusText.currentEdit.split(" ")[0]; // get the index

            var elementToEdit = document.getElementById(statusText.currentEdit);
            if (elementToEdit.gMarker === undefined) {
                // Create a new marker
                // Store the marker on the element itself
                elementToEdit.gMarker = new google.maps.Marker({
                    position: coords,
                    icon : {
                        url : statusText.gIcon,
                        labelOrigin : new google.maps.Point(0, -15)
                    },
                    label : {
                        text : statusText.currentEdit[0].toUpperCase(),
                        fontSize : "20px",
                        color : 'blue',
                        fontWeight: 'bold'
                    },
                    zIndex: -100,
                    map : map
                });
                elementToEdit.gMarker.setZIndex(-100);
            } else {
                // Marker already there, just change its position
                elementToEdit.gMarker.setPosition(coords);
            }

            doneEditing(coords);
        } else {
            alert("Click on some input type text first")
        }

    });

}

// document.getElementById("go").onclick = function () {
//     var source = sourceInputText.point;
//     var dest = destInputText.point;
//
//     var waypoints = [];
//     for (var i = 0; i < passengers.length; i++) {
//         var passenger = passengers[i];
//
//         if (passenger !== undefined) {
//             console.log(passenger);
//             if (passenger.value !== undefined) {
//                 // console.log("Pushing: " + passenger.value);
//                 waypoints.push({
//                     location : passenger.value,
//                     stopover : true
//                 });
//             }
//         }
//
//     }
//
//     console.log(waypoints);
//     directionsOnMap(source, dest, waypoints);
// };
