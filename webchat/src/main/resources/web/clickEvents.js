function openUrl(url) {
    window.open(url, "_blank");
}

function sendCommand(command) {
    sendMessage(command);
}

function fillCommandInput(command) {
    document.getElementById("messageinput").value = command;
}

function fillClipboard(content) {
    navigator.clipboard.writeText(content);
}