package grammar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrammarTest {
    public static void main(String[] args) throws Exception{
        Grammar.initialize(new BufferedReader(new FileReader("src/grammar/LL1.paquet.grm")));
        //Grammar.initialize(new BufferedReader(new FileReader("src/grammar/test.grm")));
        Grammar.showGrammar();

        Symbol s1 = new Symbol("VariableIdnest", false);
        s1.computeFirstSet(s1);
        s1.showFirstSet();
        s1.computeFollowSet(s1, new HashSet<Symbol>());
        s1.showFollowSet();

//        Symbol e = new Symbol("E", false);
//        Symbol ePrime = new Symbol("E'", false);
//        Symbol t = new Symbol("T", false);
//        Symbol tPrime = new Symbol("T'", false);
//        Symbol f = new Symbol("F", false);
//
//        List<Symbol> list = new ArrayList<>();
//
//        e.computeFirstSet(e);
//        e.showFirstSet();
//        System.out.println();
//        e.computeFollowSet(e);
//        e.showFollowSet();
//        System.out.println();
//
//        ePrime.computeFirstSet(ePrime);
//        ePrime.showFirstSet();
//        System.out.println();
//        ePrime.computeFollowSet(ePrime);
//        ePrime.showFollowSet();
//        System.out.println();
//
//        t.computeFirstSet(t);
//        t.showFirstSet();
//        System.out.println();
//        t.computeFollowSet(t);
//        t.showFollowSet();
//        System.out.println();
//
//        tPrime.computeFirstSet(tPrime);
//        tPrime.showFirstSet();
//        System.out.println();
//        tPrime.computeFollowSet(tPrime);
//        tPrime.showFollowSet();
//        System.out.println();
//
//        f.computeFirstSet(f);
//        f.showFirstSet();
//        System.out.println();
//        f.computeFollowSet(f);
//        f.showFollowSet();
//        System.out.println();
    }
}
