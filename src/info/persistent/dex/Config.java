package info.persistent.dex;

public class Config {

    private static final String DEFAULT_OUTPUT = "cli";
    private static final String DEFAULT_FORMAT = "tree";

    public boolean includeClasses;

    public String packageFilter;

    public Integer maxDepth = Integer.MAX_VALUE;

    public DexMethodCounts.Filter filter = DexMethodCounts.Filter.ALL;

    public String format = DEFAULT_FORMAT;

    public String output = DEFAULT_OUTPUT;
}
