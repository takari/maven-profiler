/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import java.util.Collection;
import java.util.List;

/**
 * Interface for a profile of the profiling. The root profile is {@link SessionProfile} representing the recording of the
 * entire session. Each {@link Profile} can have {@link #getChildren() children} representing a partition of the whole
 * into smaller steps to give more details and granularity to trace down leaks and find spots worth to optimize.
 */
public interface Profile extends Timing {

  /**
   * @return the name of this profile (name of e.g. maven project, phase, plugin).
   */
  String getName();

  /**
   * @return the {@link List} of child {@link Profile}s contained in this {@link Profile}. Will be an empty list if this
   *         profile has not children.
   */
  Collection<? extends Profile> getChildren();

}
