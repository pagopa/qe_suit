package it.pagopa.pst.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectClassLoaderAndInvokerTest {

    @TempDir
    Path directory;

    @Test
    void projectClassLoaderIsIsolatedFromPluginRealm() throws Exception {
        try (URLClassLoader classLoader = ProjectClassLoaderFactory.create(List.of(directory.toString(), " "))) {
            assertSame(ClassLoader.getPlatformClassLoader(), classLoader.getParent());
            assertEquals(1, classLoader.getURLs().length);
            assertThrows(ClassNotFoundException.class,
                    () -> Class.forName(PstGenerateMojo.class.getName(), false, classLoader));
        }
    }

    @Test
    void invokerFailsExplicitlyWhenCommonIsNotOnProjectClasspath() throws Exception {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader classLoader = ProjectClassLoaderFactory.create(List.of(directory.toString()))) {
            MojoExecutionException error = assertThrows(MojoExecutionException.class,
                    () -> PstLauncherInvoker.invoke(classLoader, Map.of()));
            assertTrue(error.getMessage().contains("it.pagopa:common"));
        }
        assertSame(previous, Thread.currentThread().getContextClassLoader());
    }
}
