package io.tesla.lifecycle.profiler.internal;

import io.tesla.lifecycle.profiler.Timer;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class DefaultTimer implements Timer 
{
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
//    System.out.println("mins " + mins);
//    System.out.println("secs " + secs);
//    System.out.println(fractionOfASecond);
//    System.out.println(">> " + fractionOfASecond);
    
    String msg = mins + "m " + secs + "." + fractionOfASecond;

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