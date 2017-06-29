package ru.electrolavash.component.wait;

public class Wait extends javax.faces.component.UIComponentBase {

    public static final String COMPONENT_TYPE = "ru.electrolavash.component.wait.Wait";
    public static final String COMPONENT_FAMILY = "ru.electrolavash.component";
    public static final String DEFAULT_RENDERER = "ru.electrolavash.component.wait.WaitRenderer";

    public Wait(){
        System.out.println(" (constructor) wait ");
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getRendererType() {
        return DEFAULT_RENDERER;
    }

    public static String getComponentType() {
        return COMPONENT_TYPE;
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
}
