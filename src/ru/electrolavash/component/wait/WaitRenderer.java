package ru.electrolavash.component.wait;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import java.io.IOException;

public class WaitRenderer extends Renderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        System.out.println("[WaitRenderer] <-- decode");
        super.decode(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        System.out.println("[WaitRenderer] --> encodeEnd");
        final ResponseWriter writer = context.getResponseWriter();
        //writer.startElement();

    }
}
