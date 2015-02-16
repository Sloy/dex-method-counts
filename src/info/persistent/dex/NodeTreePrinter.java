package info.persistent.dex;


import java.io.PrintStream;

public class NodeTreePrinter implements NodePrinter {

    private PrintStream out;

    public NodeTreePrinter(PrintStream out) {
        this.out = out;
    }


    private void output(MethodCountNode node, String indent) {
        if (indent.length() == 0) {
            out.println("<root>: " + node.count);
        }
        indent += "    ";
        for (String name : node.children.navigableKeySet()) {
            MethodCountNode child = node.children.get(name);
            out.println(indent + name + ": " + child.count);
            output(child, indent);
        }
    }

    @Override
    public void output(MethodCountNode node) {
        output(node, "");
    }
}