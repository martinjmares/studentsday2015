var webSocket;
var intervalId;

// init
$(function () {
    initWs();

    var leftButton = $('#leftButton');
    leftButton.on( "mousedown touchstart", function(e){
        sendEmptyEvent("LEFT_DOWN");
    });

    leftButton.on( "mouseup touchend", function(e){
        sendEmptyEvent("LEFT_UP");
    });

    var upButton = $('#upButton');
    upButton.on( "mousedown touchstart", function(e){
        sendEmptyEvent("UP_DOWN");
    });

    upButton.on( "mouseup touchend", function(e){
        sendEmptyEvent("UP_UP");
    });

    var rightButton = $('#rightButton');
    rightButton.on( "mousedown touchstart", function(e){
        sendEmptyEvent("RIGHT_DOWN");
    });

    rightButton.on( "mouseup touchend", function(e){
        sendEmptyEvent("RIGHT_UP");
    });

    var downButton = $('#downButton');
    downButton.on( "mousedown touchstart", function(e){
        sendEmptyEvent("DOWN_DOWN");
    });

    downButton.on( "mouseup touchend", function(e){
        sendEmptyEvent("DOWN_UP");
    });
});

function registerUser() {
    var name = $('#userName').val();
    var e = new Event("USER_SUBMIT", name);
    e.send();
    $("#controls").show();
    $("#userRegistration").hide();

}

function initWs() {
    var host = window.location.hostname + ":80";
    var uri = "ws://" + host + "/events";
    webSocket = new WebSocket(uri);
    webSocket.onmessage = function (evt) {
        var event = JSON.parse(evt.data);
        switch (event.eventType) {
            case "ENABLE_CONTROL":
                onEnableControl();
                break;
            case "DISABLE_CONTROL":
                onDisableControl();
                break;
            case "USER_CHANGE":
                onUserChange(event.payload);
                break;
            case "LEFT_DOWN":
                onLeftDown();
                break;
            case "LEFT_UP":
                onLeftUp();
                break;
            case "UP_DOWN":
                onUpDown();
                break;
            case "UP_UP":
                onUpUp();
                break;
            case "RIGHT_DOWN":
                onRightDown();
                break;
            case "RIGHT_UP":
                onRightUp();
                break;
            case "DOWN_DOWN":
                onDownDown();
                break;
            case "DOWN_UP":
                onDownUp();
                break;
            case "QUEUE_UPDATE":
                redrawQueue(event.payload);
                break;
            case "DUPLICATE_NAME":
                onDuplicateUserName(event.payload);
                break;
        }
    };

    webSocket.onerror = function (evt) {
        createErrorAlert("Connection error: " + evt + " Try reloading the page.")
    };
}

function onUserChange(user) {
    window.clearInterval(intervalId);
    onLeftUp();
    onUpUp();
    onRightUp();
    onDownUp();
    createInfoAlert(user + "'s turn begins");
    var progressbar = $("#progressbar");
    var counter = 0;
    intervalId = window.setInterval(function() {
        counter++;
        progressbar.width(counter * 2.5 + '%');

       if (counter == 40) {
           window.clearInterval(intervalId);
           progressbar.width('0%');
       }
    }, 500)

}

function redrawQueue(payload) {
    var currentCanvas = $("#currentUser");
    currentCanvas.html("");

    var queueCanvas = $("#queuedUsers");
    queueCanvas.html("");
    var queue = JSON.parse(payload);
    for (i = 0; i < queue.length; i++) {
        if (i == 0) {
            currentCanvas.append("<div class=\"panel-body\">" + queue[i] + "</div>");
        } else {
            queueCanvas.append("<div class=\"panel-body\">" + queue[i] + "</div>");
        }
    }
}

function requestSlot() {
    $("#requestSlotButton").addClass('disabled');
    sendEmptyEvent("REQUEST_SLOT");

}

function sendEmptyEvent(eventType) {
    var e = new Event(eventType, "");
    e.send();
}

function createInfoAlert(message) {
    var alerts = $("#alerts");
    alerts.append("<div class=\"alert alert-info\">"
        + message
        + "</div>");
    var addedAlert = alerts.children().last();
    window.setTimeout(function() {
        addedAlert.alert('close')
    }, 4000);
}

function onDuplicateUserName(name) {
    createErrorAlert("Name " + name + " seems to  be already used by someone else. Please select another one");
    $("#controls").hide();
    $("#userRegistration").show();
}

function createErrorAlert(message) {
    var alerts = $("#alerts");
    alerts.append("<div class=\"alert alert-danger\">"
        + message
        + "</div>");
    var addedAlert = alerts.children().last();
    window.setTimeout(function() {
        addedAlert.alert('close')
    }, 4000);
}

function onEnableControl() {
    $('#leftButton').removeClass('disabled');
    $('#upButton').removeClass('disabled');
    $('#rightButton').removeClass('disabled');
    $('#downButton').removeClass('disabled');
}

function onDisableControl() {
    $('#leftButton').addClass('disabled');
    $('#upButton').addClass('disabled');
    $('#rightButton').addClass('disabled');
    $('#downButton').addClass('disabled');
    $("#requestSlotButton").removeClass('disabled');
}

function onLeftDown() {
    var button = $('#leftButton');
    button.removeClass("btn-primary");
    button.addClass("btn-info");
}

function onLeftUp() {
    var button = $('#leftButton');
    button.removeClass("btn-info");
    button.addClass("btn-primary");
}

function onUpDown() {
    var button = $('#upButton');
    button.removeClass("btn-primary");
    button.addClass("btn-info");
}

function onUpUp() {
    var button = $('#upButton');
    button.removeClass("btn-info");
    button.addClass("btn-primary");
}


function onRightDown() {
    var button = $('#rightButton');
    button.removeClass("btn-primary");
    button.addClass("btn-info");
}

function onRightUp() {
    var button = $('#rightButton');
    button.removeClass("btn-info");
    button.addClass("btn-primary");
}

function onDownDown() {
    var button = $('#downButton');
    button.removeClass("btn-primary");
    button.addClass("btn-info");
}

function onDownUp() {
    var button = $('#downButton');
    button.removeClass("btn-info");
    button.addClass("btn-primary");
}

function Event(eventType, payload) {
    this.eventType = eventType;
    this.payload = payload;

    this.send = function () {
        var str = JSON.stringify(this);
        webSocket.send(str);
    }
}

