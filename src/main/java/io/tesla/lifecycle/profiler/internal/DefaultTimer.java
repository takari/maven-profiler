/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler.internal;

import io.tesla.lifecycle.profiler.Timer;
import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class DefaultTimer implements Timer {
    static final int MS_PER_SEC = 1000;
    static final int SEC_PER_MIN = 60;

    private long start;
    private long time;

    public DefaultTimer() {
        start = System.currentTimeMillis();
    }

    @Override
    public void stop() {
        time = elapsedTime();
    }

    @Override
    public long getElapsedTime() {
        return time;
    }

    private long elapsedTime() {
        return System.currentTimeMillis() - start;
    }

    protected static String formatMilliseconds(long durationInMillis) {

        long secs = durationInMillis / MS_PER_SEC;
        long mins = secs / SEC_PER_MIN;
        secs = secs % SEC_PER_MIN;
        long fractionOfASecond = durationInMillis - (secs * 1000);

        StringBuilder msg = new StringBuilder();
        if (mins > 0) {
            msg.append(mins);
            msg.append("m ");
        }
        if (secs > 0) {
            msg.append(secs);
            msg.append("s");
        }

        if (mins == 0) {
            if (msg.length() > 0) msg.append(" ");
            msg.append(fractionOfASecond);
            msg.append("ms");
        }

        return msg.toString();
    }
}
