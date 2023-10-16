/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import java.util.ArrayList;
import java.util.List;

public class SessionProfile extends AbstractTimerProfile {

  private final String name;

  private List<ProjectProfile> projectProfiles;

  public SessionProfile(String name) {
    super();
    this.name = name;
    this.projectProfiles = new ArrayList<>();
  }

  public void addProjectProfile(ProjectProfile projectProfile) {
    projectProfiles.add(projectProfile);
  }

  public List<ProjectProfile> getProjectProfiles() {
    return projectProfiles;
  }

  @Override
  public String getName() {

    return this.name;
  }

  @Override
  public List<? extends Profile> getChildren() {

    return getProjectProfiles();
  }
}
