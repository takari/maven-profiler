/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

/**
 * Interface for a simple timer that is like a stop-watch. Once created, it starts ticking until you {@link #stop() stop} it.
 * Then you can use {@link #getElapsedTime()} to retrieve the elapsed duration.
 */
public interface Timer extends Timing {

    /**
     * Stops this timer. Should only be called once.
     */
    void stop();

    //  default Duration getDuration() {
    //
    //    return Duration.of(getTime(), ChronoUnit.MILLIS);
    //  }

}
