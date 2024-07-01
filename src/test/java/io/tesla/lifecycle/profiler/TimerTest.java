/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import static io.tesla.lifecycle.profiler.internal.DefaultTimer.MS_PER_SEC;

import io.tesla.lifecycle.profiler.internal.DefaultTimer;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kristian Rosenvold
 */
public class TimerTest {

    @Test
    public void testTimeFormats() throws Exception {

        Timer timer = new DefaultTimer();
        Assert.assertEquals("1ms", timer.format(1));
        Assert.assertEquals("1s 1ms", timer.format(1001));
        Assert.assertEquals("1m 1s", timer.format(61 * MS_PER_SEC));
    }

    @Test
    public void assertDetailLoss() {
        Timer timer = new DefaultTimer();
        Assert.assertEquals("1m 1s", timer.format(61 * MS_PER_SEC + 1));
    }
}
