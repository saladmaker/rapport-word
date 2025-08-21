package rpp.poi.playground;

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
            String path = file.toAbsolutePath().toString();

            if (path.startsWith("/mnt/")) {
                // Convert WSL path to Windows path (e.g. /mnt/c/... → C:\...)
                String winPath = path.replaceFirst("^/mnt/([a-zA-Z])", "$1:")
                                     .replace("/", "\\");
                // Launch Word or use explorer (default app)
                new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", winPath)
                        .inheritIO()
                        .start();
            } else {
                // Native Linux/macOS/Windows Desktop API
                java.awt.Desktop.getDesktop().open(file.toFile());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to open file in Word: " + file, e);
        }
    }

  
}
