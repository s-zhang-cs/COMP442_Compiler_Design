package symbol;

import grammar.Grammar;

import java.util.*;

public class Symbol {
    public String symbol;
    public String lexeme;
    public boolean isTerminal;
    Set<Symbol> firstSet;
    Set<Symbol> followSet;

    public Symbol (String symbol, boolean isTerminal) {
        this.symbol = symbol;
        this.isTerminal = isTerminal;
        this.firstSet = new HashSet<>();
        this.followSet = new HashSet<>();
    }

    public Symbol (String symbol, String lexeme, boolean isTerminal) {
        this.symbol = symbol;
        this.lexeme = lexeme;
        this.isTerminal = isTerminal;
        convertKeyword(lexeme);
        this.firstSet = new HashSet<>();
        this.followSet = new HashSet<>();
    }

    private void convertKeyword(String lexeme) {
        if(Keywords.keywords.contains(lexeme)) {
            this.symbol = this.lexeme;
        }
        if(this.symbol.equals("::")) {
            this.symbol = "sr";
        }
        else if(this.symbol.equals("=")) {
            this.symbol = "assign";
        }
        else if(this.symbol.equals("<=")) {
            this.symbol = "leq";
        }
        else if(this.symbol.equals("<")) {
            this.symbol = "lt";
        }
        else if(this.symbol.equals(">=")) {
            this.symbol = "geq";
        }
        else if(this.symbol.equals(">")) {
            this.symbol = "gt";
        }
        else if(this.symbol.equals("!")) {
            this.symbol = "not";
        }
        else if(this.symbol.equals("String")) {
            this.symbol = "stringlit";
        }
    }

    public boolean isEmpty() {
        return symbol.isEmpty();
    }

    public Set<Symbol> computeFirstSet(Symbol s){
        Map<Symbol, Set<Symbol>> first = Grammar.first;
        //if first is already defined for this symbol
        if(first.containsKey(s)) {
            s.firstSet = first.get(s);
            return s.firstSet;
        }
        //if first needs to be computed for this symbol
        else {
            Set<Symbol> newFirst = new HashSet<>();
            //rule 1 -> if ((A is T) v (A is epsilon)) FIRST(A) takes {A}
            if(s.isTerminal) {
                s.firstSet.add(new Symbol(s.symbol, true));
            }
            //rule 2 -> if ((A is N) ^ (A -> S1 S2 ... Sk ))
            else {
                for (List<Symbol> rule : Grammar.productions.get(s)) {
                    //rule 2.1 -> FIRST(A) takes FIRST(S1) - epsilon
                    //rule 2.2 -> if (FIRST(S1) FIRST(S2) ... FIRST(Si)) contain epsilon, FIRST(A) takes FIRST(S(i+1))
                    boolean allEpsilon = true;
                    for(Symbol i : rule) {
                        Set<Symbol> iFirst = i.computeFirstSet(i);
                        s.firstSet.addAll(iFirst);
                        //arriving at S(i+1)
                        if(!iFirst.contains(new Symbol("EPSILON", true))) {
                            allEpsilon = false;
                            break;
                        }
                    }
                    //rule 2.3 -> if (FIRST(S1) FIRST(S2) ... FIRST(Sk)) is epsilon, FIRST(A) takes epsilon
                    if(allEpsilon) {
                        s.firstSet.add(new Symbol("EPSILON", true));
                    }
                    else {
                        s.firstSet.remove(new Symbol("EPSILON", true));
                    }
                }
            }
            if(!Grammar.first.containsKey(s)) {
                Grammar.first.put(s, s.firstSet);
            }
            return s.firstSet;
        }
    }

    public Set<Symbol> computeFirstSetForSymbolString(List<Symbol> symbolString) {
        Set<Symbol> first = new HashSet<>();
        for(Symbol s : symbolString) {
            Set<Symbol> f = computeFirstSet(s);
            first.addAll(f);
            if(!f.contains(new Symbol("EPSILON", true))) {
                //this line is needed to eliminate epsilon from first set unless all are nullable
                first.remove(new Symbol("EPSILON", true));
                return first;
            }
        }
        return first;
    }

    public void showFirstSet() {
        System.out.print("First set of " + symbol + ": ");
        for(Symbol s : firstSet) {
            System.out.print(s + " ");
        }
    }

    public Set<Symbol> computeFollowSet(Symbol s, Set<Symbol> seen) {
        //used to prevent infinite recursion when 2 nonterminals' follow sets require each other
        seen.add(s);
        Map<Symbol, Set<Symbol>> follow = Grammar.follow;
        //if follow is already defined for this symbol
        if(follow.containsKey(s)) {
            s.followSet = follow.get(s);
            return s.followSet;
        }
        //if follow needs to be computed for this symbol
        else {
            Set<Symbol> newFollow = new HashSet<>();
            //follow set for terminal is undefined
            if(s.isTerminal){}
            else{
                //rule 1: if(A == S) then FOLLOW(A) takes '$'/end of program symbol
                if(s.equals(Grammar.startSymbol)){
                    s.followSet.add(new Symbol("$", true));
                }
                for(Map.Entry<Symbol, List<List<Symbol>>> entry : Grammar.productions.entrySet()) {
                    Symbol left = entry.getKey();
                    List<List<Symbol>> right = entry.getValue();
                    for(List<Symbol> rule : right) {
                        List<Symbol> alpha = new ArrayList<>();
                        Symbol A = null;
                        List<Symbol> beta = new ArrayList<>();
                        boolean seenS = false;
                        for(Symbol i : rule) {
                            if(!i.equals(s)){
                                if(!seenS) {
                                    //not used in computation, but add for debug purposes
                                    alpha.add(i);
                                }
                                else {
                                    beta.add(i);
                                }
                            }
                            else {
                                //not used in computation, but add for debug purposes
                                A = s;
                                seenS = true;
                            }
                        }
                        Set<Symbol> firstBeta = null;
                        //rule 2: if(B->alpha A beta) then FOLLOW(A) takes (FIRST(beta) - epsilon)
                        if(seenS && !beta.isEmpty()) {
                            firstBeta = computeFirstSetForSymbolString(beta);
                            s.followSet.addAll(firstBeta);
                        }
                        //rule 3: if( (B->alpha A beta) ^ FIRST(beta) => epsilon ) then FOLLOW(A) takes FOLLOW(B)
                        //n.b. FIRST(beta) definition does not include epsilon unless all are nullable
                        if((seenS && beta.isEmpty()) || (seenS && !beta.isEmpty() && firstBeta.contains(new Symbol("EPSILON", true)))) {
                            //this check is needed to prevent infinite recursion to include one's own followset
                            if(!seen.contains(left)) {
//                            if(!left.equals(s)) {
                                Set<Symbol> followB = computeFollowSet(left, seen);
                                s.followSet.addAll(followB);
                            }
                        }
                    }
                }
            }
        }
        s.followSet.remove(new Symbol("EPSILON", true));
        return s.followSet;
    }

    public void showFollowSet() {
        System.out.print("Follow set of " + symbol + ": ");
        for(Symbol s : followSet) {
            System.out.print(s + " ");
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        else if(o instanceof Symbol) {
            Symbol s = (Symbol) o;
            if(s.symbol.equals(this.symbol) && s.isTerminal == this.isTerminal) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, isTerminal);
    }

    @Override
    public String toString() {
        String s = symbol;
        if(isTerminal) {
            s += " -> " + lexeme;
        }
        else {
            s += " -> nonTerminal";
        }
        return s;
    }
}
