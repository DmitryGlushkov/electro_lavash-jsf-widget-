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
    switch (data.status) {
        case "begin":
            target_element = getTargetElement(data);
            if (target_element != undefined) {
                blur(target_element);
            }
            break;
        case "complete":
            target_element = getTargetElement(data);
            if (target_element != undefined) {
                unblur(target_element);
            }
            break;
    }
}

function blur(target) {
    target.classList.add("blur");
}

function unblur(target) {
    target.classList.remove("blur");
}