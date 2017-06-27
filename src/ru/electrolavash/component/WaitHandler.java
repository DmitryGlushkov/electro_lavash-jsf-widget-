package ru.electrolavash.component;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;
import java.io.IOException;

public class WaitHandler extends TagHandler {

    public WaitHandler(TagConfig config) {
        super(config);
        System.out.println("WaitHandler");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent uiComponent) throws IOException {
        System.out.println("apply");

    }
}
