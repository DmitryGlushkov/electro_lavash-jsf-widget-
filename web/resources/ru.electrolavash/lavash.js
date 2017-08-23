var ACTIVITY = {};
var logger_socket = socket_init();
var logger_block = get_logger_block();
var OPACITY_LIMIT = 0.7;

class BlurAnimation {

    constructor() {
        this._xx = {value: 0.0};
        this._interval = undefined;
        this._block = undefined;
    }

    start(target) {
        var block = this.get_block();
        var interval = setInterval(blurFunction, 0);
        var xx = {value: 0.0};

        function blurFunction() {
            if (xx.value >= 6) {
                clearInterval(interval);
            } else {
                xx.value += 0.01;
                target.style.filter = 'blur(' + xx.value + 'px)';
                if (block.style.opacity < OPACITY_LIMIT) {
                    block.style.opacity = xx.value / 6;
                }
            }
        }

        this._interval = interval;
        this._xx = xx;
        this._block = block;
    }

    stop(target) {

        clearInterval(this._interval);

        var interval = setInterval(unblurFunction, 0);
        var xx = this._xx;
        var block = this._block;

        var remove_block = function () {
            if (block != undefined) {
                document.body.removeChild(block);
            }
        };

        function unblurFunction() {
            if (xx.value <= 0.0) {
                //remove_block();
                clearInterval(interval);
            } else {
                xx.value -= 0.05;
                target.style.filter = 'blur(' + xx.value + 'px)';
                if (block.style.opacity > 0) {
                    //block.style.opacity = xx.value / 6;
                }
            }
        }
    }

    get_block() {
        var block = document.createElement('div');
        block.className = 'block';
        block.style.opacity = 0;



        document.body.appendChild(block);
        document.body.appendChild(log);
        return block;
    }


}

function get_elements(data) {
    var target_class_name = data.source.attributes["targetClass"].value;
    var elements = document.getElementsByClassName(target_class_name);
    if (elements.length > 0) {
        return elements;
    }
    return undefined;
}

function get_blur_id(data) {
    var attr = data.source.attributes["blurid"];
    return attr.value;
}

function get_logger_block() {
    var log = document.createElement('div');
    log.className = 'log_info';
    log.textContent = "asasasas";
    return log;
}

function blur_listener(data) {
    var i, len;
    var target_elements;
    var el;
    var anim;
    switch (data.status) {
        case "begin":
            target_elements = get_elements(data);
            blur_id = get_blur_id(data);
            if (blur_id != undefined) {
                logger_socket.send(get_socket_object("register", blur_id));
            }

            for (i = 0, len = target_elements.length; i < len; ++i) {
                el = target_elements[i];
                anim = new BlurAnimation();
                ACTIVITY[el.id] = anim;
                anim.start(el);
            }
            break;
        case "complete":
            target_elements = get_elements(data);
            if (target_elements != undefined) {
                for (i = 0, len = target_elements.length; i < len; ++i) {
                    el = target_elements[i];
                    anim = ACTIVITY[el.id];
                    if (anim != undefined) {
                        anim.stop(el);
                        ACTIVITY[el.id] = undefined;
                    }
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
    var obj = {"action": action, "data": data};
    return JSON.stringify(obj);

}