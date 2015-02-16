package info.persistent.dex;

import com.google.gson.Gson;

public class JsonNodeFormatter implements NodeFormatter {

    private Gson jsonAdapter;

    public JsonNodeFormatter() {
        jsonAdapter = new Gson();
    }

    @Override
    public String formatNodeTree(MethodCountNode node) {
        return jsonAdapter.toJson(node);
    }
}
