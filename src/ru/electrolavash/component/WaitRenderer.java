package ru.electrolavash.component;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;
import java.io.IOException;

public class WaitRenderer extends Renderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        System.out.println("decode");
        super.decode(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        System.out.println("encodeBegin");
        super.encodeBegin(context, component);

    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        System.out.println("encodeEnd");
        super.encodeEnd(context, component);

    }
}
