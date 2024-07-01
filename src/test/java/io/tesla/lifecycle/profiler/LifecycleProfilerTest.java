/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import javax.inject.Inject;
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

    protected MavenProject project(String g, String a, String v) {
        MavenProject p = new MavenProject();
        p.setGroupId(g);
        p.setArtifactId(a);
        p.setVersion(v);
        return p;
    }

    protected MojoExecution mojoExecution(String goal, String executionId) {
        Plugin p = new Plugin();
        p.setGroupId("groupId");
        p.setArtifactId("artifactId");
        p.setVersion("version");
        MojoExecution me = new MojoExecution(p, goal, executionId);
        return me;
    }
}
