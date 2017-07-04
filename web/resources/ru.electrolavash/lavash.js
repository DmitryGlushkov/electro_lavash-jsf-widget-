function blur_listener(data) {
    var status = data.status; // Can be "begin", "complete" or "success".
    switch (status) {
        case "begin": // Before the ajax request is sent.
            console.log('begin');
            break;

        case "complete": // After the ajax response is arrived.
            console.log('complete');
            break;

        case "success": // After update of HTML DOM based on ajax response..
            console.log('success');
            break;
    }
}