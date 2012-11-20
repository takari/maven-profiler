package io.tesla.lifecycle.profiler;

import io.tesla.lifecycle.profiler.internal.DefaultTimer;

public class Profile {
  
  protected long elapsedTime;
  protected Timer timer;
    
  protected Profile(DefaultTimer timer) {
    this.timer = timer;    
  }
    
  public void stop() {
    timer.stop();
  }
  
  void setElapsedTime(long elapsedTime) {
    this.elapsedTime = elapsedTime;
  }
  
  public long getElapsedTime() {
    if(elapsedTime != 0) {
      return elapsedTime;
    }
    return timer.getTime();
  }
}
