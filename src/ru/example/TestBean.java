package ru.example;

import ru.electrolavash.LogMessaging;

import javax.faces.bean.ManagedBean;

@ManagedBean(name = "test")
public class TestBean extends LogMessaging {

    private static final long PERIOD = 4000L;
    private static final long DELAY = 1000L;

    private String title;

    public void startLongTask() {
        System.out.println("! BEGIN");
        try {
            final long endTime = System.currentTimeMillis() + PERIOD;
            while (true) {
                final long stamp = System.currentTimeMillis();
                System.out.println("# : " + stamp);
                alert("# : " + stamp);
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

}
