package grammar;

import symbol.Symbol;

import java.io.BufferedReader;
import java.util.*;

//54 start symbols
public class Grammar {
    public static Map<Symbol, List<List<Symbol>>> productions = new HashMap<>();
    public static Map<Symbol, Set<Symbol>> first = new HashMap<>();
    public static Map<Symbol, Set<Symbol>> follow = new HashMap<>();
    public static Symbol startSymbol = new Symbol("START", false);

    public static void initialize(BufferedReader b) throws Exception{
        String line;
        Symbol left = null;
        List<List<Symbol>> right = new ArrayList<>();

        while((line = b.readLine()) != null){
            String[] lineParts = line.split(" ");
            List<Symbol> rightCurr = new ArrayList<>();
            boolean startSymbol = true;
            for(String part: lineParts) {
                if(part.startsWith("<") && part.endsWith(">")) {
                    if(startSymbol) {
                        left = new Symbol(part.substring(1, part.length() - 1), false);
                    }
                    else {
                        rightCurr.add(new Symbol(part.substring(1, part.length() - 1), false));
                    }
                }
                else if(part.equals("::=")) {
                    startSymbol = false;
                }
                else if(part.startsWith("\'") && part.endsWith("\'")) {
                    rightCurr.add(new Symbol(part.substring(1, part.length() - 1), true));
                }
                else if(part.equals("EPSILON")) {
                    rightCurr.add(new Symbol("EPSILON", true));
                }
            }
            if(!rightCurr.isEmpty()) {
                right.add(rightCurr);
            }
            if(line.equals("") && left != null){
                productions.put(left, right);
                left = null;
                right = new ArrayList<>();
            }
        }
    }

    public static void showGrammar() {
        System.out.println(productions);
    }
}
