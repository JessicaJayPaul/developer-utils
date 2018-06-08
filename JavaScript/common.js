String.prototype.endWith = function (str) {
    return this.substring(this.length - str.length) === str;
};

String.prototype.startWith = function (str) {
    return this.substr(0, str.length) === str;
};

String.prototype.trim = function () {
    return this.replace(/(^\s+)|(\s+$)/g, '');
};

function getEvent() {
    if (document.all) return window.event;
    func = getEvent.caller;
    while (func !== null) {
        var arg0 = func.arguments[0];
        if (arg0) {
            if ((arg0.constructor === Event || arg0.constructor === MouseEvent) || (typeof (arg0) === "object" && arg0.preventDefault && arg0.stopPropagation)) {
                return arg0;
            }
        }
        func = func.caller;
    }
    return null;
}

function getTarget() {
    var event = getEvent();
    return event.srcElement || event.target;
}

function isExist(arr, obj) {
    var exist = false;
    for (var i = 0; i < arr.length; i++) {
        if (arr[i] === obj) {
            exist = true;
            break;
        }
    }
    return exist;
}
