package ru.electrolavash.blur;

import com.sun.faces.component.behavior.AjaxBehaviors;
import com.sun.faces.facelets.tag.TagAttributeImpl;
import com.sun.faces.facelets.tag.TagHandlerImpl;
import com.sun.faces.facelets.tag.jsf.CompositeComponentTagHandler;
import com.sun.faces.util.RequestStateManager;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;
import javax.faces.view.AttachedObjectTarget;
import javax.faces.view.BehaviorHolderAttachedObjectHandler;
import javax.faces.view.BehaviorHolderAttachedObjectTarget;
import javax.faces.view.facelets.*;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class BlurHandler extends TagHandlerImpl implements BehaviorHolderAttachedObjectHandler {

    private static final String JS_FUNCTION = "blur_listener";
    private static final String PARAM_NAME = "blur_id";

    private static final String RENDER_TYPE_SCRIPT = "javax.faces.resource.Script";
    private static final String RENDER_TYPE_STYLE = "javax.faces.resource.Stylesheet";

    public static final String SCRIPT_STATE_BLUR = "ru.electrolavash.blur.SCRIPT_STATE";

    public static final String RESOURCE_NAME_JSF = "jsf.js";
    public static final String RESOURCE_NAME_LAVASH_JS = "lavash.js";
    public static final String RESOURCE_NAME_LAVASH_CSS = "lavash.css";

    public static final String LIB_NAME_JSF = "javax.faces";
    public static final String LIB_NAME_LAVASH = "ru.electrolavash";


    private final TagAttribute event;
    private final TagAttribute execute;
    private final TagAttribute render;
    private final TagAttribute onevent;
    private final TagAttribute onerror;
    private final TagAttribute disabled;
    private final TagAttribute immediate;
    private final TagAttribute resetValues;
    private final TagAttribute listener;
    private final TagAttribute delay;
    private final TagAttribute targetClass;

    private final boolean wrapping;

    public BlurHandler(TagConfig config) {
        super(config);
        this.event = this.getAttribute("event");
        this.execute = this.getAttribute("execute");
        this.render = this.getAttribute("render");
        this.onevent = this.getAttribute("onevent");
        this.onerror = this.getAttribute("onerror");
        this.disabled = this.getAttribute("disabled");
        this.immediate = this.getAttribute("immediate");
        this.resetValues = this.getAttribute("resetValues");
        this.listener = this.getAttribute("listener");
        this.delay = this.getAttribute("delay");
        this.targetClass = this.getAttribute("class");

        this.wrapping = isWrapping();
    }

    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        final String blurId = getBlurId(parent);
        parent.getPassThroughAttributes(true).put("targetClass", targetClass.getValue());
        parent.getPassThroughAttributes(true).put(PARAM_NAME, blurId);

        String eventName = getEventName();
        if (this.wrapping) {
            applyWrapping(ctx, parent, eventName);
        } else {
            applyNested(ctx, parent, eventName);
        }
    }

    public void applyAttachedObject(FacesContext context, UIComponent parent) {
        FaceletContext ctx = (FaceletContext) context.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        applyAttachedObject(ctx, parent, getEventName());
    }

    public String getFor() {
        return null;
    }

    public String getEventName() {
        FacesContext context = FacesContext.getCurrentInstance();
        FaceletContext ctx = (FaceletContext) context.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        return (this.event != null) ? this.event.getValue(ctx) : null;
    }

    private boolean isWrapping() {
        return ((this.nextHandler instanceof TagHandler) || (this.nextHandler instanceof CompositeFaceletHandler));
    }

    private void applyWrapping(FaceletContext ctx, UIComponent parent, String eventName) throws IOException {
        installResourceIfNecessary();
        AjaxBehavior ajaxBehavior = createAjaxBehavior(ctx, eventName);
        FacesContext context = ctx.getFacesContext();
        AjaxBehaviors ajaxBehaviors = AjaxBehaviors.getAjaxBehaviors(context, true);
        ajaxBehaviors.pushBehavior(context, ajaxBehavior, eventName);
        nextHandler.apply(ctx, parent);
        ajaxBehaviors.popBehavior();
    }

    private void applyNested(FaceletContext ctx, UIComponent parent, String eventName) {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }

        if (UIComponent.isCompositeComponent(parent)) {
            boolean tagApplied = false;
            if (parent instanceof ClientBehaviorHolder) {
                applyAttachedObject(ctx, parent, eventName);  // error here will propagate up
                tagApplied = true;
            }
            BeanInfo componentBeanInfo = (BeanInfo) parent.getAttributes().get(UIComponent.BEANINFO_KEY);
            if (null == componentBeanInfo) {
                throw new TagException(tag, "Error: enclosing composite component does not have BeanInfo attribute");
            }
            BeanDescriptor componentDescriptor = componentBeanInfo.getBeanDescriptor();
            if (null == componentDescriptor) {
                throw new TagException(tag, "Error: enclosing composite component BeanInfo does not have BeanDescriptor");
            }
            List<AttachedObjectTarget> targetList = (List<AttachedObjectTarget>) componentDescriptor.getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);
            if (null == targetList && !tagApplied) {
                throw new TagException(tag, "Error: enclosing composite component does not support behavior events");
            }
            boolean supportedEvent = false;
            for (AttachedObjectTarget target : targetList) {
                if (target instanceof BehaviorHolderAttachedObjectTarget) {
                    BehaviorHolderAttachedObjectTarget behaviorTarget = (BehaviorHolderAttachedObjectTarget) target;
                    if ((null != eventName && eventName.equals(behaviorTarget.getName()))
                            || (null == eventName && behaviorTarget.isDefaultEvent())) {
                        supportedEvent = true;
                        break;
                    }
                }
            }
            if (supportedEvent) {
                CompositeComponentTagHandler.getAttachedObjectHandlers(parent).add(this);
            } else {
                if (!tagApplied) {
                    throw new TagException(tag, "Error: enclosing composite component does not support event " + eventName);
                }
            }
        } else if (parent instanceof ClientBehaviorHolder) {
            applyAttachedObject(ctx, parent, eventName);
        } else {
            throw new TagException(this.tag, "Unable to attach <f:ajax> to non-ClientBehaviorHolder parent");
        }

    }

    private void applyAttachedObject(FaceletContext ctx, UIComponent parent, String eventName) {
        ClientBehaviorHolder bHolder = (ClientBehaviorHolder) parent;
        if (null == eventName) {
            eventName = bHolder.getDefaultEventName();
            if (null == eventName) {
                throw new TagException(this.tag, "Event attribute could not be determined: " + eventName);
            }
        } else {
            Collection<String> eventNames = bHolder.getEventNames();
            if (!eventNames.contains(eventName)) {
                throw new TagException(this.tag, getUnsupportedEventMessage(eventName, eventNames, parent));
            }
        }

        AjaxBehavior ajaxBehavior = createAjaxBehavior(ctx, eventName);
        bHolder.addClientBehavior(eventName, ajaxBehavior);
        installResourceIfNecessary();
    }

    private String getBlurId(final UIComponent parent) {
        UIParameter parameter = null;
        for (final UIComponent child : parent.getChildren()) {
            if (child instanceof UIParameter) {
                final String name = ((UIParameter) child).getName();
                if (name.equals(PARAM_NAME)) {
                    parameter = (UIParameter) child;
                    break;
                }
            }
        }
        if (parameter == null) {
            parameter = new UIParameter();
            parameter.setName(PARAM_NAME);
            parameter.setValue(UUID.randomUUID().toString());
            parent.getChildren().add(parameter);
        }
        return (String) parameter.getValue();
    }

    private AjaxBehavior createAjaxBehavior(FaceletContext ctx, String eventName) {
        Application application = ctx.getFacesContext().getApplication();
        AjaxBehavior behavior = (AjaxBehavior) application.createBehavior(AjaxBehavior.BEHAVIOR_ID);

        setBehaviorAttribute(ctx, behavior, new TagAttributeImpl(targetClass.getLocation(), targetClass.getNamespace(), "onevent", "onevent", JS_FUNCTION), String.class);

        setBehaviorAttribute(ctx, behavior, this.onerror, String.class);
        setBehaviorAttribute(ctx, behavior, this.disabled, Boolean.class);
        setBehaviorAttribute(ctx, behavior, this.immediate, Boolean.class);
        setBehaviorAttribute(ctx, behavior, this.resetValues, Boolean.class);
        setBehaviorAttribute(ctx, behavior, this.execute, Object.class);
        setBehaviorAttribute(ctx, behavior, this.render, Object.class);
        setBehaviorAttribute(ctx, behavior, this.delay, String.class);

        if (null != listener) {
            behavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpll(
                    this.listener.getMethodExpression(ctx, Object.class, new Class[]{AjaxBehaviorEvent.class}),
                    this.listener.getMethodExpression(ctx, Object.class, new Class[]{})));
        }

        return behavior;
    }

    private void setBehaviorAttribute(FaceletContext ctx, AjaxBehavior behavior, TagAttribute attr, Class type) {
        if (attr != null) {
            behavior.setValueExpression(attr.getLocalName(), attr.getValueExpression(ctx, type));
        }
    }

    private void installResourceIfNecessary() {

        FacesContext context = FacesContext.getCurrentInstance();

        if (RequestStateManager.containsKey(context, SCRIPT_STATE_BLUR)) return;

        check(context, RESOURCE_NAME_JSF,        LIB_NAME_JSF,    RENDER_TYPE_SCRIPT);
        check(context, RESOURCE_NAME_LAVASH_JS,  LIB_NAME_LAVASH, RENDER_TYPE_SCRIPT);
        check(context, RESOURCE_NAME_LAVASH_CSS, LIB_NAME_LAVASH, RENDER_TYPE_STYLE);

        RequestStateManager.set(context, SCRIPT_STATE_BLUR, Boolean.TRUE);

    }

    private void check(final FacesContext context, final String name, final String library, final String renderType) {

        if (isInstalled(context, name, library)) return;
        if (isRendered(context, name, library)) return;

        UIOutput output = new UIOutput();
        output.setRendererType(renderType);
        output.getAttributes().put("name", name);
        output.getAttributes().put("library", library);
        context.getViewRoot().addComponentResource(context, output, "head");
    }

    private boolean isRendered(FacesContext context, String _name, String _library) {
        String resourceIdentifier = _library + ":" + _name;
        Set<String> resourceIdentifiers = (Set<String>) context.getAttributes().get("/javax.faces.resource");
        return resourceIdentifiers != null && resourceIdentifiers.contains(resourceIdentifier);
    }

    private boolean isInstalled(FacesContext context, String _name, String _library) {
        UIViewRoot viewRoot = context.getViewRoot();
        List<UIComponent> resources = new ArrayList<>();
        resources.addAll(viewRoot.getComponentResources(context, "head"));
        resources.addAll(viewRoot.getComponentResources(context, "form"));
        resources.addAll(viewRoot.getComponentResources(context, "body"));
        for (UIComponent resource : resources) {
            Object name = resource.getAttributes().get("name");
            Object library = resource.getAttributes().get("library");
            if (_name.equals(name) && _library.equals(library)) return true;
        }
        return false;
    }

    private String getUnsupportedEventMessage(String eventName, Collection<String> eventNames, UIComponent parent) {
        StringBuilder builder = new StringBuilder(100);
        builder.append("'");
        builder.append(eventName);
        builder.append("' is not a supported event for ");
        builder.append(parent.getClass().getSimpleName());
        builder.append(".  Please specify one of these supported event names: ");
        Collection<String> sortedEventNames = new TreeSet<String>(eventNames);
        Iterator<String> iter = sortedEventNames.iterator();
        boolean hasNext = iter.hasNext();
        while (hasNext) {
            builder.append(iter.next());
            hasNext = iter.hasNext();
            if (hasNext) {
                builder.append(", ");
            }
        }
        builder.append(".");
        return builder.toString();
    }
}


class AjaxBehaviorListenerImpll implements AjaxBehaviorListener, Serializable {

    private MethodExpression oneArgListener;
    private MethodExpression noArgListener;

    public AjaxBehaviorListenerImpll() {}

    public AjaxBehaviorListenerImpll(MethodExpression oneArg, MethodExpression noArg) {
        this.oneArgListener = oneArg;
        this.noArgListener = noArg;
    }

    public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
        final ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        try {
            noArgListener.invoke(elContext, new Object[]{});
        } catch (MethodNotFoundException mnfe) {
            oneArgListener.invoke(elContext, new Object[]{event});
        } catch (IllegalArgumentException iae) {
            oneArgListener.invoke(elContext, new Object[]{event});
        }
    }
}