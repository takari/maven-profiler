/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler.internal;

import io.tesla.lifecycle.profiler.MojoProfile;
import io.tesla.lifecycle.profiler.PhaseProfile;
import io.tesla.lifecycle.profiler.ProjectProfile;
import io.tesla.lifecycle.profiler.SessionProfile;

// old implementation that has been replaced by AdvancedSessionProfileRenderer. Could actually be removed.
//@Named
//@Singleton
public class DefaultSessionProfileRenderer extends AbstractSessionProfileRenderer {

  public DefaultSessionProfileRenderer() {
    super();
  }

  public void render(SessionProfile sessionProfile) {

    if (!this.logProfileData) {
      return;
    }
    for(ProjectProfile pp : sessionProfile.getProjectProfiles()) {
      render("");
      render(pp.getProjectName());
      render("");
      for(PhaseProfile phaseProfile : pp.getPhaseProfile()) {
        render("  " + phaseProfile.getPhase() + " " + DefaultTimer.formatMilliseconds(phaseProfile.getElapsedTime()));
        for(MojoProfile mp : phaseProfile.getMojoProfiles()) {
          render("    " + mp.getId() + DefaultTimer.formatMilliseconds(mp.getElapsedTime()));
        }
        render("");
      }
    }
  }

  private void render(String s) {
    System.out.println(s);
  }
}
