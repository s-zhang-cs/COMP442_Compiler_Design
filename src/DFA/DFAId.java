package DFA;

public class DFAId extends DFA {
    public DFAId(String s) {
        super(s);
        addEnglishAlphabet();
        addNumeralAlphabet();
        addAlphabet('_');
        addErrorState();

        //initialize states
        for(int i = 0; i < 26 + 10 + 1; i++) {
            states.add(new State("s" + i));
        }

        startState = getStateByName(this, "s0");
        currentState = getStateByName(this, "s0");
        finalStates.add(getStateByName(this, "s1"));

        //add transitions
        //s0
        for(char c = 'a'; c <= 'z'; c++) {
            getStateByName(this, "s0").addTransition(new Transition(c, getStateByName(this, "s1")));
        }
        for(char c = 'A'; c <= 'Z'; c++) {
            getStateByName(this, "s0").addTransition(new Transition(c, getStateByName(this, "s1")));
        }

        //s1
        for(char c = 'a'; c <= 'z'; c++) {
            getStateByName(this, "s1").addTransition(new Transition(c, getStateByName(this, "s1")));
        }
        for(char c = 'A'; c <= 'Z'; c++) {
            getStateByName(this, "s1").addTransition(new Transition(c, getStateByName(this, "s1")));
        }
        for(char c = '0'; c <= '9'; c++) {
            getStateByName(this, "s1").addTransition(new Transition(c, getStateByName(this, "s1")));
        }
        getStateByName(this, "s1").addTransition(new Transition('_', getStateByName(this, "s1")));

        markStates();
    }
}
