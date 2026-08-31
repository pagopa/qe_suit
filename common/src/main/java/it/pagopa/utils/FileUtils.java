package it.pagopa.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class FileUtils {

    private FileUtils() {
    }

    public static File loadClasspathResourceAsTempFile(String classpathLocation) {
        if (classpathLocation == null || classpathLocation.isBlank()) {
            throw new IllegalArgumentException("classpathLocation non può essere vuoto");
        }

        String normalizedPath = classpathLocation.startsWith("/")
                ? classpathLocation.substring(1)
                : classpathLocation;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = FileUtils.class.getClassLoader();
        }

        try (InputStream inputStream = classLoader.getResourceAsStream(normalizedPath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Risorsa non trovata nel classpath: " + classpathLocation);
            }

            String fileName = new File(normalizedPath).getName();
            String prefix = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
            String suffix = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : ".tmp";

            File tempFile = File.createTempFile(prefix + "-", suffix);
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            tempFile.deleteOnExit();
            return tempFile;
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile caricare la risorsa: " + classpathLocation, e);
        }
    }
}