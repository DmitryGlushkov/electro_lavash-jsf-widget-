package ru.example;

import com.sun.faces.facelets.tag.jsf.core.AjaxHandler;
import com.sun.xml.internal.ws.client.RequestContext;

import javax.faces.bean.ManagedBean;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@ManagedBean(name = "test")
public class TestBean {

    private static final long PERIOD = 6000L;
    private static final long DELAY = 1000L;

    private String title;

    public void startLongTask() {

        final Thread longTask = new Thread(() -> {
            System.out.println("! BEGIN");
            try {
                final long endTime = System.currentTimeMillis() + PERIOD;
                while (true) {
                    final long stamp = System.currentTimeMillis();
                    System.out.println("# : " + stamp);
                    Thread.sleep(DELAY);
                    if (System.currentTimeMillis() > endTime) break;
                }
            } catch (InterruptedException e) {
                System.out.println("InterruptedException");
            }
            System.out.println("! END");
        });
        longTask.start();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void processAjaxBehavior(javax.faces.event.AjaxBehaviorEvent event) throws javax.faces.event.AbortProcessingException {
        System.out.println(1);
    }
}
