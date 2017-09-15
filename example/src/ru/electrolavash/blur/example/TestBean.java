package ru.electrolavash.blur.example;

import ru.electrolavash.blur.LoggingBean;

import javax.faces.bean.ManagedBean;

@ManagedBean(name = "test")
public class TestBean extends LoggingBean {

    private static final long PERIOD = 3000L;
    private static final long DELAY = 400L;

    public void startLongTask() {
        try {
            final long endTime = System.currentTimeMillis() + PERIOD;
            while (true) {
                final long stamp = System.currentTimeMillis();
                alert(String.format("Info: %d", stamp));
                Thread.sleep(DELAY);
                if (System.currentTimeMillis() >= endTime) break;
            }
        } catch (InterruptedException e) {
            alert(String.format("Error: %s", e.getMessage()));
        }
    }

}
