package semantic;

public class SymTabEntryFunc extends SymTabEntry{
    String returnType;

    public SymTabEntryFunc() {
        super(Kind.FUNCTION);
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String toString() {
        String str = super.toString();
        str += " | " + "returnType: " + returnType;

        return str;
    }
}
