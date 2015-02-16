package info.persistent.dex;

import com.google.gson.Gson;

import java.io.PrintStream;

public class NodeJsonPrinter implements NodePrinter {

    private Gson jsonAdapter;
    private PrintStream out;

    public NodeJsonPrinter(PrintStream out) {
        this.out = out;
        jsonAdapter = new Gson();
    }

    @Override
    public void output(MethodCountNode node) {
        String result = jsonAdapter.toJson(node);
        out.print(result);
    }
}
