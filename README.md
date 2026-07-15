# Maven profiler

[![Maven Central](https://img.shields.io/maven-central/v/io.takari.maven/maven-profiler.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.takari.maven/maven-profiler)
[![Verify](https://github.com/takari/maven-profiler/actions/workflows/ci.yml/badge.svg)](https://github.com/takari/maven-profiler/actions/workflows/ci.yml)
[![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/io/takari/maven/maven-profiler/badge.json)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/io/takari/maven/maven-profiler/README.md)

The Tesla profiler is a simple EventSpy implementation that gathers timing information. It collects the time it takes to execute each mojo and gathers them within the phase they run. This was originally written to find a particular hotspot and it not formatted particularly well and definitely needs improvement.

To profile your build you need to install the profiling extension your Maven/Tesla distribution. Find the [latest version in Maven Central][1] and install it in the `${M2_HOME}/lib/ext` directory,
or add it to `.mvn/extensions.xml`.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
    <extension>
        <groupId>io.takari.maven</groupId>
        <artifactId>maven-profiler</artifactId>
        <version>1.1.0</version>
  </extension>
</extensions>
```

To activate the profiling you need to enable the `maven.profile` system property:

```
mvn clean install -Dmaven.profile
```

Parallel Maven reactors are supported. Project, phase, and mojo state is isolated by project when Maven executes modules concurrently with `-T`.

### Raw timeline

Set `maven.profile.timeline` to record the raw Maven execution timeline as JSON Lines. The timeline can be enabled with or without the aggregate console/CSV profile:

```
mvn -T4 verify -Dmaven.profile -Dmaven.profile.timeline
mvn -T4 verify -Dmaven.profile.timeline=target/maven-timeline.jsonl
```

An empty value or `true` writes `.maven.profiling-timeline.jsonl` in the multi-module project directory. A non-boolean value is used as the output path. Each record contains a schema version, sequence, wall and monotonic timestamps, event type, Maven thread, and applicable project/mojo identity. Session startup also records the selected reactor projects and their upstream project dependencies.

The timeline contains observed Maven session, project, and mojo start/finish events. Maven does not emit a project-ready or scheduler-queue event; tools may derive an earliest-ready bound from the captured dependency graph and predecessor completion times, but should label that value as derived.

Here's an example of what the output will look like:

```
org.apache.maven:maven-core:3.1.2-SNAPSHOT

  clean 176ms
    org.apache.maven.plugins:maven-clean-plugin:2.5 (default-clean) 176ms

  initialize 408ms
    org.codehaus.mojo:buildnumber-maven-plugin:1.2 (create-noncanonicalrev) 349ms
    org.codehaus.mojo:buildnumber-maven-plugin:1.2 (create-buildnumber) 59ms

  generate-sources 408ms
    org.codehaus.modello:modello-maven-plugin:1.8.1 (standard) 369ms
    org.codehaus.modello:modello-maven-plugin:1.8.1 (standard) 28ms
    org.codehaus.modello:modello-maven-plugin:1.8.1 (standard) 11ms

  generate-resources 933ms
    org.apache.maven.plugins:maven-remote-resources-plugin:1.4 (default) 932ms

  process-resources 225ms
    org.apache.maven.plugins:maven-resources-plugin:2.6 (default-resources) 224ms

  compile 4s 522ms
    org.apache.maven.plugins:maven-compiler-plugin:2.5.1 (default-compile) 4s 522ms

  process-classes 6s 880ms
    org.codehaus.mojo:animal-sniffer-maven-plugin:1.6 (check-java-1.5-compat) 5s 814ms
    org.codehaus.plexus:plexus-component-metadata:1.5.5 (default) 946ms
    org.sonatype.plugins:sisu-maven-plugin:1.1 (default) 120ms

  process-test-resources 173ms
    org.apache.maven.plugins:maven-resources-plugin:2.6 (default-testResources) 173ms

  test-compile 818ms
    org.apache.maven.plugins:maven-compiler-plugin:2.5.1 (default-testCompile) 818ms

  process-test-classes 134ms
    org.codehaus.plexus:plexus-component-metadata:1.5.5 (default) 110ms
    org.sonatype.plugins:sisu-maven-plugin:1.1 (default) 23ms

  test 11s 306ms
    org.apache.maven.plugins:maven-surefire-plugin:2.12 (default-test) 11s 306ms

  package 1s 371ms
    org.apache.maven.plugins:maven-jar-plugin:2.4 (default-jar) 502ms
    org.apache.maven.plugins:maven-site-plugin:3.3 (attach-descriptor) 869ms

```
[1]: https://repo.maven.apache.org/maven2/io/takari/maven/maven-profiler/
