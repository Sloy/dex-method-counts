package info.persistent.dex;

import java.util.NavigableMap;
import java.util.TreeMap;

public class MethodCountNode {
    int count = 0;
    NavigableMap<String, MethodCountNode> children = new TreeMap<>();
}
