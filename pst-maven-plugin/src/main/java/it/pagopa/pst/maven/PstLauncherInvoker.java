package it.pagopa.pst.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Reflection bridge to {@code it.pagopa.infrastructure.pst.launcher.PstGenerationLauncher}, loaded from the
 * project class loader. Only JDK types ({@code Map<String, String>} in, {@code Map<String, Object>} out)
 * cross the boundary.
 */
public final class PstLauncherInvoker {

    static final String LAUNCHER_CLASS = "it.pagopa.infrastructure.pst.launcher.PstGenerationLauncher";

    private PstLauncherInvoker() {
    }

    public static Map<?, ?> invoke(ClassLoader projectClassLoader, Map<String, String> arguments)
            throws MojoExecutionException, MojoFailureException {
        Method generate;
        try {
            Class<?> launcher = Class.forName(LAUNCHER_CLASS, true, projectClassLoader);
            generate = launcher.getMethod("generate", Map.class);
        } catch (ClassNotFoundException exception) {
            throw new MojoExecutionException(
                    LAUNCHER_CLASS + " not found on the project classpath: the project must depend on it.pagopa:common"
                            + " with PST support.", exception);
        } catch (NoSuchMethodException exception) {
            throw new MojoExecutionException(
                    "Incompatible it.pagopa:common on the project classpath: " + LAUNCHER_CLASS
                            + ".generate(Map) not found.", exception);
        }

        Thread thread = Thread.currentThread();
        ClassLoader previous = thread.getContextClassLoader();
        thread.setContextClassLoader(projectClassLoader);
        try {
            return (Map<?, ?>) generate.invoke(null, arguments);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause() == null ? exception : exception.getCause();
            throw new MojoFailureException("PST generation failed: " + cause.getMessage(), cause);
        } catch (IllegalAccessException exception) {
            throw new MojoExecutionException("Cannot invoke " + LAUNCHER_CLASS, exception);
        } finally {
            thread.setContextClassLoader(previous);
        }
    }
}
