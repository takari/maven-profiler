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
