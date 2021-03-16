package grammar;

import lexer.Lexer;
import symbol.Symbol;

import java.io.FileReader;
import java.util.*;

public class Parser {

    private Symbol lookahead;
    private Lexer lexer;

    public Parser(String filePath) throws Exception{
        lexer = new Lexer(filePath);
    }

    public boolean parse() throws Exception{
        lookahead = nextToken();
        if(recursiveDescentParse(new Symbol("Prog", false)) && match(new Symbol("$", true))) {
            return true;
        }
        return false;
    }

    private boolean match(Symbol s) throws Exception{
        if(lookahead.equals(s)) {
            lookahead = nextToken();
            return true;
        }
        lookahead = nextToken();
        return false;
    }

    private boolean recursiveDescentParse(Symbol startSymbol) throws Exception{
        //---------------------------------debug purposes -------------------------------------
            System.out.print(lookahead.symbol + " ");

        if(lookahead.equals(new Symbol("read", true))) {
            int debug = 0; //debug purpose
        }
        //-------------------------------------------------------------------------------------
        boolean RHSNullable = false;
        //all the branches of form if(lookahead belongs to FIRST(RHS))
        for (List<Symbol> RHS : Grammar.productions.get(startSymbol)) {
            boolean currRHSNullable = true;
            Set<Symbol> first = startSymbol.computeFirstSetForSymbolString(RHS);
            if(first.contains(lookahead)) {
                boolean match = true;
                for(Symbol s : RHS) {
                    if(s.isTerminal) {
                        if(!match(s)) {
                            match = false;
                            break;
                        }
                    }
                    else {
                        if(!recursiveDescentParse(s)) {
                            match = false;
                            break;
                        }
                    }
                }
                return match;
            }
            if(!first.contains(new Symbol("EPSILON", true))) {
                currRHSNullable = false;
            }
            RHSNullable = RHSNullable || currRHSNullable;
        }
        //last branch of form if(lookahead belongs to FOLLOW(LHS)), only applies when LHS -> epsilon exists
        Set<Symbol> follow = startSymbol.computeFollowSet(startSymbol, new HashSet<Symbol>());
        if(RHSNullable && follow.contains(lookahead)) {
            return true;
        }
        return false;
    }

    public Symbol nextToken() throws Exception{
        Symbol s = null;
        if(!lexer.reachedEOF()) {
            s = lexer.nextToken();
        }
        else {
            s = new Symbol("$", "EOF", true);
        }
        return s;
    }

}
