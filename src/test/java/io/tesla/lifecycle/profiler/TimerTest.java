package io.tesla.lifecycle.profiler;

import io.tesla.lifecycle.profiler.internal.DefaultTimer;

import org.junit.Assert;
import org.junit.Test;

import static io.tesla.lifecycle.profiler.internal.DefaultTimer.MS_PER_SEC;

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
