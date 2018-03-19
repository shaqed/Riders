const ICON_GREEN = "markers-icons/green.png";
const ICON_BLUE = "markers-icons/blue.png";
const ICON_RED = "markers-icons/red.png";
const ICON_YELLOW = "markers-icons/yellow.png";


var btnAdd = document.getElementById("btn-add");
var statusText = document.getElementById("sts");
var sourceInputText = document.getElementById("source-ip");
var destInputText = document.getElementById("destination-ip");


var passengersIds = 0;

function addPassenger() {
    var container = document.getElementById("passengers-container");

    var passengerIndex = passengersIds++;
    var cont = document.createElement("div");
    cont.id = passengerIndex;

    var span = document.createElement("span");
    span.innerText = "Passenger " + passengerIndex + ": Si: ";

    var input = document.createElement("input");
    input.id = passengerIndex + " si";
    input.type = "text";

    input.value = "Click to set";

    input.onclick = edt;


    var span2 = document.createElement("span");
    span2.innerText = " Ti: ";


    var input2 = document.createElement("input");
    input2.id = passengerIndex + " ti";
    input2.type = "text";

    input2.value = "Click to set";

    input2.onclick = edt;


    var btn = document.createElement("button");
    btn.innerText = "X";
    btn.id = "del-"+ passengerIndex;
    btn.onclick = removePassenger;

    cont.appendChild(span);
    cont.appendChild(input);
    cont.appendChild(span2);
    cont.appendChild(input2);
    cont.appendChild(btn);
    container.appendChild(cont);

}

function removePassenger() {
    var index = this.id.split("-")[1];

    // Get the element to remove
    var passengersContainer = document.getElementById("passengers-container");
    var children = passengersContainer.children;
    var eleToRemove = null;
    for (var j = 0; j < children.length; j++) {
        if (children[j].id === index) {
            eleToRemove = children[j];
            break;
        }
    }



    // Before deleting it, make sure to remove the markers (if applied) on the map
    var inputs = eleToRemove.getElementsByTagName("input");
    for (var i = 0; i < inputs.length; i++) {
        if (inputs[i].gMarker !== undefined) {
            // Marked is present. remove it
            inputs[i].gMarker.setMap(null); // that should do it
        }
    }
    passengersContainer.removeChild(eleToRemove);

}


/**
 * Function that get used by the input texts of Si and Ti
 * */
function edt() {
    if (this.parentNode !== undefined) {
        var index = this.parentNode.id; // get index of said passenger
    }
    var isSi = this.id.includes("si");
    var isTi = this.id.includes("ti");

    if (isSi) {
        statusText.currentEdit = this.id; // editing this input text
        statusText.innerText = "Editing Passenger #" + index + "'s Si"
        statusText.gIcon = ICON_BLUE;
    } else if (this.id === "destination-ip") {
        statusText.currentEdit = this.id;
        statusText.innerText = "Editing Destination";
        statusText.gIcon = ICON_GREEN;
    } else if (isTi) {
        statusText.currentEdit = this.id;
        statusText.innerText = "Editing Passenger #" + index + "'s Ti";
        statusText.gIcon = ICON_YELLOW;
    } else if (this.id === "destination-ip") {
        statusText.currentEdit = this.id;
        statusText.innerText = "Editing Destination";
        statusText.gIcon = ICON_GREEN;
    } else if (this.id === "source-ip") {
        statusText.currentEdit = this.id;
        statusText.innerText = "Editing Source";
        statusText.gIcon = ICON_RED;
    }  else {
        console.log("Error, unexpected item to be edited: " + this.id);
    }

    // statusText.c = index;
    // statusText.innerText = "Editing passenger: " + index + ". Click on the map";
    // console.log(statusText.c);

}


/**
 * This function is called from the map_js.js when a click was performed on the map
 * @param point the point from the Google Maps, contains lat and lng fields
 * */
function doneEditing(point) {
    var currentlyEditing = statusText.currentEdit; // id of the input text currently editing

    var elementToEdit = document.getElementById(currentlyEditing);
    elementToEdit.value = point.lat + ", " + point.lng;
    elementToEdit.point = point;


    // Clear
    statusText.innerText = "";
    statusText.currentEdit = undefined;
    statusText.gIcon = undefined;

}


btnAdd.onclick = function () {
    addPassenger();
};


sourceInputText.onclick = edt;
destInputText.onclick = edt;

function go() {
    // make a JSON Object

    var passengersElements = document.getElementById("passengers-container").children;
    var passengers = [];
    for (var i = 0; i < passengersElements.length; i++) {
        var inputs = passengersElements[i].getElementsByTagName("input");
        var psngr = {
            name : "Passenger: " + passengersElements[i].id,
            si : null,
            ti : null
        };

        for (var j = 0; j < inputs.length; j++){
            if (inputs[j].id.includes("si")) {
                psngr.si = inputs[j].point;
            } else if (inputs[j].id.includes("ti")) {
                psngr.ti = inputs[j].point;
            } else {
                console.log("Weird input element inside of a passenger's container (not si and not ti:");
                console.log(inputs[j]);
            }
        }
        passengers.push(psngr);
    }

    var data = {
        radius : document.getElementById("radius-ip").value,
        source : document.getElementById("source-ip").point,
        dest : document.getElementById("destination-ip").point,
        passengers : passengers
    };

    console.log(data);
    console.log(JSON.stringify(data))
    // Send a POST request to /api/GetRouteServlet

    // for now the object is ready to be sent


    // Display result on the map (display directions when you have the order)
}
