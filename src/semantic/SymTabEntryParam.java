package semantic;

public class SymTabEntryParam extends SymTabEntry{
    String type;

    public SymTabEntryParam() {
        super(Kind.PARAMETER);
    }

    public String toString() {
        String str = super.toString();
        str += " | " + "type: " + type;
        return str;
    }
}
