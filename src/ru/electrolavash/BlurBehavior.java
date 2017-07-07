package ru.electrolavash;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.context.FacesContext;
import javax.faces.render.ClientBehaviorRenderer;

public class BlurBehavior extends AjaxBehavior {

    public static final String BEHAVIOR_ID = "ru.electrolavash.BlurBehavior._ID";

    @Override
    public String getRendererType() {
        return  BEHAVIOR_ID;
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (null == context || null == component) {
            throw new NullPointerException();
        }

        ClientBehaviorRenderer renderer = getRenderer(context);
        if (null != renderer){
            renderer.decode(context, component, this);
        }
    }
}
