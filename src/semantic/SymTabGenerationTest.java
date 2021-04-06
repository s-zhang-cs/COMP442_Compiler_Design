package semantic;

import grammar.Grammar;
import java.io.BufferedReader;
import java.io.FileReader;

public class SymTabGenerationTest {
    public static void main(String[] args) throws Exception {
        Grammar.initialize(new BufferedReader(new FileReader("resources/grammar/LL1.paquet.grm")));
        SymTabGeneration s = new SymTabGeneration("resources/lexer/polynomial.src");
        System.out.println(s.source);
        s.generate();
        s.recordSymTabs();
        int debug = 0;
    }
}
