package ru.electrolavash;

import com.sun.faces.facelets.tag.TagAttributeImpl;
import com.sun.faces.facelets.tag.TagHandlerImpl;
import com.sun.faces.renderkit.RenderKitUtils;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.view.BehaviorHolderAttachedObjectHandler;
import javax.faces.view.facelets.*;
import java.io.IOException;

public class BlurHandler extends TagHandlerImpl implements BehaviorHolderAttachedObjectHandler {

    private final TagAttribute effect;
    private final TagAttribute event;
    private final TagAttribute execute;
    private final TagAttribute render;

    private final boolean wrapping;

    public BlurHandler(TagConfig config) {
        super(config);
        this.event = this.getAttribute("event");
        this.effect = this.getAttribute("effect");
        this.execute = this.getAttribute("execute");
        this.render = this.getAttribute("render");
        this.wrapping = isWrapping();
        System.out.println("[BlurHandler] constructor");
    }

    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {

        if (wrapping) {
            return;
        }

        if (!ComponentHandler.isNew(parent)) {
            return;
        }

        if (parent instanceof ClientBehaviorHolder) {
            final ClientBehaviorHolder bHolder = (ClientBehaviorHolder) parent;
            final String eventName = bHolder.getDefaultEventName();
            final AjaxBehavior ajaxBehavior = createAjaxBehavior(ctx);
            bHolder.addClientBehavior(eventName, ajaxBehavior);
            installAjaxResourceIfNecessary();
        } else {
            throw new TagException(this.tag, "Unable to attach <f:ajax> to non-ClientBehaviorHolder parent");
        }

    }

    private void installAjaxResourceIfNecessary() {

        FacesContext context = FacesContext.getCurrentInstance();

        if (RenderKitUtils.hasScriptBeenRendered(context)) {
            return;
        }

        final UIOutput jsf = getLibOutput(context, "jsf.js", "javax.faces");
        final UIOutput lavash = getLibOutput(context, "lavash.js", "ru.electrolavash");

        if (jsf != null) context.getViewRoot().addComponentResource(context, jsf, "head");
        if (lavash != null) context.getViewRoot().addComponentResource(context, lavash, "head");

        RenderKitUtils.setScriptAsRendered(context);

    }

    private UIOutput getLibOutput(final FacesContext context, final String name, final String library) {
        if (RenderKitUtils.hasResourceBeenInstalled(context, name, library)) {
            RenderKitUtils.setScriptAsRendered(context);
            return null;
        }
        UIOutput output = new UIOutput();
        output.setRendererType("javax.faces.resource.Script");
        output.getAttributes().put("name", name);
        output.getAttributes().put("library", library);
        return output;
    }

    private AjaxBehavior createAjaxBehavior(FaceletContext ctx) {
        final Application application = ctx.getFacesContext().getApplication();
        final AjaxBehavior behavior = (AjaxBehavior) application.createBehavior(AjaxBehavior.BEHAVIOR_ID);
        setBehaviorAttribute(ctx, behavior, this.execute, Object.class);
        setBehaviorAttribute(ctx, behavior, this.render, Object.class);
        setBehaviorAttribute(ctx, behavior, new TagAttributeImpl(render.getLocation(), render.getNamespace(), "onevent", "onevent", "blur_listener"), String.class);
        return behavior;
    }

    private void setBehaviorAttribute(FaceletContext ctx, AjaxBehavior behavior, TagAttribute attr, Class type) {
        if (attr != null) {
            behavior.setValueExpression(attr.getLocalName(), attr.getValueExpression(ctx, type));
        }
    }

    @Override
    public String getEventName() {
        FacesContext context = FacesContext.getCurrentInstance();
        FaceletContext ctx = (FaceletContext) context.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        return (this.event != null) ? this.event.getValue(ctx) : null;
    }

    @Override
    public void applyAttachedObject(FacesContext context, UIComponent parent) {
        System.out.println("[BlurHandler] applyAttachedObject");
    }

    @Override
    public String getFor() {
        return null;
    }


    private boolean isWrapping() {
        return ((this.nextHandler instanceof TagHandler) || (this.nextHandler instanceof CompositeFaceletHandler));
    }
}
