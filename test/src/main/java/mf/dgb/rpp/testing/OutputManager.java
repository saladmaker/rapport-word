package mf.dgb.rpp.testing;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OutputManager {
    private static final Path OUTPUT_DIR = Paths.get("output");

    public static Path getOutputFile(String theme, String generator) throws IOException {
        Files.createDirectories(OUTPUT_DIR);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return OUTPUT_DIR.resolve(generator + "_" + theme + "_" + timestamp + ".docx");
    }

    public static void cleanOutputs() throws IOException {
        if (Files.exists(OUTPUT_DIR)) {
            try (var files = Files.list(OUTPUT_DIR)) {
                files.forEach(f -> {
                    try {
                        Files.deleteIfExists(f);
                    } catch (IOException ignored) {
                    }
                });
            }
        }
    }

    public static void openInWord(Path file) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String path = file.toAbsolutePath().toString();

            if (isWSL()) {
                // Convert WSL path to Windows path: /home/... -> \\wsl$\<distro>\home\...
                String distro = System.getenv("WSL_DISTRO_NAME");
                if (distro == null)
                    distro = "Ubuntu";
                String winPath = "\\\\wsl$\\" + distro + path.replace("/", "\\");
                new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", winPath)
                        .inheritIO()
                        .start();
            } else if (os.contains("win")) {
                // Windows: Use cmd.exe
                new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", path)
                        .inheritIO()
                        .start();
            } else if (os.contains("mac")) {
                // macOS: Use open
                new ProcessBuilder("open", path)
                        .inheritIO()
                        .start();
            } else {
                // Linux with GUI: Use xdg-open
                new ProcessBuilder("xdg-open", path)
                        .inheritIO()
                        .start();
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to open file in Word: " + file, e);
        }
    }

    private static boolean isWSL() {
        String version = System.getenv("WSL_INTEROP");
        return version != null || System.getenv("WSL_DISTRO_NAME") != null;
    }

}
