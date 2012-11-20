package io.tesla.lifecycle.profiler.internal;

import io.tesla.lifecycle.profiler.Timer;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class DefaultTimer implements Timer {
  
  public static final int MS_PER_SEC = 1000;
  public static final int SEC_PER_MIN = 60;
  private long start;
  private long time;

  public DefaultTimer() {
    start = System.currentTimeMillis();
  }

  public void stop() {
    time = elapsedTime();
  }

  public long getTime() {
    return time;
  }

  private long elapsedTime() {
    return System.currentTimeMillis() - start;
  }

  public String format(long ms) {
    long secs = ms / MS_PER_SEC;
    long mins = secs / SEC_PER_MIN;
    secs = secs % SEC_PER_MIN;
    long fractionOfASecond = ms - (secs * 1000);

    StringBuilder msg = new StringBuilder();
    if (mins > 0) {
      msg.append(mins);
      msg.append("m ");
    }
    if (secs > 0) {
      msg.append(secs);
      msg.append("s");
    }

    if (mins == 0) {
      if (msg.length() > 0)
        msg.append(" ");
      msg.append(fractionOfASecond);
      msg.append("ms");
    }

    return msg.toString();
  }
}
