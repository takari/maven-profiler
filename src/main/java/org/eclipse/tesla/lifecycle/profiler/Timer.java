package org.eclipse.tesla.lifecycle.profiler;

public class Timer {
  public static final int MS_PER_SEC = 1000;
  public static final int SEC_PER_MIN = 60;
  private long start;
  private long time;

  public Timer() {
    start = System.currentTimeMillis();
  }

  public void start() {
    start = System.currentTimeMillis();
  }

  public void stop() {
    time = elapsedTime();
  }

  public long getTime() {
    return time;
  }

  public long elapsedTime() {
    return System.currentTimeMillis() - start;
  }

  public static String formatTime(long ms) {
    long secs = ms / MS_PER_SEC;
    secs = secs % SEC_PER_MIN;
    long mms = ms - (secs * 1000);

    String msg = secs + "." + mms;
    if (msg.length() == 3) {
      msg += "00s";
    } else if (msg.length() == 4) {
      msg += "0s";
    } else {
      msg += "s";
    }

    return msg;
  }
}