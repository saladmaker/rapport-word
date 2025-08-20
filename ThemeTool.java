import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class ThemeTool {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java ThemeTool [-a | -n themeName] [extract|package]");
            return;
        }

        // Find base dir = location of this .java (or current working dir when run)
        Path baseDir = Paths.get("").toAbsolutePath();
        Path themesDir = baseDir.resolve("themes");

        if (!Files.isDirectory(themesDir)) {
            System.err.println("No 'themes' folder found in " + baseDir);
            return;
        }

        boolean all = args[0].equals("-a");
        String targetName = args[0].equals("-n") ? args[1] : null;
        String mode = args[args.length - 1];

        if (!mode.equals("extract") && !mode.equals("package")) {
            System.out.println("Mode must be extract or package");
            return;
        }

        if (all) {
            try (var dirs = Files.list(themesDir)) {
                dirs.filter(Files::isDirectory).forEach(d -> process(d, mode));
            }
        } else {
            Path dir = themesDir.resolve(targetName);
            process(dir, mode);
        }
    }

    static void process(Path templateDir, String mode) {
        Path themeDoc = templateDir.resolve("theme.docx");
        Path themeDir = templateDir.resolve("theme");

        try {
            if (mode.equals("extract")) {
                if (!Files.exists(themeDoc)) {
                    System.err.println("No theme.docx in " + templateDir);
                    return;
                }
                if (Files.exists(themeDir)) {
                    deleteRecursively(themeDir);
                }
                Files.createDirectories(themeDir);
                unzip(themeDoc, themeDir);
                System.out.println("Extracted: " + templateDir.getFileName());
            } else { // package
                if (!Files.exists(themeDir)) {
                    System.err.println("No theme/ dir in " + templateDir);
                    return;
                }
                Files.deleteIfExists(themeDoc);
                zip(themeDir, themeDoc);
                System.out.println("Packaged: " + templateDir.getFileName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void unzip(Path zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    static void zip(Path sourceDir, Path zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            Files.walk(sourceDir).forEach(path -> {
                if (Files.isRegularFile(path)) {
                    String entryName = sourceDir.relativize(path).toString().replace("\\", "/");
                    try {
                        zos.putNextEntry(new ZipEntry(entryName));
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            });
        }
    }

    static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) return;
        Files.walk(path)
                .sorted((a, b) -> b.compareTo(a)) // delete children before parents
                .forEach(p -> {
                    try { Files.delete(p); } catch (IOException e) { throw new UncheckedIOException(e); }
                });
    }
}
