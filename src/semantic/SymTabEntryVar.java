package semantic;

public class SymTabEntryVar extends SymTabEntry{
    String type;

    public SymTabEntryVar() {
        super(Kind.VARIABLE);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        String str = super.toString();
        str += " | " + "type: " + type;
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        else if(o instanceof SymTabEntryVar) {
            SymTabEntryVar s = (SymTabEntryVar) o;
            if(s.name.equals(this.name) && s.kind == this.kind && s.type.equals(this.type)) {
                return true;
            }
        }
        return false;
    }
}
