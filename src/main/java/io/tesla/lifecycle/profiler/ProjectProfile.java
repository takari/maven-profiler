package io.tesla.lifecycle.profiler;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;

public class ProjectProfile extends Profile {

  private MavenProject project;
  private List<PhaseProfile> phaseProfiles;
  
  public ProjectProfile(MavenProject project) {
    super(new Timer());
    this.project = project;
    this.phaseProfiles = new ArrayList<PhaseProfile>();
  }
  
  public void addPhaseProfile(PhaseProfile phaseProfile) {
    phaseProfiles.add(phaseProfile);
  }

  public String getProjectName() {
    return project.getGroupId() + ":" + project.getArtifactId() + ":" + project.getVersion();
  }

  public List<PhaseProfile> getPhaseProfile() {
    return phaseProfiles;
  }
}
