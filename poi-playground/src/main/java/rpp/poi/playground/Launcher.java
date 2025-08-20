package rpp.poi.playground;

import java.lang.reflect.Method;

public class Launcher {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: Launcher <MainClassName> [themeName] [--clean]");
            return;
        }

        String mainClassName = "rpp.poi.playground." + args[0];
        Class<?> clazz = Class.forName(mainClassName);
        Method mainMethod = clazz.getMethod("main", String[].class);

        // Forward remaining args to the target main
        String[] forwardedArgs = new String[args.length - 1];
        System.arraycopy(args, 1, forwardedArgs, 0, forwardedArgs.length);

        mainMethod.invoke(null, (Object) forwardedArgs);
    }
}
