/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler.internal;

import io.tesla.lifecycle.profiler.AggregationProfile;
import io.tesla.lifecycle.profiler.Profile;
import io.tesla.lifecycle.profiler.SessionProfile;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Advanced implementation of {@link AbstractSessionProfileRenderer} that can write both CSV and/or log human readable profiling summary.
 */
@Named
@Singleton
public class AdvancedSessionProfileRenderer extends AbstractSessionProfileRenderer {

    private static final String WRITE_CSV = "maven.profile.write.csv";

    private static final char CSV_SEPARATOR = ';';

    private final List<AggregationProfile> aggregations;

    private Writer csvWriter;

    public AdvancedSessionProfileRenderer() {

        super();
        this.aggregations = new ArrayList<>();
        this.aggregations.add(new AggregationProfile("Aggregation by project"));
        this.aggregations.add(new AggregationProfile("Aggregation by phase"));
        this.aggregations.add(new AggregationProfile("Aggregation by mojo"));
    }

    public void render(SessionProfile sessionProfile) {

        boolean writeCsv = getBooleanProperty(WRITE_CSV, true);
        OutputStream out = null;
        if (writeCsv) {
            String timestamp = new DateTimeFormatterBuilder()
                    .appendPattern("YYYY-MM-dd_HH-mm-ss")
                    .toFormatter()
                    .format(LocalDateTime.now());
            String filename = ".maven.profiling." + timestamp + ".csv";
            try {
                out = new FileOutputStream(filename);
                this.csvWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                try {
                    this.csvWriter.write("Depth");
                    this.csvWriter.write(CSV_SEPARATOR);
                    this.csvWriter.write("Name");
                    this.csvWriter.write(CSV_SEPARATOR);
                    this.csvWriter.write("Duration (ms)");
                    this.csvWriter.write(CSV_SEPARATOR);
                    this.csvWriter.write("Fraction");
                    this.csvWriter.write("\n");
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to write CSV data!", e);
                }
            } catch (IOException e) {
                throw new IllegalStateException("Failed to open file '" + filename + "' for writing!", e);
            }
        }
        try {
            renderProfile(sessionProfile, true);
            for (AggregationProfile aggregation : this.aggregations) {
                aggregation.setElapsedTime(sessionProfile.getElapsedTime());
                renderProfile(aggregation, false);
            }
        } finally {
            if (writeCsv) {
                close(this.csvWriter);
                this.csvWriter = null;
                close(out);
            }
        }
    }

    private void renderProfile(Profile sessionProfile, boolean aggregate) {

        renderProfile(0, sessionProfile, 0, "");
        renderProfileRecursive(sessionProfile, 0, "", aggregate);
    }

    private void close(AutoCloseable out) {

        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private void renderProfileRecursive(Profile profile, int depth, String prefix, boolean aggregate) {

        Collection<? extends Profile> children = profile.getChildren();
        AggregationProfile aggregation = null;
        if (aggregate && (depth < aggregations.size())) {
            aggregation = this.aggregations.get(depth);
        }
        int childDepth = depth + 1;
        long duration = profile.getElapsedTime();
        String childPrefix = prefix + "  ";
        for (Profile child : children) {
            renderProfile(childDepth, child, duration, childPrefix);
            if (aggregation != null) {
                aggregation.getOrCreateChild(child.getName()).addElapsedTime(child.getElapsedTime());
            }
            renderProfileRecursive(child, childDepth, childPrefix, aggregate);
        }
    }

    private void renderProfile(int depth, Profile profile, long total, String prefix) {

        String fraction = "1";
        String percentage = "100%";
        if (total > 0) {
            long duration = profile.getElapsedTime();
            if (duration < total) {
                long fract = (duration * 1000) / total;
                percentage = (fract / 10) + "%";
                fraction = Long.toString(fract);
                int zeros = 3 - fraction.length();
                while (zeros > 0) {
                    fraction = "0" + fraction;
                    zeros--;
                }
                fraction = "0." + fraction;
            } else if (duration > total) {
                // invalid, should never happen
                fraction = "";
                percentage = "";
            }
        }
        renderProfileAsCsv(depth, profile, fraction);
        if (this.logProfileData) {
            String message = prefix + profile.getName() + " "
                    + DefaultTimer.formatMilliseconds(profile.getElapsedTime()) + "(" + percentage + ")";
            render(message);
        }
    }

    private void renderProfileAsCsv(int depth, Profile profile, String fraction) {

        if (this.csvWriter == null) {
            return;
        }
        try {
            this.csvWriter.write(Integer.toString(depth));
            this.csvWriter.write(CSV_SEPARATOR);
            this.csvWriter.write(profile.getName());
            this.csvWriter.write(CSV_SEPARATOR);
            this.csvWriter.write(Long.toString(profile.getElapsedTime()));
            this.csvWriter.write(CSV_SEPARATOR);
            this.csvWriter.write(fraction);
            this.csvWriter.write("\n");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write CSV data!", e);
        }
    }

    private void render(String s) {

        System.out.println(s);
    }
}
