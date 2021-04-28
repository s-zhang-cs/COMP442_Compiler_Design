package semantic;

public class SymTabEntry {

    public enum Kind {
        VARIABLE,
        CLASS,
        FUNCTION,
        PARAMETER,
        LITVAL,
        UNINITIALIZED,
        TEMPVAR
    }

    String name;
    Kind kind;
    int memSize;
    int memOffset;
    String scope;
    SymbolTable link;

    public SymTabEntry(Kind kind) {
        this.kind = kind;
    }

    public String toString() {
        if(scope == null) {
            String s = "name: " + name + "(" + "null" + ")" + " | " +  "kind: " + kind + " | " + "memSize: " + memSize + " | "
                    + "memOffset: " + memOffset;
            if(link != null) {
                s +=  " | " + "link: " + link.getName();
            }
            else {
                s += " | " + "link: null";
            }
            return s;
        }
        else {
            String s = "name: " + name + "(" + scope + ")" + " | " +  "kind: " + kind + " | " + "memSize: " + memSize + " | "
                    + "memOffset: " + memOffset;
            if(link != null) {
                s +=  " | " + "link: " + link.getName();
            }
            else {
                s += " | " + "link: null";
            }
            return s;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public int getMemSize() {
        return memSize;
    }

    public void setMemSize(int size) {
        memSize = size;
    }

    public int getMemOffset() {
        return memOffset;
    }

    public void setMemOffset(int offset) {
        memOffset = offset;
    }

    public SymbolTable getLink() {
        return link;
    }

    public void setLink(SymbolTable link) {
        this.link = link;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
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
