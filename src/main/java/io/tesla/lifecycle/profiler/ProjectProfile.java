/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import io.tesla.lifecycle.profiler.internal.DefaultTimer;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;

public class ProjectProfile extends Profile {

  private MavenProject project;
  private List<PhaseProfile> phaseProfiles;
  
  public ProjectProfile(MavenProject project) {
    super(new DefaultTimer());
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
