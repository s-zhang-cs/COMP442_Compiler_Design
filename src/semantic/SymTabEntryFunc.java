package semantic;

public class SymTabEntryFunc extends SymTabEntry{
    String returnType;

    public SymTabEntryFunc(Kind kind) {
        super(kind);
    }

    public String toString() {
        String str = super.toString();
        str += " | " + "returnType: " + returnType;

        return str;
    }
}
