package info.persistent.dex;

public class Config {

    boolean includeClasses;

    String packageFilter;

    Integer maxDepth = Integer.MAX_VALUE;

    DexMethodCounts.Filter filter = DexMethodCounts.Filter.ALL;

    String[] inputFileNames;
}
