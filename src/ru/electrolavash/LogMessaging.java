package ru.electrolavash;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class LogMessaging {

    private String blurId;

    @PostConstruct
    void init() {
        final HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        final Object idObject = req.getSession().getAttribute("blurid");
        if (idObject != null) {
            blurId = ((String[]) idObject)[0];
        }
    }

    protected void alert(final String message) {
        if (blurId != null) SessionHandler.send(blurId, message);
    }

}
