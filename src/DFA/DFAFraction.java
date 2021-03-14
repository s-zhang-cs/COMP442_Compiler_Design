package DFA;

public class DFAFraction extends DFA{
    public DFAFraction(String s) {
        super(s);
        addNumeralAlphabet();
        addAlphabet('.');

        addErrorState();

        //initialize states
        for(int i = 0; i < 7; i++) {
            addState(new State("s" + i));
        }
        startState = getStateByName(this, "s0");
        currentState = getStateByName(this, "s0");
        finalStates.add(getStateByName(this, "s2"));
        finalStates.add(getStateByName(this, "s3"));
        finalStates.add(getStateByName(this, "s5"));
        finalStates.add(getStateByName(this, "s6"));


        //add transitions
        //s0
        getStateByName(this, "s0").addTransition(new Transition('.', getStateByName(this, "s1")));
        //s1
        getStateByName(this, "s1").addTransition(new Transition('0', getStateByName(this, "s2")));
        for(char c = '1'; c <= '9'; c++) {
            getStateByName(this, "s1").addTransition(new Transition(c, getStateByName(this, "s3")));
        }
        //s2
        getStateByName(this, "s2").addTransition(new Transition('0', getStateByName(this, "s4")));
        for(char c = '1'; c <= '9'; c++) {
            getStateByName(this, "s2").addTransition(new Transition(c, getStateByName(this, "s5")));
        }
        //s3
        getStateByName(this, "s3").addTransition(new Transition('0', getStateByName(this, "s4")));
        for(char c = '1'; c <= '9'; c++) {
            getStateByName(this, "s3").addTransition(new Transition(c, getStateByName(this, "s6")));
        }
        //s4
        getStateByName(this, "s4").addTransition(new Transition('0', getStateByName(this, "s4")));
        for(char c = '1'; c <= '9'; c++) {
            getStateByName(this, "s4").addTransition(new Transition(c, getStateByName(this, "s5")));
        }
        //s5
        getStateByName(this, "s5").addTransition(new Transition('0', getStateByName(this, "s4")));
        for(char c = '1'; c <= '9'; c++) {
            getStateByName(this, "s5").addTransition(new Transition(c, getStateByName(this, "s5")));
        }
        //s6
        getStateByName(this, "s6").addTransition(new Transition('0', getStateByName(this, "s4")));
        for(char c = '1'; c <= '9'; c++) {
            getStateByName(this, "s6").addTransition(new Transition(c, getStateByName(this, "s6")));
        }

        markStates();
    }
}
