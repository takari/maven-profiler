package io.tesla.lifecycle.profiler;

public class Profile {
  
  protected long elapsedTime;
  protected Timer timer;
    
  protected Profile(Timer timer) {
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
