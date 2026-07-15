/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;

final class TimelineRecorder implements AutoCloseable {

    static final String TIMELINE_PROPERTY = "maven.profile.timeline";
    private static final String DEFAULT_TIMELINE = ".maven.profiling-timeline.jsonl";
    private static final int SCHEMA_VERSION = 1;

    private final Path output;
    private final AtomicLong sequence = new AtomicLong();
    private BufferedWriter writer;

    TimelineRecorder() {
        this(configuredOutput());
    }

    TimelineRecorder(Path output) {
        this.output = output;
    }

    boolean isEnabled() {
        return output != null;
    }

    void init() throws IOException {
        if (!isEnabled()) {
            return;
        }
        Path parent = output.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        writer = Files.newBufferedWriter(
                output,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    synchronized void record(ExecutionEvent event) throws IOException {
        if (writer == null) {
            return;
        }
        writeExecutionEvent(event);
        if (event.getType() == ExecutionEvent.Type.SessionStarted) {
            writeReactor(event.getSession());
        }
        if (event.getType() == ExecutionEvent.Type.ProjectSucceeded
                || event.getType() == ExecutionEvent.Type.ProjectFailed
                || event.getType() == ExecutionEvent.Type.SessionEnded) {
            writer.flush();
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }

    private void writeExecutionEvent(ExecutionEvent event) throws IOException {
        StringBuilder line = startLine("execution");
        field(line, "event", event.getType().name());
        field(line, "thread", Thread.currentThread().getName());
        numberField(line, "threadId", Thread.currentThread().getId());
        projectFields(line, event.getProject());
        mojoFields(line, event.getMojoExecution());
        if (event.getException() != null) {
            field(line, "exceptionClass", event.getException().getClass().getName());
            field(line, "exceptionMessage", event.getException().getMessage());
        }
        finishLine(line);
    }

    private void writeReactor(MavenSession session) throws IOException {
        ProjectDependencyGraph graph = session.getProjectDependencyGraph();
        for (MavenProject project : session.getProjects()) {
            StringBuilder line = startLine("reactorProject");
            projectFields(line, project);
            List<MavenProject> upstream =
                    graph == null ? Collections.emptyList() : graph.getUpstreamProjects(project, false);
            projectArrayField(line, "upstreamProjects", upstream);
            finishLine(line);
        }
    }

    private StringBuilder startLine(String type) {
        StringBuilder line = new StringBuilder(256);
        line.append('{');
        numberField(line, "schemaVersion", SCHEMA_VERSION);
        field(line, "type", type);
        numberField(line, "sequence", sequence.incrementAndGet());
        field(line, "wallTime", Instant.now().toString());
        numberField(line, "nanoTime", System.nanoTime());
        return line;
    }

    private void projectFields(StringBuilder line, MavenProject project) {
        if (project == null) {
            return;
        }
        field(line, "project", projectId(project));
        if (project.getBasedir() != null) {
            field(line, "basedir", project.getBasedir().getAbsolutePath());
        }
    }

    private void mojoFields(StringBuilder line, MojoExecution mojo) {
        if (mojo == null) {
            return;
        }
        field(line, "mojo", mojo.getGroupId() + ":" + mojo.getArtifactId() + ":" + mojo.getVersion());
        field(line, "goal", mojo.getGoal());
        field(line, "executionId", mojo.getExecutionId());
        field(line, "lifecyclePhase", mojo.getLifecyclePhase());
    }

    private void projectArrayField(StringBuilder line, String name, List<MavenProject> projects) {
        comma(line);
        quote(line, name);
        line.append(':').append('[');
        boolean first = true;
        for (MavenProject project : projects) {
            if (!first) {
                line.append(',');
            }
            quote(line, projectId(project));
            first = false;
        }
        line.append(']');
    }

    private void finishLine(StringBuilder line) throws IOException {
        writer.write(line.append('}').append('\n').toString());
    }

    private static String projectId(MavenProject project) {
        return project.getGroupId() + ":" + project.getArtifactId() + ":" + project.getVersion();
    }

    private static void field(StringBuilder line, String name, String value) {
        if (value == null) {
            return;
        }
        comma(line);
        quote(line, name);
        line.append(':');
        quote(line, value);
    }

    private static void numberField(StringBuilder line, String name, long value) {
        comma(line);
        quote(line, name);
        line.append(':').append(value);
    }

    private static void comma(StringBuilder line) {
        if (line.length() > 1) {
            line.append(',');
        }
    }

    private static void quote(StringBuilder line, String value) {
        line.append('"');
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '"':
                    line.append("\\\"");
                    break;
                case '\\':
                    line.append("\\\\");
                    break;
                case '\b':
                    line.append("\\b");
                    break;
                case '\f':
                    line.append("\\f");
                    break;
                case '\n':
                    line.append("\\n");
                    break;
                case '\r':
                    line.append("\\r");
                    break;
                case '\t':
                    line.append("\\t");
                    break;
                default:
                    if (character < 0x20) {
                        line.append(String.format("\\u%04x", (int) character));
                    } else {
                        line.append(character);
                    }
            }
        }
        line.append('"');
    }

    private static Path configuredOutput() {
        String configured = System.getProperty(TIMELINE_PROPERTY);
        if (configured == null) {
            return null;
        }
        if ("false".equalsIgnoreCase(configured) || "no".equalsIgnoreCase(configured)) {
            return null;
        }
        if (configured.isEmpty() || "true".equalsIgnoreCase(configured)) {
            String root = System.getProperty("maven.multiModuleProjectDirectory", ".");
            return Paths.get(root, DEFAULT_TIMELINE);
        }
        return Paths.get(configured);
    }
}
