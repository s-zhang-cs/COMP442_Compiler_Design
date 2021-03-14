package DFA;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class DFAFloat {

    String name;
    DFAFraction dfaFraction;
    DFAInteger dfaInteger;
    compositionState curr;

    //integer . fraction . [e [+|-] integer]
    enum compositionState {
        INTEGER1, FRACTION, E, PLUSMINUS, INTEGER2
    }

    public DFAFloat (String s) {
        name = s;
        dfaFraction = new DFAFraction("DFAFraction");
        dfaInteger = new DFAInteger("DFAInteger");
        curr = compositionState.INTEGER1;
    }

    public boolean transition(char c) {
        boolean res = false;
        switch(curr) {
            case INTEGER1:
                res = dfaInteger.transition(dfaInteger, c);
                if (res) {
                    break;
                }
                else {
                    curr = compositionState.FRACTION;
                }
            case FRACTION:
                res = dfaFraction.transition(dfaFraction, c);
                if(res) {
                    break;
                }
                else {
                    curr = compositionState.E;
                }
            case E:
                if(c == 'e') {
                    res = true;
                    curr = compositionState.PLUSMINUS;
                    break;
                }
                else {
                    res = false;
                    break;
                }
            case PLUSMINUS:
                if(c == '+' || c == '-') {
                    res = true;
                    curr = compositionState.INTEGER2;
                    dfaInteger.resetDFA(dfaInteger);
                    break;
                }
                else {
                    curr = compositionState.INTEGER2;
                    dfaInteger.resetDFA(dfaInteger);
                }
            case INTEGER2:
                res = dfaInteger.transition(dfaInteger, c);
        }
        return res;
    }

    public boolean evaluateInput(String s) {
        CharacterIterator ci = new StringCharacterIterator(s);
        while(ci.current() != CharacterIterator.DONE) {
            if(transition(ci.current()) == false) {
                resetDFA();
                return false;
            }
            ci.next();
        }
        resetDFA();
        return true;
    }

    public void resetDFA() {
        dfaInteger.resetDFA(dfaInteger);
        dfaFraction.resetDFA(dfaFraction);
        curr = compositionState.INTEGER1;
    }

}
