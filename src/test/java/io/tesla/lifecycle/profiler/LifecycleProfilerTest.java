/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.inject.Inject;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.sisu.launch.InjectedTestCase;

public class LifecycleProfilerTest extends InjectedTestCase {

    @Inject
    private SessionProfileRenderer sessionProfileRenderer;

    public void testSessionProfile() {

        SessionProfile s = new SessionProfile("mvn");

        ProjectProfile p0 = new ProjectProfile(project("g0", "a0", "v0"));
        PhaseProfile ph0 = new PhaseProfile("phase0");
        MojoProfile m0 = new MojoProfile(mojoExecution("goal0", "m0"));
        m0.setElapsedTime(3000);
        ph0.addMojoProfile(m0);
        MojoProfile m00 = new MojoProfile(mojoExecution("goal00", "m00"));
        m00.setElapsedTime(5492009);
        ph0.addMojoProfile(m00);
        p0.addPhaseProfile(ph0);
        s.addProjectProfile(p0);

        ProjectProfile p1 = new ProjectProfile(project("g1", "a1", "v1"));
        PhaseProfile ph1 = new PhaseProfile("phase1");
        MojoProfile m1 = new MojoProfile(mojoExecution("goal1", "m1"));
        m1.setElapsedTime(2500);
        ph1.addMojoProfile(m1);
        p1.addPhaseProfile(ph1);
        s.addProjectProfile(p1);

        ProjectProfile p2 = new ProjectProfile(project("g2", "a2", "v2"));
        PhaseProfile ph2 = new PhaseProfile("phase2");
        MojoProfile m2 = new MojoProfile(mojoExecution("goal2", "m2"));
        m2.setElapsedTime(5000);
        ph2.addMojoProfile(m2);
        p2.addPhaseProfile(ph2);
        s.addProjectProfile(p2);

        sessionProfileRenderer.render(s);
    }

    public void testParallelProjectsKeepIndependentProfiles() throws Exception {
        String oldProfile = System.getProperty("maven.profile");
        System.setProperty("maven.profile", "true");
        try {
            CapturingRenderer renderer = new CapturingRenderer();
            LifecycleProfiler profiler = new LifecycleProfiler(renderer);
            MavenProject firstProject = project("g", "first", "1");
            MavenProject secondProject = project("g", "second", "1");
            MavenSession session = session(firstProject, secondProject);
            profiler.onEvent(event(ExecutionEvent.Type.SessionStarted, session, null, null));

            CyclicBarrier barrier = new CyclicBarrier(2);
            ExecutorService executor = Executors.newFixedThreadPool(2);
            try {
                Future<?> first = executor.submit(() -> profileProject(
                        profiler,
                        session,
                        firstProject,
                        mojoExecution("first-goal", "first-execution", "test"),
                        barrier));
                Future<?> second = executor.submit(() -> profileProject(
                        profiler,
                        session,
                        secondProject,
                        mojoExecution("second-goal", "second-execution", "integration-test"),
                        barrier));
                first.get();
                second.get();
            } finally {
                executor.shutdownNow();
            }

            profiler.onEvent(event(ExecutionEvent.Type.SessionEnded, session, null, null));

            assertNotNull(renderer.profile);
            assertEquals(2, renderer.profile.getProjectProfiles().size());
            assertProjectProfile(renderer.profile.getProjectProfiles().get(0), "g:first:1", "test", "first-execution");
            assertProjectProfile(
                    renderer.profile.getProjectProfiles().get(1), "g:second:1", "integration-test", "second-execution");
        } finally {
            restoreProperty("maven.profile", oldProfile);
        }
    }

    public void testTimelineRecordsRawEventsAndReactorDependenciesWithoutAggregateProfile() throws Exception {
        String oldProfile = System.getProperty("maven.profile");
        System.clearProperty("maven.profile");
        Path timeline = Files.createTempFile("maven-profiler-timeline", ".jsonl");
        try {
            CapturingRenderer renderer = new CapturingRenderer();
            LifecycleProfiler profiler = new LifecycleProfiler(renderer, new TimelineRecorder(timeline));
            MavenProject firstProject = project("g", "first", "1");
            MavenProject secondProject = project("g", "second", "1");
            MavenSession session = session(firstProject, secondProject);
            session.setProjectDependencyGraph(graph(firstProject, secondProject));
            MojoExecution mojo = mojoExecution("test", "default-test", "test");

            profiler.init(null);
            profiler.onEvent(event(ExecutionEvent.Type.SessionStarted, session, null, null));
            profiler.onEvent(event(ExecutionEvent.Type.ProjectStarted, session, secondProject, null));
            profiler.onEvent(event(ExecutionEvent.Type.MojoStarted, session, secondProject, mojo));
            profiler.onEvent(event(ExecutionEvent.Type.MojoSucceeded, session, secondProject, mojo));
            profiler.onEvent(event(ExecutionEvent.Type.ProjectSucceeded, session, secondProject, null));
            profiler.onEvent(event(ExecutionEvent.Type.SessionEnded, session, null, null));
            profiler.close();

            List<String> lines = Files.readAllLines(timeline, StandardCharsets.UTF_8);
            assertEquals(8, lines.size());
            assertTrue(lines.get(0).contains("\"event\":\"SessionStarted\""));
            assertTrue(lines.get(1).contains("\"project\":\"g:first:1\""));
            assertTrue(lines.get(2).contains("\"project\":\"g:second:1\""));
            assertTrue(lines.get(2).contains("\"upstreamProjects\":[\"g:first:1\"]"));
            assertTrue(lines.get(4).contains("\"event\":\"MojoStarted\""));
            assertTrue(lines.get(4).contains("\"executionId\":\"default-test\""));
            assertTrue(lines.get(4).contains("\"lifecyclePhase\":\"test\""));
            assertNull(renderer.profile);
        } finally {
            Files.deleteIfExists(timeline);
            restoreProperty("maven.profile", oldProfile);
        }
    }

