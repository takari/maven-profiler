/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

public abstract class AbstractProfile implements Profile {

  protected long elapsedTime;

  protected AbstractProfile() {
    super();
  }

  /**
   * @param elapsedTime the new value of {@link #getElapsedTime()}.
   */
  public void setElapsedTime(long elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  /**
   * @param millis the milliseconds to add to {@link #getElapsedTime() elapsed time}.
   */
  public void addElapsedTime(long millis) {

    this.elapsedTime += millis;
  }

  @Override
  public long getElapsedTime() {
      return elapsedTime;
  }

}
