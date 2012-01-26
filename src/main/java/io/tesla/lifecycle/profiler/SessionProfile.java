package io.tesla.lifecycle.profiler;

import java.util.ArrayList;
import java.util.List;

public class SessionProfile extends Profile {

  private List<ProjectProfile> projectProfiles;
  
  public SessionProfile() {
    super(new Timer());
    this.projectProfiles = new ArrayList<ProjectProfile>();
  }
  
  public void addProjectProfile(ProjectProfile projectProfile) {
    projectProfiles.add(projectProfile);
  }

  public List<ProjectProfile> getProjectProfiles() {
    return projectProfiles;
  }
}
