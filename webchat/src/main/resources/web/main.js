let webSocket;

function connect(username) {
    if (webSocket != null && webSocket.readyState !== WebSocket.CLOSED && webSocket.readyState !== WebSocket.CLOSING) {
        return;
    }

    webSocket = new WebSocket("ws://${host}/ws");
    webSocket.onopen = ev => {
        webSocket.send(username);
        console.log("Connection opened")
    };

    webSocket.onmessage = ev => {
        let input = ev.data;
        try {

            let json = JSON.parse(input);

            this.setInfo(json.success, json.message);

        } catch (e) {
            this.displayMessage(input);
        }
    }

    webSocket.onclose = ev => {
        console.log("Connection closed");
        webSocket = null;
        this.clearChat();
    }

}

function sendMessage(message) {
    if (webSocket == null) {
        return;
    }

    webSocket.send(message);
}


function displayMessage(message) {
    let element = document.createElement("a");
    element.innerText = message;

    let base = document.getElementById("messages");
    base.insertBefore(element, base.firstChild);
    base.insertBefore(document.createElement("br"), element);
}

function setInfo(success, message) {
    let successElement = document.getElementById("info-success");
    let messageElement = document.getElementById("info-message");

    successElement.innerText = success ? "Success" : "Failed";
    successElement.style.color = success ? "green" : "red";

    messageElement.innerText = message;

    setTimeout(() => {
        successElement.value = "";
        messageElement.value = "";
    }, 5000);

}

function clearChat() {
    this.clear(document.getElementById('messages'));
}

function clear(node) {
    console.log("test")
    while (node.firstChild) {
        console.log(node.firstChild);
        node.removeChild(node.firstChild);
    }
}
