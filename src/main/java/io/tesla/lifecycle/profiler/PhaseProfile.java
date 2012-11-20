package io.tesla.lifecycle.profiler;

import io.tesla.lifecycle.profiler.internal.DefaultTimer;

import java.util.ArrayList;
import java.util.List;

public class PhaseProfile extends Profile {

  private String phase;
  private List<MojoProfile> mojoProfiles;
  
  public PhaseProfile(String phase) {
    super(new DefaultTimer());
    this.phase = phase;
    this.mojoProfiles = new ArrayList<MojoProfile>();
  }
  
  public void addMojoProfile(MojoProfile mojoProfile) {
    mojoProfiles.add(mojoProfile);
  }

  public String getPhase() {
    return phase;
  }

  public List<MojoProfile> getMojoProfiles() {
    return mojoProfiles;
  }
}
