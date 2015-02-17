package info.persistent.dex;


public class TreeNodeFormatter implements NodeFormatter {

    private StringBuilder stringBuilder;

    public TreeNodeFormatter() {
        this.stringBuilder = new StringBuilder();
    }

    private String formatNodeTree(MethodCountNode node, String indent) {
        if (indent.length() == 0) {
            stringBuilder.append("<root>: ").append(node.count).append("\n");
        }
        indent += "    ";
        for (MethodCountNode child : node.children) {
            stringBuilder.append(indent).append(child.name).append(": ").append(child.count).append("\n");
            formatNodeTree(child, indent);
        }
        return stringBuilder.toString();
    }

    @Override
    public String formatNodeTree(MethodCountNode node) {
        return formatNodeTree(node, "");
    }
}