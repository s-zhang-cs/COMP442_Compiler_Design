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
        INTEGER1, INTEGER1_SUCCESS, FRACTION, FRACTION_SUCCESS, E, PLUSMINUS, INTEGER2
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
                    curr = compositionState.INTEGER1_SUCCESS;
                    break;
                }
                else {
                    return false;
                }
            case INTEGER1_SUCCESS:
                res = dfaInteger.transition(dfaInteger, c);
                if (res) {
                    return true;
                }
                else {
                    curr = compositionState.FRACTION;
                }
            case FRACTION:
                res = dfaFraction.transition(dfaFraction, c);
                if(res) {
                    curr = compositionState.FRACTION_SUCCESS;
                    break;
                }
                else {
                    return false;
                }
            case FRACTION_SUCCESS:
                res = dfaFraction.transition(dfaFraction, c);
                if(res) {
                    curr = compositionState.FRACTION_SUCCESS;
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
                    return false;
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
        char currChar = ci.current();
        while(ci.current() != CharacterIterator.DONE) {
            currChar = ci.current();
            if(transition(currChar) == false) {
                resetDFA();
                return false;
            }
            ci.next();
        }
        resetDFA();
        if(currChar == 'e' || currChar == 'E') {
            return false;
        }
        return true;
    }

    public void resetDFA() {
        dfaInteger.resetDFA(dfaInteger);
        dfaFraction.resetDFA(dfaFraction);
        curr = compositionState.INTEGER1;
    }

}
