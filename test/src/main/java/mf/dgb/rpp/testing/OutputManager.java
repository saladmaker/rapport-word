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
                // Better WSL path detection and conversion
                String distro = getWSLDistro();
                String wslPath = convertToWindowsPath(path, distro);

                System.out.println("Opening WSL path: " + wslPath); // Debug

                new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", "\"" + wslPath + "\"")
                        .inheritIO()
                        .start();

            } else if (os.contains("win")) {
                // Windows native
                new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", "\"" + path + "\"")
                        .inheritIO()
                        .start();
            } else if (os.contains("mac")) {
                // macOS
                new ProcessBuilder("open", path)
                        .inheritIO()
                        .start();
            } else {
                // Linux
                new ProcessBuilder("xdg-open", path)
                        .inheritIO()
                        .start();
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to open file in Word: " + file, e);
        }
    }

    private static boolean isWSL() {
        return System.getenv("WSL_DISTRO_NAME") != null ||
                System.getenv("WSL_INTEROP") != null ||
                Files.exists(Path.of("/proc/version")) &&
                        readFile("/proc/version").toLowerCase().contains("microsoft");
    }

    private static String getWSLDistro() {
        String distro = System.getenv("WSL_DISTRO_NAME");
        if (distro == null) {
            // Fallback: try to detect from common distros
            if (Files.exists(Path.of("/etc/os-release"))) {
                String osRelease = readFile("/etc/os-release");
                if (osRelease.contains("Ubuntu"))
                    distro = "Ubuntu";
                else if (osRelease.contains("Debian"))
                    distro = "Debian";
                else
                    distro = "Ubuntu"; // Default fallback
            } else {
                distro = "Ubuntu"; // Final fallback
            }
        }
        return distro.trim();
    }

    private static String convertToWindowsPath(String wslPath, String distro) {
        // Handle WSL paths starting from root
        if (wslPath.startsWith("/")) {
            return "\\\\wsl$\\" + distro + wslPath.replace("/", "\\");
        }

        // Handle paths that might already be in WSL format
        return "\\\\wsl$\\" + distro + "\\" + wslPath.replace("/", "\\");
    }

    private static String readFile(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            return "";
        }
    }

}
