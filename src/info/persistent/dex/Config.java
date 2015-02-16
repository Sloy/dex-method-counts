package info.persistent.dex;

public class Config {

    private static final String DEFAULT_OUTPUT = "cli";
    private static final String DEFAULT_FORMAT = "tree";

    boolean includeClasses;

    String packageFilter;

    Integer maxDepth = Integer.MAX_VALUE;

    DexMethodCounts.Filter filter = DexMethodCounts.Filter.ALL;

    String[] inputFileNames;

    String format = DEFAULT_FORMAT;

    String output = DEFAULT_OUTPUT;
}
