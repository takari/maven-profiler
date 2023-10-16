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

public class PhaseProfile extends AbstractTimerProfile {

  private String phase;
  private List<MojoProfile> mojoProfiles;

  public PhaseProfile(String phase) {
    super();
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

  @Override
  public String getName() {

    return phase;
  }

  @Override
  public List<? extends Profile> getChildren() {

    return getMojoProfiles();
  }
}
