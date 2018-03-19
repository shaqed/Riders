const greenIcon = "http://maps.google.com/mapfiles/ms/icons/green-dot.png";

var btnAdd = document.getElementById("btn-add");
var statusText = document.getElementById("sts");
var sourceInputText = document.getElementById("source-ip");
var destInputText = document.getElementById("destination-ip");


passengers = []; // Array

function presentPassengers() {
    var container = document.getElementById("passengers-container");

    container.innerHTML = ""; // clean

    for (var i = 0; i < passengers.length; i++) {
        var cont = document.createElement("div");
        cont.id = i;

        var span = document.createElement("span");
        span.innerText = "Passenger: " + i;

        var input = document.createElement("input");
        input.type = "text";
        if (passengers[i].value !== undefined) {
            input.value = passengers[i].value.lat + ", " + passengers[i].value.lng;
        } else {
            input.value = "Click to set";
        }
        input.onclick = edt;

        var btn = document.createElement("button");
        btn.innerText = "X";
        btn.onclick = removePassenger;

        cont.appendChild(span);
        cont.appendChild(input);
        cont.appendChild(btn);
        container.appendChild(cont);
    }
}

function addPassenger() {
    passengers.push({
        name: "",
        value: undefined
    });
    presentPassengers();
}

function removePassenger() {
    var index = this.parentNode.id;
    passengers.splice(index, 1);
    presentPassengers();
    // refresh displayed list
}

function edt() {
    var index = this.parentNode.id;

    statusText.c = index;
    statusText.innerText = "Editing passenger: " + index + ". Click on the map";
    console.log(statusText.c);

}

function doneEditing(point) {
    var index = statusText.c;
    if (index === "source") {
        sourceInputText.point = point;
        sourceInputText.value = point.lat + ", " + point.lng;
    } else if (index === "destination"){
        destInputText.point = point;
        destInputText.value = point.lat + ", " + point.lng;
    } else {
        passengers[index].value = point;
        presentPassengers();
    }

    // Clear status
    statusText.c = undefined;
    statusText.innerText = "";
}


btnAdd.onclick = function () {
    addPassenger();
};

sourceInputText.onclick = function () {
    statusText.innerText = "Select Source";
    statusText.c = "source";
    // edt();
};

destInputText.onclick = function () {
    statusText.innerText = "Select Destination";
    statusText.c = "destination";
    // edt();
};


