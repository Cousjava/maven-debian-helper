package org.debian.maven.plugin;

import java.io.File;
import java.io.IOException;
import org.apache.maven.bootstrap.util.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Install pom and jar files into the /usr/share/hierarchy
 *
 * @goal sysinstall
 */
public class SysInstallMojo extends AbstractMojo
{
  // ----------------------------------------------------------------------
  // Mojo parameters
  // ----------------------------------------------------------------------

  /**
   * groupId
   *
   * @parameter expression="${project.groupId}"
   * @required
   * @readonly
   */
  private String groupId;

  /**
   * artifactId
   *
   * @parameter expression="${project.artifactId}"
   * @required
   * @readonly
   */
  private String artifactId;

  /**
   * version
   *
   * @parameter expression="${project.version}"
   * @required
   * @readonly
   */
  private String version;

  /**
   * directory where the current pom.xml can be found
   *
   * @parameter expression="${basedir}"
   * @required
   * @readonly
   */
  private File basedir;

  /**
   * directory of the jar file
   *
   * @parameter expression="${project.build.directory}"
   * @required
   * @readonly
   */
  private String jarDir;

  // ----------------------------------------------------------------------
  // Public methods
  // ----------------------------------------------------------------------

  public void execute() throws MojoExecutionException
  {
    try
    {
      runMojo();
    }
    catch(IOException e)
    {
      getLog().error("execution failed", e);
      throw new MojoExecutionException("IOException catched");
    }
  }

  // ----------------------------------------------------------------------
  // Private methods
  // ----------------------------------------------------------------------

  /* optional destination prefix, empty by default
   */

  protected String packagePath()
  {
    return "";
  }

  /* returns e.g. /org/debian/maven/maven-debian-plugin/0.1/
   */

  private String repoPath()
  {
    return "/" + groupId.replace( '.', '/' ) + "/" + artifactId + "/" + version + "/";
  }

  /* absolute path to destination dir
   */

  protected String fullRepoPath()
  {
    return packagePath() + "/usr/share/maven-repo" + repoPath();
  }

  private String pomName()
  {
    return artifactId + "-" + version + ".pom";
  }

  private String pomSrcPath()
  {
    return basedir.getAbsolutePath() + "/pom.xml";
  }

  private String pomDestPath()
  {
    return fullRepoPath() + pomName();
  }

  private String jarName()
  {
    return artifactId + "-" + version + ".jar";
  }

  private String fullJarName()
  {
    return jarDir + "/" + jarName();
  }

  private String jarDestPath()
  {
    return fullRepoPath() + jarName();
  }

  /* jar file name without version number
   */

  private String compatName()
  {
    return artifactId + ".jar";
  }

  private String compatSharePath()
  {
    return packagePath() + "/usr/share/java/";
  }

  private String compatRelPath()
  {
    return "../maven-repo" + repoPath() + jarName();
  }

  protected String fullCompatPath()
  {
    return compatSharePath() + compatName();
  }

  /* command for creating the relative symlink
   */

  private String[] linkCommand()
  {
    String[] command = {"ln", "-s", compatRelPath(), fullCompatPath()};
    return command;
  }

  /* copy the pom.xml
   */

  private void copyPom() throws IOException
  {
    FileUtils.copyFile(new File(pomSrcPath()), new File(pomDestPath()));
  }

  private void mkdir(String path) throws IOException
  {
    new File(path).mkdirs();
  }

  private void run(String[] command) throws IOException
  {
    Runtime.getRuntime().exec(command, null);
  }

  /* if a jar exists: copy it and symlink it to the compat share dir
   */

  private void copyAndSymlinkJar() throws IOException
  {
    FileUtils.copyFile(new File(pomSrcPath()), new File(pomDestPath()));
    File jarFile = new File(fullJarName());
    if (jarFile.exists())
    {
      FileUtils.copyFile(jarFile, new File(jarDestPath()));
      mkdir(compatSharePath());
      run(linkCommand());
    }
  }

  /* do the actual work
   */
  protected void runMojo() throws IOException
  {
    copyPom();
    copyAndSymlinkJar();
  }
}
