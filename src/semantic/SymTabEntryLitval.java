package semantic;

public class SymTabEntryLitval extends SymTabEntry{
    String value;

    public SymTabEntryLitval() {
        super(Kind.LITVAL);
    }

    public String toString() {
        String str = super.toString();
        str += " | " + "value: " + value;

        return str;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}