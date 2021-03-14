package grammar;

import java.io.BufferedReader;
import java.io.FileReader;

public class ParserTest {
    public static void main(String[] args) throws Exception{
        Grammar.initialize(new BufferedReader(new FileReader("src/grammar/LL1.paquet.grm")));
        Grammar.showGrammar();

        Grammar.productions.get(new Symbol("Prog", false));

        Parser parser = new Parser("src/grammar/idnesttest.atocc.tokenstream");
        System.out.println(parser.parse());
    }
}
