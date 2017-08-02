var ACTIVITY = {};

class BlurAnimation {

    constructor() {
        this._xx = {value: 0.0};
        this._interval = undefined;
    }

    start(target) {
        var interval = setInterval(blurFunction, 0);
        var xx = {value: 0.0};

        function blurFunction() {
            if (xx.value >= 15) {
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
                xx.value -= 0.01;
                target.style.filter = 'blur(' + xx.value + 'px)';
            }
        }
    }
}

function getTargetElement(data) {
    var target = data.source.attributes["target"];
    if (target != undefined) {
        var target_id = target.value;
        var form_id = data.source.form.id;
        var el_id = form_id + ':' + target_id;
        return document.getElementById(el_id);
    }
    return undefined;
}

function blur_listener(data) {
    var target_element;
    var anim;
    switch (data.status) {
        case "begin":
            target_element = getTargetElement(data);
            if (target_element != undefined) {
                anim = new BlurAnimation();
                ACTIVITY[target_element.id] = anim;
                anim.start(target_element);
            }
            break;
        case "complete":
            target_element = getTargetElement(data);
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