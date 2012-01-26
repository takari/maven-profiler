package io.tesla.lifecycle.profiler;

import org.apache.maven.plugin.MojoExecution;

public class MojoProfile extends Profile {

  private MojoExecution mojoExecution;
  
  protected MojoProfile(MojoExecution mojoExecution) {
    super(new Timer());
    this.mojoExecution = mojoExecution;
  }
  
  public String getId() {
    return mojoExecution.getGroupId() + ":" + mojoExecution.getArtifactId() + ":" + mojoExecution.getVersion() + " (" + mojoExecution.getExecutionId() + ") ";
  }

}
