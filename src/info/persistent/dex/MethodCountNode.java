package info.persistent.dex;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class MethodCountNode {
    int count = 0;
    String name = "";
    List<MethodCountNode> children = new ArrayList<>();
}
