/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.MojoExecution;

public class MojoProfile extends AbstractTimerProfile {

  private MojoExecution mojoExecution;

  protected MojoProfile(MojoExecution mojoExecution) {
    super();
    this.mojoExecution = mojoExecution;
  }

  public String getId() {
    return mojoExecution.getGroupId() + ":" + mojoExecution.getArtifactId() + ":" + mojoExecution.getVersion() + " (" + mojoExecution.getExecutionId() + ")";
  }

  @Override
  public String getName() {

    return getId();
  }

  @Override
  public List<? extends Profile> getChildren() {

    return Collections.emptyList();
  }

}
