package io.tesla.lifecycle.profiler;

public interface Timer {

  void stop();
  long getTime();
  String format(long elapsedTime);
}
