package grammar;

import symbol.Symbol;

import java.io.BufferedReader;
import java.io.FileReader;

public class ParserTest {
    public static void main(String[] args) throws Exception{
        Grammar.initialize(new BufferedReader(new FileReader("resources/grammar/LL1.paquet.grm")));

        Parser parser = new Parser("resources/lexer/polynomial.src");
        System.out.println(parser.parse());
        parser.showAST();
    }
}
