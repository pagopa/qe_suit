package it.pagopa.pst.maven;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Builds an isolated class loader with the project's compile + runtime classpath
 * (output directory with the compiled generated sources, and all resolved dependencies).
 * The parent is the platform class loader, so no class leaks from the Maven plugin realm.
 */
public final class ProjectClassLoaderFactory {

    private ProjectClassLoaderFactory() {
    }

    public static URLClassLoader create(MavenProject project) throws MojoExecutionException {
        Set<String> elements = new LinkedHashSet<>();
        try {
            elements.add(project.getBuild().getOutputDirectory());
            elements.addAll(project.getCompileClasspathElements());
            elements.addAll(project.getRuntimeClasspathElements());
        } catch (DependencyResolutionRequiredException exception) {
            throw new MojoExecutionException("Project dependencies are not resolved", exception);
        }
        return create(elements);
    }

    static URLClassLoader create(Iterable<String> classpathElements) throws MojoExecutionException {
        List<URL> urls = new ArrayList<>();
        for (String element : classpathElements) {
            if (element == null || element.isBlank()) continue;
            try {
                urls.add(new File(element).toURI().toURL());
            } catch (MalformedURLException exception) {
                throw new MojoExecutionException("Invalid classpath element: " + element, exception);
            }
        }
        return new URLClassLoader("pst-project", urls.toArray(URL[]::new), ClassLoader.getPlatformClassLoader());
    }
}
