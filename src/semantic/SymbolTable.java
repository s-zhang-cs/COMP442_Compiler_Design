package semantic;

import java.util.ArrayList;
import java.util.List;

public class SymbolTable {
    String name;
    int offset;
    List<SymTabEntry> entries;

    public SymbolTable(String s) {
        if(s != null) {
            name = s.toUpperCase();
        }
        entries = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addEntry(SymTabEntry entry) {
        entries.add(entry);
    }

    public boolean containsEntry(SymTabEntry s) {
        return entries.contains(s);
    }

    public SymTabEntry getEntry(SymTabEntry entry) {
        for(SymTabEntry i : entries) {
            if(i.equals(entry)) {
                return i;
            }
        }
        return null;
    }

    public String toString() {
        String res = "[" + name + "]" + ":\n";
        for(SymTabEntry s : entries) {
            res += s.toString() + "\n";
        }
        return res;
    }

}
