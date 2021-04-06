package semantic;

import java.util.ArrayList;
import java.util.List;

public class SymbolTable {
    String name;
    List<SymTabEntry> entries;

    public SymbolTable(String s) {
        name = s;
        entries = new ArrayList<SymTabEntry>();
    }

    public String getName() {
        return name;
    }

    public void addEntry(SymTabEntry entry) {
        entries.add(entry);
    }

    public String toString() {
        String res = name + ":\n";
        for(SymTabEntry s : entries) {
            res += s.toString() + "\n";
        }
        return res;
    }

    public boolean containsValue(SymTabEntry s) {
        return entries.contains(s);
    }
}
