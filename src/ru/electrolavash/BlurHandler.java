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
import javax.faces.view.facelets.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class BlurHandler extends TagHandlerImpl {

    private static final String JS_FUNCTION = "blur_listener";
    private static final String RENDER_TYPE_SCRIPT = "javax.faces.resource.Script";
    private static final String RENDER_TYPE_STYLE = "javax.faces.resource.Stylesheet";
    private final TagAttribute execute;
    private final TagAttribute render;
    private final TagAttribute target;
    private final TagAttribute log;
    private final boolean wrapping;

    public BlurHandler(TagConfig config) {
        super(config);
        this.execute = this.getAttribute("execute");
        this.render = this.getAttribute("render");
        this.target = this.getAttribute("target");
        this.log = this.getAttribute("log");
        this.wrapping = isWrapping();
    }

    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        final  HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        System.out.println("apply");
        if (this.wrapping) {
            return;
        } else {
            if (!ComponentHandler.isNew(parent)) {
                return;
            }
            if (parent instanceof ClientBehaviorHolder) {
                parent.getPassThroughAttributes(true).put("target", target.getValue());
                if(log != null){
                    // тут магия
                    final String ajaxHeader = req.getHeader("faces-request");
                    if(ajaxHeader == null){
                        final String blurId = UUID.randomUUID().toString();
                        req.getSession().setAttribute("blurid", new String[]{blurId});
                        System.out.println("getPassThroughAttributes : " + blurId);
                        parent.getPassThroughAttributes(true).put("blurid", blurId);
                    }
                }
                final ClientBehaviorHolder bHolder = (ClientBehaviorHolder) parent;
                final String eventName = bHolder.getDefaultEventName();
                final AjaxBehavior ajaxBehavior = createAjaxBehavior(ctx);
                bHolder.addClientBehavior(eventName, ajaxBehavior);
                installAjax();
            } else {
                throw new TagException(this.tag, "Unable to attach <f:ajax> to non-ClientBehaviorHolder parent");
            }
        }
    }

    private void installAjax() {

        FacesContext context = FacesContext.getCurrentInstance();

        if (RenderKitUtils.hasScriptBeenRendered(context)) {
            return;
        }

        final UIOutput jsf = getLibOutput(context, "jsf.js", "javax.faces", RENDER_TYPE_SCRIPT);
        final UIOutput lavash = getLibOutput(context, "lavash.js", "ru.electrolavash", RENDER_TYPE_SCRIPT);
        final UIOutput css = getLibOutput(context, "lavash.css", "ru.electrolavash", RENDER_TYPE_STYLE);

        if (jsf != null) context.getViewRoot().addComponentResource(context, jsf, "head");
        if (lavash != null) context.getViewRoot().addComponentResource(context, lavash, "head");
        if (css != null) context.getViewRoot().addComponentResource(context, css, "head");

        RenderKitUtils.setScriptAsRendered(context);

    }

    private UIOutput getLibOutput(final FacesContext context, final String name, final String library, final String renderType) {
        if (RenderKitUtils.hasResourceBeenInstalled(context, name, library)) {
            RenderKitUtils.setScriptAsRendered(context);
            return null;
        }
        UIOutput output = new UIOutput();
        output.setRendererType(renderType);
        output.getAttributes().put("name", name);
        output.getAttributes().put("library", library);
        return output;
    }

    private boolean isWrapping() {
        return ((this.nextHandler instanceof TagHandler) || (this.nextHandler instanceof CompositeFaceletHandler));
    }

    private AjaxBehavior createAjaxBehavior(FaceletContext ctx) {
        Application application = ctx.getFacesContext().getApplication();
        AjaxBehavior behavior = (AjaxBehavior) application.createBehavior(AjaxBehavior.BEHAVIOR_ID);
        setBehaviorAttribute(ctx, behavior, this.execute, Object.class);
        setBehaviorAttribute(ctx, behavior, this.render, Object.class);
        setBehaviorAttribute(ctx, behavior, new TagAttributeImpl(target.getLocation(), target.getNamespace(), "onevent", "onevent", JS_FUNCTION), String.class);
        return behavior;
    }

    private void setBehaviorAttribute(FaceletContext ctx, AjaxBehavior behavior, TagAttribute attr, Class type) {
        if (attr != null) {
            behavior.setValueExpression(attr.getLocalName(), attr.getValueExpression(ctx, type));
        }
    }
}

