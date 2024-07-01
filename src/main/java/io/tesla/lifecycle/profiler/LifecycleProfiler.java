/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;

// pom deserialization
// app dependency download
// plugin dependency download
// mojo execution

/**
 * @author Jason van Zyl
 */
@Named
@Singleton
public class LifecycleProfiler extends AbstractEventSpy {

    private static final String TESLA_PROFILE = "maven.profile";

    //
    // Components
    //
    private SessionProfileRenderer renderer;

    //
    // Profile data
    //
    private SessionProfile sessionProfile;
    private ProjectProfile projectProfile;
    private PhaseProfile phaseProfile;
    private MojoProfile mojoProfile;

    @Inject
    public LifecycleProfiler(SessionProfileRenderer sessionProfileRenderer) {
        this.renderer = sessionProfileRenderer;
    }

    @Override
    public void init(Context context) throws Exception {}

    @Override
    public void onEvent(Object event) throws Exception {
        if (event instanceof ExecutionEvent) {
            ExecutionEvent executionEvent = (ExecutionEvent) event;
            if (executionEvent.getType() == ExecutionEvent.Type.SessionStarted) {
                //
                //
                //
                sessionProfile = new SessionProfile();
            } else if (executionEvent.getType() == ExecutionEvent.Type.SessionEnded) {
                //
                //
                //
                sessionProfile.stop();
                if (System.getProperty(TESLA_PROFILE) != null) {
                    renderer.render(sessionProfile);
                }
            } else if (executionEvent.getType() == ExecutionEvent.Type.ProjectStarted) {
                //
                // We need to collect the mojoExecutions within each project
                //
                projectProfile = new ProjectProfile(executionEvent.getProject());
            } else if (executionEvent.getType() == ExecutionEvent.Type.ProjectSucceeded
                    || executionEvent.getType() == ExecutionEvent.Type.ProjectFailed) {
                //
                //
                //
                if (phaseProfile != null) {
                    phaseProfile.stop();
                    projectProfile.addPhaseProfile(phaseProfile);
                    phaseProfile = null;
                }
                projectProfile.stop();
                sessionProfile.addProjectProfile(projectProfile);
            } else if (executionEvent.getType() == ExecutionEvent.Type.MojoStarted) {
                String phase = executionEvent.getMojoExecution().getLifecyclePhase();
                //
                // Create a new phase profile if one doesn't exist or the phase has changed.
                //
                if (phaseProfile == null) {
                    phaseProfile = new PhaseProfile(phase);
                } else if (!phaseProfile.getPhase().equals(phase)) {
                    phaseProfile.stop();
                    projectProfile.addPhaseProfile(phaseProfile);
                    phaseProfile = new PhaseProfile(phase);
                }
                mojoProfile = new MojoProfile(executionEvent.getMojoExecution());
            } else if (executionEvent.getType() == ExecutionEvent.Type.MojoSucceeded
                    || executionEvent.getType() == ExecutionEvent.Type.MojoFailed) {
                //
                //
                //
                mojoProfile.stop();
                phaseProfile.addMojoProfile(mojoProfile);
            }
        }
    }
}
