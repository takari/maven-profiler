/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;

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

    private static final String MAVEN_PROFILE = "maven.profile";

    private final SessionProfileRenderer renderer;
    private final TimelineRecorder timelineRecorder;

    private final boolean profileEnabled;
    private final boolean disabled;

    //
    // Profile data
    //
    private volatile SessionProfile sessionProfile;
    private final Map<MavenProject, ProjectProfileState> projectProfiles = new ConcurrentHashMap<>();

    @Inject
    public LifecycleProfiler(SessionProfileRenderer sessionProfileRenderer) {
        this(sessionProfileRenderer, new TimelineRecorder());
    }

    LifecycleProfiler(SessionProfileRenderer sessionProfileRenderer, TimelineRecorder timelineRecorder) {
        super();
        this.renderer = sessionProfileRenderer;
        this.timelineRecorder = timelineRecorder;
        this.profileEnabled = (System.getProperty(MAVEN_PROFILE) != null);
        this.disabled = !profileEnabled && !timelineRecorder.isEnabled();
    }

    @Override
    public void init(Context context) throws Exception {
        timelineRecorder.init();
    }

    @Override
    public void onEvent(Object event) throws Exception {
        if (this.disabled) {
            return;
        }
        if (event instanceof ExecutionEvent) {
            ExecutionEvent executionEvent = (ExecutionEvent) event;
            timelineRecorder.record(executionEvent);
            if (!profileEnabled) {
                return;
            }
            if (executionEvent.getType() == ExecutionEvent.Type.SessionStarted) {
                //
                //
                //
                StringBuilder command = new StringBuilder("mvn");
                for (String goal : executionEvent.getSession().getGoals()) {
                    command.append(' ');
                    command.append(goal);
                }
                sessionProfile = new SessionProfile(command.toString());
                projectProfiles.clear();
            } else if (executionEvent.getType() == ExecutionEvent.Type.SessionEnded) {
                //
                //
                //
                finishSession(executionEvent);
            } else if (executionEvent.getType() == ExecutionEvent.Type.ProjectStarted) {
                //
                // We need to collect the mojoExecutions within each project
                //
                projectProfiles.put(executionEvent.getProject(), new ProjectProfileState(executionEvent.getProject()));
            } else if (executionEvent.getType() == ExecutionEvent.Type.ProjectSucceeded
                    || executionEvent.getType() == ExecutionEvent.Type.ProjectFailed) {
                projectState(executionEvent).finish();
            } else if (executionEvent.getType() == ExecutionEvent.Type.MojoStarted) {
                projectState(executionEvent).mojoStarted(executionEvent);
            } else if (executionEvent.getType() == ExecutionEvent.Type.MojoSucceeded
                    || executionEvent.getType() == ExecutionEvent.Type.MojoFailed) {
                projectState(executionEvent).mojoFinished();
            }
        }
    }

    @Override
    public void close() throws Exception {
        timelineRecorder.close();
    }

    private ProjectProfileState projectState(ExecutionEvent event) {
        return projectProfiles.computeIfAbsent(event.getProject(), ProjectProfileState::new);
    }

    private void finishSession(ExecutionEvent event) {
        sessionProfile.stop();
        for (MavenProject project : event.getSession().getProjects()) {
            ProjectProfileState state = projectProfiles.remove(project);
            if (state != null) {
                state.finish();
                sessionProfile.addProjectProfile(state.projectProfile);
            }
        }

        List<ProjectProfileState> remaining = new ArrayList<>(projectProfiles.values());
        remaining.sort(Comparator.comparing(state -> state.projectProfile.getProjectName()));
        for (ProjectProfileState state : remaining) {
            state.finish();
            sessionProfile.addProjectProfile(state.projectProfile);
        }
        projectProfiles.clear();
        renderer.render(sessionProfile);
    }

    private static final class ProjectProfileState {
        private final ProjectProfile projectProfile;
        private PhaseProfile phaseProfile;
        private MojoProfile mojoProfile;
        private boolean finished;

        private ProjectProfileState(MavenProject project) {
            projectProfile = new ProjectProfile(project);
        }

        private synchronized void mojoStarted(ExecutionEvent event) {
            String phase = event.getMojoExecution().getLifecyclePhase();
            if (phaseProfile == null || !Objects.equals(phaseProfile.getPhase(), phase)) {
                finishPhase();
                phaseProfile = new PhaseProfile(phase);
            }
            mojoProfile = new MojoProfile(event.getMojoExecution());
        }

        private synchronized void mojoFinished() {
            if (mojoProfile != null && phaseProfile != null) {
                mojoProfile.stop();
                phaseProfile.addMojoProfile(mojoProfile);
                mojoProfile = null;
            }
        }

        private synchronized void finish() {
            if (finished) {
                return;
            }
            mojoFinished();
            finishPhase();
            projectProfile.stop();
            finished = true;
        }

        private void finishPhase() {
            if (phaseProfile != null) {
                phaseProfile.stop();
                projectProfile.addPhaseProfile(phaseProfile);
                phaseProfile = null;
            }
        }
    }
}
