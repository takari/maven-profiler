package io.tesla.lifecycle.profiler;

public class Timer {
  public static final int MS_PER_SEC = 1000;
  public static final int SEC_PER_MIN = 60;
  private long start;
  private long time;

  public Timer() {
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

  public static String formatTime(long ms) {
    long secs = ms / MS_PER_SEC;
    long mins = secs / SEC_PER_MIN;
    secs = secs % SEC_PER_MIN;
    long fractionOfASecond = ms - (secs * 1000);

      StringBuilder msg = new StringBuilder();
      if (mins > 0)
      {
          msg.append(mins);
          msg.append("m ");
      }
      if (secs > 0)
      {
          msg.append(secs);
          msg.append("s");
      }

      if ( mins == 0)
      {
          if (msg.length() > 0 ) msg.append(" ");
          msg.append(fractionOfASecond);
          msg.append("ms");
      }

      return msg.toString();
  }
}