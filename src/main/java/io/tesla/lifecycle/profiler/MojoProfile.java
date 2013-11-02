/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import io.tesla.lifecycle.profiler.internal.DefaultTimer;

import org.apache.maven.plugin.MojoExecution;

public class MojoProfile extends Profile {

  private MojoExecution mojoExecution;
  
  protected MojoProfile(MojoExecution mojoExecution) {
    super(new DefaultTimer());
    this.mojoExecution = mojoExecution;
  }
  
  public String getId() {
    return mojoExecution.getGroupId() + ":" + mojoExecution.getArtifactId() + ":" + mojoExecution.getVersion() + " (" + mojoExecution.getExecutionId() + ") ";
  }

}
