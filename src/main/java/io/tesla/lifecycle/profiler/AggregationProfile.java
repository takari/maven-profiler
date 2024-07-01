/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregationProfile extends AbstractProfile {

    private final String name;

    private final Map<String, AggregationProfile> childMap;

    private final List<AggregationProfile> children;

    public AggregationProfile() {
        this("all");
    }

    public AggregationProfile(String name) {
        super();
        this.name = name;
        this.childMap = new HashMap<>();
        this.children = new ArrayList<>();
    }

    @Override
    public String getName() {

        return this.name;
    }

    @Override
    public long getElapsedTime() {

        long millis = this.elapsedTime;
        if (millis == 0) {
            for (AggregationProfile child : this.children) {
                millis += child.getElapsedTime();
            }
        }
        return millis;
    }

    public AggregationProfile getChild(String name) {

        return this.childMap.get(name);
    }

    public AggregationProfile getOrCreateChild(String name) {

        return this.childMap.computeIfAbsent(name, this::newAggregationProfile);
    }

    private AggregationProfile newAggregationProfile(String name) {

        AggregationProfile profile = new AggregationProfile(name);
        this.children.add(profile);
        return profile;
    }

    @Override
    public Collection<? extends Profile> getChildren() {

        return this.children;
    }
}
