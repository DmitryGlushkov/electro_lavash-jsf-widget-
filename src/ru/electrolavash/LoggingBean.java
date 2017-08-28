package ru.electrolavash;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class LoggingBean {

    private String blurId;

    @PostConstruct
    void init() {
        final HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        final String[] paramArray = req.getParameterMap().get("blur_id");
        if (paramArray != null && paramArray.length > 0) {
            blurId = paramArray[0];
        }
    }

    protected void alert(final String message) {
        if (blurId != null) SessionHandler.send(blurId, message);
    }

}
