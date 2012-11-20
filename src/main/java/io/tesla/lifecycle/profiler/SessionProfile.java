package io.tesla.lifecycle.profiler;

import io.tesla.lifecycle.profiler.internal.DefaultTimer;

import java.util.ArrayList;
import java.util.List;

public class SessionProfile extends Profile {

  private List<ProjectProfile> projectProfiles;
  
  public SessionProfile() {
    super(new DefaultTimer());
    this.projectProfiles = new ArrayList<ProjectProfile>();
  }
  
  public void addProjectProfile(ProjectProfile projectProfile) {
    projectProfiles.add(projectProfile);
  }

  public List<ProjectProfile> getProjectProfiles() {
    return projectProfiles;
  }
}
