package lexer;

import java.security.Key;

public class Token {
    private String type;
    private String lexeme;
    private int length;

    public Token(String type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
        length = lexeme.length();
        if(isKeyword(lexeme)) {
            this.type = "Keyword";
        }
    }

    public String getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLength() {
        return length;
    }

    public boolean isEmpty() { return type.isEmpty() && lexeme.isEmpty() && length == 0; }

    public String toString() {
        return "[" + type + ": " + lexeme + "]";
    }

    public boolean isKeyword(String lexeme) {
        return Keywords.keywords.contains(lexeme);
    }
}
