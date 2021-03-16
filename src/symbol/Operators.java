package symbol;

import java.util.Collection;
import java.util.HashSet;

public class Operators {
    public static Collection<String> operators = new HashSet<>();
    static {
        operators.add("+");
        operators.add("-");
        operators.add("*");
        operators.add("/");
        operators.add("|");
        operators.add("&");
        operators.add("!");
        operators.add("?");
        operators.add("(");
        operators.add(")");
        operators.add("{");
        operators.add("}");
        operators.add("[");
        operators.add("]");
        operators.add(";");
        operators.add(",");
        operators.add(".");
        operators.add(":");
        operators.add("::");
        operators.add("=");
        operators.add("==");
        operators.add(">");
        operators.add(">=");
        operators.add("<");
        operators.add("<>");
        operators.add("<=");
    }
}
