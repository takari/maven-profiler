package io.tesla.lifecycle.profiler.internal;

import io.tesla.lifecycle.profiler.MojoProfile;
import io.tesla.lifecycle.profiler.PhaseProfile;
import io.tesla.lifecycle.profiler.ProjectProfile;
import io.tesla.lifecycle.profiler.SessionProfile;
import io.tesla.lifecycle.profiler.SessionProfileRenderer;
import io.tesla.lifecycle.profiler.Timer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class DefaultSessionProfileRenderer implements SessionProfileRenderer {

  private Timer timer;
  
  @Inject
  public DefaultSessionProfileRenderer(Timer timer) {
    this.timer = timer;
  }
  
  public void render(SessionProfile sessionProfile) {
    
    for(ProjectProfile pp : sessionProfile.getProjectProfiles()) {
      render("");
      render(pp.getProjectName());
      render("");
      for(PhaseProfile phaseProfile : pp.getPhaseProfile()) {
        render("  " + phaseProfile.getPhase() + " " + timer.format(phaseProfile.getElapsedTime()));
        for(MojoProfile mp : phaseProfile.getMojoProfiles()) {
          render("    " + mp.getId() + timer.format(mp.getElapsedTime())); 
        }
        render("");
      }
    }
  }
  
  private void render(String s) {
    System.out.println(s);
  }
}
