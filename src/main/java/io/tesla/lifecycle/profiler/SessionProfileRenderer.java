package io.tesla.lifecycle.profiler;

public class SessionProfileRenderer {

  public void render(SessionProfile sessionProfile) {
    
    for(ProjectProfile pp : sessionProfile.getProjectProfiles()) {
      render(pp.getProjectName());
      for(PhaseProfile phaseProfile : pp.getPhaseProfile()) {
        render("  " + phaseProfile.getPhase() + " " + Timer.formatTime(phaseProfile.getElapsedTime()));
        for(MojoProfile mp : phaseProfile.getMojoProfiles()) {
          render("    " + mp.getId() + Timer.formatTime(mp.getElapsedTime())); 
        }
      }
      render("");
    }
  }
  
  private void render(String s) {
    System.out.println(s);
  }
}
