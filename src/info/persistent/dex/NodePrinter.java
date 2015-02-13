package info.persistent.dex;

public interface NodePrinter {
    void output(DexMethodCounts.Node node, String indent);
}
