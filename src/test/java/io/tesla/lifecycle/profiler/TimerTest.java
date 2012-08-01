package io.tesla.lifecycle.profiler;

import org.junit.Assert;
import org.junit.Test;

import static io.tesla.lifecycle.profiler.Timer.MS_PER_SEC;

/**
 * @author Kristian Rosenvold
 */
public class TimerTest {

    @Test
    public void testTimeFormats() throws Exception
    {
        Assert.assertEquals("1ms", Timer.formatTime(1));
        Assert.assertEquals("1s 1ms", Timer.formatTime(1001));
        Assert.assertEquals("1m 1s", Timer.formatTime(61 * MS_PER_SEC));
    }

    @Test
    public void assertDetailLoss()
    {
        Assert.assertEquals("1m 1s", Timer.formatTime(61 * MS_PER_SEC + 1));
    }
}
