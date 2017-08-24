package ru.example;

import ru.electrolavash.LogMessaging;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "test")
public class TestBean extends LogMessaging {

    private static final long PERIOD = 3000L;
    private static final long DELAY = 400L;

    public void startLongTask() {
        try {
            final long endTime = System.currentTimeMillis() + PERIOD;
            while (true) {
                final long stamp = System.currentTimeMillis();
                alert("# : " + stamp);
                Thread.sleep(DELAY);
                if (System.currentTimeMillis() > endTime) break;
            }
        } catch (InterruptedException e) {
            System.out.println("InterruptedException");
        }
    }

}
