package semantic;

import java.util.ArrayList;
import java.util.List;

public class SymTabEntryClass extends SymTabEntry{

    List<String> inherList;

    public SymTabEntryClass() {
        super(Kind.CLASS);
        inherList = new ArrayList<>();
    }

    public String toString() {
        String str = super.toString();
        if(!inherList.isEmpty()) {
            str += " | inherList: ";
        }
        for(String s : inherList) {
            str += s + " ";
        }
        return str;
    }
}

