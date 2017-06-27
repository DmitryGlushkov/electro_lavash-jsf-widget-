package ru.electrolavash.component;

public class Wait extends javax.faces.component.UIComponentBase {

    public static final String COMPONENT_TYPE = "ru.electrolavash.component.Wait";
    public static final String COMPONENT_FAMILY = "ru.electrolavash.component";
    public static final String DEFAULT_RENDERER = "ru.electrolavash.component.WaitRenderer";

    public Wait(){
        System.out.println("Wait");
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
