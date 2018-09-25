package cc.eevee.gradle.plugin;

import cc.eevee.gradle.plugin.internal.LoggerWrapper;
import com.android.build.gradle.AndroidConfig;
import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.LibraryExtension;
import com.android.utils.ILogger;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

@NonNullApi
@SuppressWarnings("unused")
public class AndroidArchivePlugin implements Plugin<Project> {

  private Project project;
  private ILogger logger;

  @Override
  public void apply(Project project) {
    this.project = project;
    this.logger = new LoggerWrapper(project.getLogger()).tag(AndroidArchivePlugin.class.getSimpleName());

    project.afterEvaluate(pro -> createTasks());
  }

  private void createTasks() {
    // Get Android config
    AndroidConfig androidConfig = project.getExtensions().getByType(AndroidConfig.class);

    androidConfig.getSourceSets().getByName("main", androidSourceSet -> {
      FileTree javaSourceFiles = androidSourceSet.getJava().getSourceFiles();

      //logger.verbose("JavaSourceFiles:");
      //javaSourceFiles.forEach(file -> {
      //  logger.verbose("  " + file.getPath());
      //});

      logger.verbose("Create androidSourcesJar task");
      Jar androidSourcesJar = project.getTasks().create(
          "androidSourcesJar", Jar.class, task -> {
            task.setClassifier("sources");
            task.from(javaSourceFiles);
          });

      logger.verbose("Create androidJavadoc task");
      Javadoc androidJavadoc = project.getTasks().create(
          "androidJavadoc", Javadoc.class, task -> {
            if (androidConfig instanceof BaseExtension) {
              BaseExtension baseExt = (BaseExtension) androidConfig;
              task.setClasspath(project.files(baseExt.getBootClasspath()));
            }

            if (androidConfig instanceof LibraryExtension) {
              LibraryExtension libExt = (LibraryExtension) androidConfig;
              libExt.getLibraryVariants().all(variant -> {
                if (variant.getName().equals("release")) {
                  //task.getClasspath().add(variant.getJavaCompile().getClasspath());
                  Task t = variant.getJavaCompiler();
                  if (t instanceof JavaCompile) {
                    task.getClasspath().add(((JavaCompile) t).getClasspath());
                  }
                }
              });
            }

            task.setSource(androidSourceSet.getJava().getSrcDirs());

            MinimalJavadocOptions options = task.getOptions();
            options.setEncoding("UTF-8");
            if (options instanceof StandardJavadocDocletOptions) {
              StandardJavadocDocletOptions stdOptions = (StandardJavadocDocletOptions) options;
              stdOptions.addStringOption("Xdoclint:none", "-quiet");
              stdOptions.setCharSet("UTF-8");
            }

            task.exclude("**/R.java");
            task.setFailOnError(false);
          });

      logger.verbose("Create androidJavadocJar task");
      Jar androidJavadocJar = project.getTasks().create("androidJavadocJar", Jar.class, task -> {
        task.setClassifier("javadoc");
        task.from(androidJavadoc.getDestinationDir());
      });
      androidJavadocJar.dependsOn(androidJavadoc);

      logger.verbose("Add archives to artifacts");
      project.getArtifacts().add("archives", androidJavadocJar);
      project.getArtifacts().add("archives", androidSourcesJar);
    });
  }
}
