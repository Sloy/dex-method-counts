package info.persistent.dex;


import java.io.PrintStream;

public class NodeOutputPrinter implements NodePrinter {

    private PrintStream out;

    public NodeOutputPrinter(PrintStream out) {
        this.out = out;
    }

    @Override
    public void output(DexMethodCounts.Node node, String indent) {
        if (indent.length() == 0) {
            out.println("<root>: " + node.count);
            DexMethodCounts.overallCount += node.count;
        }
        indent += "    ";
        for (String name : node.children.navigableKeySet()) {
            DexMethodCounts.Node child = node.children.get(name);
            out.println(indent + name + ": " + child.count);
            output(child, indent);
        }
    }
}
