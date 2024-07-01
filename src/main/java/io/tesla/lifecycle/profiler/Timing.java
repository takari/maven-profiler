/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

/**
 * Interface for an object that records the timing and therefore has an {@link #getElapsedTime() elapsed time}.
 */
public interface Timing {

    /**
     * @return the duration in milliseconds of this timing. Typically the duration from the instantiation a {@link Timer}
     *         until it has been {@link Timer#stop() stop}ed.
     */
    long getElapsedTime();

    //  default Duration getDuration() {
    //
    //    return Duration.of(getTime(), ChronoUnit.MILLIS);
    //  }

}
