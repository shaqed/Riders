var map;


function directionsOnMap(sourceLatlng, destLatlng, waypoints) {
    var directionsService = new google.maps.DirectionsService();
    var directionsDisplay = new google.maps.DirectionsRenderer();
    // var haight = new google.maps.LatLng(37.7699298, -122.4469157);
    // var oceanBeach = new google.maps.LatLng(37.7683909618184, -122.51089453697205);



    directionsDisplay.setMap(map);

    directionsService.route({
        origin: sourceLatlng,
        destination: destLatlng,
        waypoints : waypoints,
        travelMode : google.maps.TravelMode["DRIVING"]
    }, function (response, status) {
        if (status === "OK") {
            console.log("GOOD");
            console.log(response);
            directionsDisplay.setDirections(response);
        } else {
            console.log("BAD");
            console.log(response);
        }
    });
}


function initMap() {
    /*var m = new google.maps.Map(document.getElementById("map"), {
        zoom :4,
        center : {lat: -25.363, lng: 131.044}
    });
    m.addListener("click", function (e) {
        var lat = e.latLng.lat();
        var long = e.latLng.lng();

        console.log(lat + ", " + long);
    });*/



    /* GET DIRECTIONS AND DISPLAY THEM ON THE MAP*/

    /*END*/

    var mapOptions = {
        zoom: 14,
        center: new google.maps.LatLng(37.7699298, -122.4469157)
    };

    map = new google.maps.Map(document.getElementById('map'), mapOptions)

    map.addListener("click", function (e) {
        var lat = e.latLng.lat();
        var lng = e.latLng.lng();

        var coords = {
            lat: lat,
            lng : lng
        };

        if (passengers[statusText.c] !== undefined) {
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
    });

}

document.getElementById("go").onclick = function () {
    var source = sourceInputText.point;
    var dest = destInputText.point;

    var waypoints = [];
    for (var i = 0; i < passengers.length; i++) {
        var passenger = passengers[i];

        if (passenger !== undefined) {
            console.log(passenger);
            if (passenger.value !== undefined) {
                // console.log("Pushing: " + passenger.value);
                waypoints.push({
                    location : passenger.value,
                    stopover : true
                });
            }
        }

    }

    console.log(waypoints);
    directionsOnMap(source, dest, waypoints);
};
