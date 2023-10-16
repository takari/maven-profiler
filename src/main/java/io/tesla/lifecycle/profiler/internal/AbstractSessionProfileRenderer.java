/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler.internal;

import java.util.Locale;

import io.tesla.lifecycle.profiler.SessionProfileRenderer;

/**
 * Abstract base implementation of {@link SessionProfileRenderer}.
 */
public abstract class AbstractSessionProfileRenderer implements SessionProfileRenderer {

  /** Determines if the profiling report shall be logged at the end of the build */
  protected final boolean logProfileData;

  /**
   * The constructor.
   */
  public AbstractSessionProfileRenderer() {

    super();
    this.logProfileData = getBooleanProperty("maven.profile.log.output", true);
  }

  /**
   * @param name the name of the {@link System#getProperty(String) system property}.
   * @param defaultValue the default value if the {@link System#getProperty(String) system property} is undefined or invalid.
   * @return the configured boolean value.
   */
  protected static boolean getBooleanProperty(String name, boolean defaultValue) {

    boolean result = defaultValue;
    String valueAsString = System.getProperty(name);
    if (valueAsString != null) {
      valueAsString = valueAsString.toLowerCase(Locale.ROOT);
      if ("true".equals(valueAsString) || "yes".equals(valueAsString)) {
        result = true;
      } else if ("false".equals(valueAsString) || "no".equals(valueAsString)) {
        result = true;
      }
    }
    return result;
  }

}
