package org.eclipse.tesla.lifecycle.profiler;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;

//TODO: need to deal with the mojo execution within each project

/**
 * @author Jason van Zyl
 */
@Named
@Singleton
public class LifecycleProfiler extends AbstractEventSpy {

  private Timer sessionTimer;
  private Map<String, Timer> taskTimers;

  @Override
  public void init(Context context) throws Exception {
    taskTimers = new LinkedHashMap<String, Timer>();
  }

  @Override
  public void onEvent(Object event) throws Exception {
    if (event instanceof ExecutionEvent) {
      ExecutionEvent executionEvent = (ExecutionEvent) event;
      if (executionEvent.getType() == ExecutionEvent.Type.SessionStarted) {
        sessionTimer = new Timer();
      } else if (executionEvent.getType() == ExecutionEvent.Type.SessionEnded) {
        sessionTimer.stop();
        System.out.println(getTimes());
      } else if (executionEvent.getType() == ExecutionEvent.Type.ProjectStarted) {
        //
        // We need to collect the mojoExecutions within each project
        //
      } else if (executionEvent.getType() == ExecutionEvent.Type.ProjectSucceeded || executionEvent.getType() == ExecutionEvent.Type.ProjectFailed) {

      } else if (executionEvent.getType() == ExecutionEvent.Type.MojoStarted) {
        MojoExecution task = executionEvent.getMojoExecution();
        taskTimers.put(key(task), new Timer());
      } else if (executionEvent.getType() == ExecutionEvent.Type.MojoSucceeded || executionEvent.getType() == ExecutionEvent.Type.MojoFailed) {
        MojoExecution task = executionEvent.getMojoExecution();
        taskTimers.get(key(task)).stop();
      }
    }
  }

  private String key(MojoExecution task) {
    return task.getGroupId() + ":" + task.getArtifactId() + ":" + task.getVersion() + " (" + task.getExecutionId() + ") ";
  }

  private String getTimes() {
    StringBuffer sb = new StringBuffer();
    sb.append("\n").append("Mojo Execution Profile: ").append("\n\n");
    for (String key : taskTimers.keySet()) {
      sb.append(Timer.formatTime(taskTimers.get(key).getTime())).append(" --- ").append(key).append("\n");
    }
    return sb.toString();
  }
}
