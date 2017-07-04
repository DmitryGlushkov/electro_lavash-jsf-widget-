package ru.electrolavash;

import com.sun.faces.facelets.tag.TagHandlerImpl;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.BehaviorHolderAttachedObjectHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import java.io.IOException;

public class BlurHandler extends TagHandlerImpl implements BehaviorHolderAttachedObjectHandler {

    private final TagAttribute effect;

    public BlurHandler(TagConfig config) {
        super(config);
        this.effect = this.getAttribute("effect");
        System.out.println("asasas");
    }

    @Override
    public String getEventName() {
        return null;
    }

    @Override
    public void applyAttachedObject(FacesContext context, UIComponent parent) {
        System.out.println("applyAttachedObject");

    }

    @Override
    public String getFor() {
        return null;
    }

    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        System.out.println("apply");

    }
}
