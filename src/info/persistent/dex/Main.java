package info.persistent.dex;

import com.android.dexdeps.UsageException;

public class Main {

    public static void main(String[] args) {
        try {
            DexCountApi dexCountApi = new DexCountApi();
            Args arguments = parseConfigFromArgs(args);
            dexCountApi.generateBulkReport(arguments.config, arguments.inputFiles);
        } catch (UsageException ue) {
            usage();
            System.exit(2);
        }
    }

    static void usage() {
        System.err.print(
                "DEX per-package/class method counts v1.0\n" +
                        "Usage: dex-method-counts [options] <file.{dex,apk,jar,directory}> ...\n" +
                        "Options:\n" +
                        "  --include-classes\n" +
                        "  --package-filter=com.foo.bar\n" +
                        "  --max-depth=N\n" +
                        "  --format={tree|json}\n" +
                        "  --output={cli|file}\n"
        );
    }

    static Args parseConfigFromArgs(String[] args) {
        Args parsedArgs = new Args();
        Config config = new Config();
        parsedArgs.config = config;

        int idx;
        for (idx = 0; idx < args.length; idx++) {
            String arg = args[idx];

            if (arg.equals("--") || !arg.startsWith("--")) {
                break;
            } else if (arg.equals("--include-classes")) {
                config.includeClasses = true;
            } else if (arg.startsWith("--package-filter=")) {
                config.packageFilter = arg.substring(arg.indexOf('=') + 1);
            } else if (arg.startsWith("--max-depth=")) {
                config.maxDepth =
                        Integer.parseInt(arg.substring(arg.indexOf('=') + 1));
            } else if (arg.startsWith("--filter=")) {
                config.filter = Enum.valueOf(
                        DexMethodCounts.Filter.class,
                        arg.substring(arg.indexOf('=') + 1).toUpperCase());
            } else if (arg.startsWith("--format=")) {
                config.format = arg.substring(arg.indexOf('=') + 1);
            } else if (arg.startsWith("--output=")) {
                config.output = arg.substring(arg.indexOf('=') + 1);
            } else {
                System.err.println("Unknown option '" + arg + "'");
                throw new UsageException();
            }
        }

        // We expect at least one more argument (file name).
        int fileCount = args.length - idx;
        if (fileCount == 0) {
            throw new UsageException();
        }
        parsedArgs.inputFiles = new String[fileCount];
        System.arraycopy(args, idx, parsedArgs.inputFiles, 0, fileCount);
        return parsedArgs;
    }

    static class Args {
        Config config;
        String[] inputFiles;
    }
}
