package lexer;

import java.util.Collection;
import java.util.HashSet;

public class Keywords {
    public static Collection<String> keywords = new HashSet<>();
    static {
        keywords.add("if");
        keywords.add("then");
        keywords.add("else");
        keywords.add("integer");
        keywords.add("float");
        keywords.add("string");
        keywords.add("void");
        keywords.add("public");
        keywords.add("private");
        keywords.add("func");
        keywords.add("var");
        keywords.add("class");
        keywords.add("while");
        keywords.add("read");
        keywords.add("write");
        keywords.add("return");
        keywords.add("main");
        keywords.add("inherits");
        keywords.add("break");
        keywords.add("continue");
    }
}
