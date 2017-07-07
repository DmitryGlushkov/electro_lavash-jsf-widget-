package ru.electrolavash;

import com.sun.faces.renderkit.html_basic.AjaxBehaviorRenderer;

import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.render.FacesBehaviorRenderer;

@FacesBehaviorRenderer(rendererType = "ru.electrolavash.BlurBehavior._ID")
public class BlurBehaviorRenderer extends AjaxBehaviorRenderer {

    @Override
    public String getScript(ClientBehaviorContext behaviorContext, ClientBehavior behavior) {
        final String s = super.getScript(behaviorContext, behavior);
        return s;
    }
}
