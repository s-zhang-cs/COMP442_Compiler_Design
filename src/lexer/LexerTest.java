package lexer;

import symbol.Symbol;

public class LexerTest {
    public static void main(String[] args) throws Exception{
        Lexer lexer = new Lexer("resources/lexer/polynomial.src");

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~testing the lexer functionalities~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        while(true) {
            Symbol t = lexer.nextToken();
            System.out.println(t);
            if(t == null || t.equals(new Symbol("$", true))) {
                break;
            }
        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