    public void testTimelineCanBeDisabledExplicitly() {
        String oldTimeline = System.getProperty(TimelineRecorder.TIMELINE_PROPERTY);
        System.setProperty(TimelineRecorder.TIMELINE_PROPERTY, "false");
        try {
            assertFalse(new TimelineRecorder().isEnabled());
        } finally {
            restoreProperty(TimelineRecorder.TIMELINE_PROPERTY, oldTimeline);
        }
    }

    protected MavenProject project(String g, String a, String v) {
        MavenProject p = new MavenProject();
        p.setGroupId(g);
        p.setArtifactId(a);
        p.setVersion(v);
        return p;
    }

    protected MojoExecution mojoExecution(String goal, String executionId) {
        return mojoExecution(goal, executionId, null);
    }

    protected MojoExecution mojoExecution(String goal, String executionId, String lifecyclePhase) {
        Plugin p = new Plugin();
        p.setGroupId("groupId");
        p.setArtifactId("artifactId");
        p.setVersion("version");
        MojoExecution me = new MojoExecution(p, goal, executionId);
        me.setLifecyclePhase(lifecyclePhase);
        return me;
    }

    private static MavenSession session(MavenProject... projects) {
        DefaultMavenExecutionRequest request = new DefaultMavenExecutionRequest();
        request.setGoals(Collections.singletonList("verify"));
        return new MavenSession(null, request, new DefaultMavenExecutionResult(), Arrays.asList(projects));
    }

    private static ProjectDependencyGraph graph(MavenProject firstProject, MavenProject secondProject) {
        List<MavenProject> projects = Arrays.asList(firstProject, secondProject);
        return new ProjectDependencyGraph() {
            @Override
            public List<MavenProject> getAllProjects() {
                return projects;
            }

            @Override
            public List<MavenProject> getSortedProjects() {
                return projects;
            }

            @Override
            public List<MavenProject> getDownstreamProjects(MavenProject project, boolean transitive) {
                return project == firstProject ? Collections.singletonList(secondProject) : Collections.emptyList();
            }

            @Override
            public List<MavenProject> getUpstreamProjects(MavenProject project, boolean transitive) {
                return project == secondProject ? Collections.singletonList(firstProject) : Collections.emptyList();
            }
        };
    }

    private static void profileProject(
            LifecycleProfiler profiler,
            MavenSession session,
            MavenProject project,
            MojoExecution mojo,
            CyclicBarrier barrier) {
        try {
            profiler.onEvent(event(ExecutionEvent.Type.ProjectStarted, session, project, null));
            barrier.await();
            profiler.onEvent(event(ExecutionEvent.Type.MojoStarted, session, project, mojo));
            barrier.await();
            profiler.onEvent(event(ExecutionEvent.Type.MojoSucceeded, session, project, mojo));
            barrier.await();
            profiler.onEvent(event(ExecutionEvent.Type.ProjectSucceeded, session, project, null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void assertProjectProfile(
            ProjectProfile projectProfile, String projectName, String phase, String executionId) {
        assertEquals(projectName, projectProfile.getProjectName());
        assertEquals(1, projectProfile.getPhaseProfile().size());
        PhaseProfile phaseProfile = projectProfile.getPhaseProfile().get(0);
        assertEquals(phase, phaseProfile.getPhase());
        assertEquals(1, phaseProfile.getMojoProfiles().size());
        assertTrue(phaseProfile.getMojoProfiles().get(0).getId().contains("(" + executionId + ")"));
    }

    private static ExecutionEvent event(
            ExecutionEvent.Type type, MavenSession session, MavenProject project, MojoExecution mojoExecution) {
        return new ExecutionEvent() {
            @Override
            public Type getType() {
                return type;
            }

            @Override
            public MavenSession getSession() {
                return session;
            }

            @Override
            public MavenProject getProject() {
                return project;
            }

            @Override
            public MojoExecution getMojoExecution() {
                return mojoExecution;
            }

            @Override
            public Exception getException() {
                return null;
            }
        };
    }

    private static void restoreProperty(String name, String value) {
        if (value == null) {
            System.clearProperty(name);
        } else {
            System.setProperty(name, value);
        }
    }

    private static final class CapturingRenderer implements SessionProfileRenderer {
        private SessionProfile profile;

        @Override
        public void render(SessionProfile sessionProfile) {
            profile = sessionProfile;
        }
    }
}
