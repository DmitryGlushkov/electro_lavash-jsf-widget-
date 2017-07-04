package ru.example;

import javax.faces.bean.ManagedBean;

@ManagedBean(name = "test")
public class TestBean {

    private static final long PERIOD = 1000L;
    private static final long DELAY = 1000L;

    private String title;

    public void startLongTask() {
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
