let webSocket;

function connect(username) {
    if (webSocket != null && webSocket.readyState !== WebSocket.CLOSED && webSocket.readyState !== WebSocket.CLOSING) {
        return;
    }

    webSocket = new WebSocket("ws://" + document.location.host + "/ws");
    webSocket.onopen = ev => {
        webSocket.send(username);
        this.clearChat();
        console.log("Connection opened")
    };

    webSocket.onmessage = ev => {
        let input = ev.data;
        let json = JSON.parse(ev.data);

        if (json.hasOwnProperty("success") && json.hasOwnProperty("message")) {
            this.setInfo(json.success, json.message);
        }

        if (json.hasOwnProperty("modes")) {
            let modes = document.getElementById("modes-content");
            this.clear(modes);
            json.modes.forEach(mode => {
                let modeElement = document.createElement("a");
                modeElement.innerText = mode;
                modeElement.onclick = () => this.changeMode(mode);
                modes.append(modeElement);
            });
        }

        if (json.hasOwnProperty("selectedMode")) {
            document.getElementById("selectedMode").innerText = json.selectedMode;
        }

        if (json.hasOwnProperty("appendChat")) {
            this.displayMessage(json.appendChat);
        }

    }

    webSocket.onclose = ev => {
        console.log("Connection closed");
        webSocket = null;
    }

}

function sendMessage(message) {
    if (webSocket == null) {
        return;
    }

    webSocket.send(JSON.stringify({"action": "chat", "value": message}));
}

function changeMode(mode) {
    if (webSocket == null) {
        return;
    }

    webSocket.send(JSON.stringify({"action": "changeMode", "value": mode}))
}


function displayMessage(message) {
    let element = document.createElement("a");
    element.innerHTML = message;

    let base = document.getElementById("messages");
    base.append(document.createElement("br"));
    base.append(element);

    if (document.getElementById("autoscroll").checked) {
        document.getElementById("selectedMode").scrollIntoView();
    }
}

function setInfo(success, message) {
    let successElement = document.getElementById("info-success");
    let messageElement = document.getElementById("info-message");

    successElement.innerText = success ? "Success" : "Failed";
    successElement.style.color = success ? "lightgreen" : "red";

    messageElement.innerText = message;

    setTimeout(() => {
        successElement.innerText = "";
        messageElement.innerText = "";
    }, 5000);

}

function clearChat() {
    this.clear(document.getElementById('messages'));
}

function clear(node) {
    while (node.firstChild) {
        console.log(node.firstChild);
        node.removeChild(node.firstChild);
    }
}
