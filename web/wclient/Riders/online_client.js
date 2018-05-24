

/**
 * This object is being constructed gradually after each step in the process of the algorithm
 * At the end it should have a 'ready' field which will indicate if the object is ready to be sent
 * as data to an online request
 * */
var globalOnlineState = {
};

function globalOnlineInit(radius, maxChange = 100) {
    globalOnlineState = {
        radius : radius,
        newPassenger: {
            // Will be filled later from the user
        },
        maxChange : maxChange
    };
}

function globalOnlineInitMainPoints(mainPoints) {
    globalOnlineState.mainPoints = mainPoints;
}

function globalOnlineInitPath(totalPoints, plength) {
    globalOnlineState.path = totalPoints;
    globalOnlineState.plength = plength;

    // Flag that the global online object has been fully constructed
    // And you can use it
    globalOnlineState.ready = globalOnlineState.mainPoints !== undefined && globalOnlineState.radius !== undefined
    && globalOnlineState.maxChange !== undefined;
}

// For testing
function OnlineState() {

    this.mainPoints = testMainPoints;
    this.path = testTotalPoints;
    this.plength = onlineObject.plength;
    this.radius = onlineObject.radius;
    this.newPassenger = onlineObject.newPassenger;
    this.maxChange = 100;
}

// Send a request with the online state parameter
function onlineSendRequest() {
    $.ajax({
        url : "/api/includepassenger",
        type : "POST",
        contentType : "application/json",
        data : JSON.stringify(globalOnlineState),
        success : function (response) {
            console.log("Online Request Successful", response);
            // Display route with a given onlineState
            // This online state is to be update later
            // By it calling the callbacks below
            displayRouteOnMap(response.route, globalOnlineState);

        },
        error : function (response) {
            console.log("Online request FAILED : " , response)
        }
    });
}

function onlineUpdateChange(onlineStateObject, totalPoints, plength) {
    onlineStateObject.path = totalPoints;

    // Before assigning new plength - compute the change in percentages
    var changePercent = (plength / onlineStateObject.plength) * 100;
    console.log("change is: " + changePercent + " max change in onlineStateObject is: " + onlineStateObject.maxChange + ". deducting from it");

    onlineStateObject.maxChange -= (changePercent-100);
    console.log("max change now is: "  + onlineStateObject.maxChange);

}


// Used by the client when pressed on the 'online' button
function doOnline() {
    if (!globalOnlineState.ready) {
        window.alert("Online error: Get your route first");
        return;
    }

    // Ask the user for an Si
    statusText.currentEdit = "online-si";
    statusText.innerText = "Select Si on the map"


    // Ask the user for a Ti

    // Assign newPassenger to the global online state object and go!

}