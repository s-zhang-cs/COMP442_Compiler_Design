package semantic;

public class SymTabEntryVar extends SymTabEntry{
    String type;

    public SymTabEntryVar() {
        super(Kind.VARIABLE);
    }

    public String toString() {
        String str = super.toString();
        str += " | " + "type: " + type;
        return str;
    }
}
