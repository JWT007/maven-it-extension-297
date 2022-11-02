package me.jwt007.maven.plugins.ite.issue297;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "hello",
      defaultPhase = LifecyclePhase.COMPILE,
      requiresDependencyCollection = ResolutionScope.RUNTIME,
      threadSafe = true)
public class HelloMojo extends AbstractMojo {

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("++++++++++ HELLO +++++++++++++++");
  }

}
