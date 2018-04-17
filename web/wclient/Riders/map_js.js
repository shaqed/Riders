var map;
var directionsService;
var directionsDisplay;

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

function displayRouteOnMap(data) {

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
            directionsDisplay.setDirections(response);
        } else {
            console.log("Error while displaying final route");
            console.log(response);
        }
    });

}

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
        waypoints : waypoints,
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


    map.addListener("click", function (e) {
        var lat = e.latLng.lat();
        var lng = e.latLng.lng();

        var coords = {
            lat: lat,
            lng : lng
        };

        if (statusText.currentEdit !== undefined) {

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
                    map : map
                });
            } else {
                // Marker already there, just change its position
                elementToEdit.gMarker.setPosition(coords);
            }

            doneEditing(coords);
        } else {
            alert("Click on some input type text first")
        }



        /*if (passengers[statusText.c] !== undefined) {
            if (passengers[statusText.c].marker === undefined) {
                passengers[statusText.c].marker = new google.maps.Marker({
                    position: coords,
                    map: map
                });

            } else {
                // passengers[statusText.c].marker.setMap(null);
                passengers[statusText.c].marker.setPosition(coords);
                // passengers[statusText.c].marker.setMap(map);
            }
        } else if (statusText.c === "source") {
            if (sourceInputText.point === undefined) {
                sourceInputText.point = new google.maps.Marker({
                    position: coords,
                    icon : greenIcon,
                    map : map
                });
            } else {
                sourceInputText.point.setPosition(coords);
            }
        } else if (statusText.c === "destination") {
            if (destInputText.point === undefined) {
                destInputText.point = new google.maps.Marker({
                    position: coords,
                    icon : greenIcon,
                    map : map
                });
            } else {
                destInputText.point.setPosition(coords);
            }

        }
        // console.log("Setting: " + lat + ", " + lng);
        doneEditing(coords);
        */

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
