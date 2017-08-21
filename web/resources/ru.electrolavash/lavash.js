var ACTIVITY = {};
var logger_socket = socket_init();

class BlurAnimation {

    constructor(socket) {
        this._xx = {value: 0.0};
        this._interval = undefined;
        this._socket = socket;
    }

    start(target) {
        console.log("anim-start");
        var interval = setInterval(blurFunction, 0);
        var xx = {value: 0.0};

        function blurFunction() {
            if (xx.value >= 6) {
                clearInterval(interval);
            } else {
                xx.value += 0.01;
                target.style.filter = 'blur(' + xx.value + 'px)';
            }
        }

        this._interval = interval;
        this._xx = xx;
    }

    stop(target) {
        clearInterval(this._interval);
        var interval = setInterval(unblurFunction, 0);
        var xx = this._xx;

        function unblurFunction() {
            if (xx.value == 0.0) {
                clearInterval(interval);
            } else {
                xx.value -= 0.05;
                target.style.filter = 'blur(' + xx.value + 'px)';
            }
        }

    }

}

function getElement(data, attr_name) {
    var target = data.source.attributes[attr_name];
    if (target != undefined) {
        var target_id = target.value;
        var form_id = data.source.form.id;
        var el_id = form_id + ':' + target_id;
        return document.getElementById(el_id);
    }
    return undefined;
}

function get_blur_id(data) {
    var attr = data.source.attributes["blurid"];
    return attr.value;
}

function blur_listener(data) {
    var target_element;
    var logging_element;
    var anim;
    switch (data.status) {
        case "begin":
            target_element = getElement(data, "target");
            blur_id = get_blur_id(data);
            if(blur_id != undefined){
                logger_socket.send(get_socket_object("register", blur_id));
            }
            if (target_element != undefined) {
                anim = new BlurAnimation(logger_socket);
                ACTIVITY[target_element.id] = anim;
                anim.start(target_element);
            }
            break;
        case "complete":
            target_element = getElement(data, "target");
            if (target_element != undefined) {
                anim = ACTIVITY[target_element.id];
                if (anim != undefined) {
                    anim.stop(target_element);
                    ACTIVITY[target_element.id] = undefined;
                }
            }
            break;
    }
}

function socket_init() {

    var socket = new WebSocket("ws://localhost:8989/log");

    socket.onopen = function () {

        console.log("Соединение установлено.");
    };

    socket.onclose = function (event) {
        if (event.wasClean) {
            console.log('Соединение закрыто чисто');
        } else {
            console.log('Обрыв соединения'); // например, "убит" процесс сервера
        }
        console.log('Код: ' + event.code + ' причина: ' + event.reason);
    };

    socket.onmessage = function (event) {
        console.log("Получены данные " + event.data);
    };

    socket.onerror = function (error) {
        console.log("Ошибка " + error.message);
    };

    return socket;
}

function get_socket_object(action, data = null) {
    var obj = {"action":action, "data":data};
    return JSON.stringify(obj);

}