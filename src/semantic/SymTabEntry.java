package semantic;

import symbol.Symbol;

import java.util.Objects;

public class SymTabEntry {

    public enum Kind {
        VARIABLE,
        CLASS,
        FUNCTION,
        PARAMETER
    }

    String name;
    Kind kind;

    public SymTabEntry(Kind kind) {
        this.kind = kind;
    }

    public String toString() {
        return "name: " + name + " | " +  "kind: " + kind;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        else if(o instanceof SymTabEntry) {
            SymTabEntry s = (SymTabEntry) o;
            if(s.name.equals(this.name) && s.kind == this.kind) {
                return true;
            }
        }
        return false;
    }

}
