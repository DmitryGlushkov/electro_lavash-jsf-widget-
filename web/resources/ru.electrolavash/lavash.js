

var B = function () {
    this.start = function () {
        console.log("start");
    }
    this.stop = function () {
        console.log("stop");
    }
}

function blur_listener(data) {
    var status = data.status;
    switch (status) {
        case "begin":
            console.log("begin");
            //B.start();
            break;
        case "complete":
            console.log("complete");
            //B.stop();
            break;
        case "success":
            console.log('success');
            break;
    }
}

function myMove() {
    var elem = document.getElementById("animate");
    var pos = 0;
    var id = setInterval(frame, 5);
    function frame() {
        if (pos == 350) {
            clearInterval(id);
        } else {
            pos++;
            elem.style.top = pos + 'px';
            elem.style.left = pos + 'px';
        }
    }
}


function blur() {
    console.log('-> blur');
    //document.body.className += " blur";
    //document.body.classList.add("blur");
    var body = document.body;
    body.style.setProperty('filter', 'blur(5px)');

    var id = setInterval(frame, 5);
    var i = 0;
    function frame() {
        i++;
        console.log(i);
        /*if (pos == 350) {
            clearInterval(id);
        } else {
            pos++;
            elem.style.top = pos + 'px';
            elem.style.left = pos + 'px';
        }*/
    }

}

function unblur() {
    console.log('-> unblur');
    //document.body.classList.remove("blur");
}