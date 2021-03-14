package DFA;

public class DFAString extends DFA{
    public DFAString(String s) {
        super(s);
        addNumeralAlphabet();
        addEnglishAlphabet();
        addAlphabet('_');
        addAlphabet(' ');
        addAlphabet('\"');

        addErrorState();

        //initialize states
        for(int i = 0; i < 3; i++) {
            addState(new State("s" + i));
        }
        startState = getStateByName(this, "s0");
        currentState = getStateByName(this, "s0");
        finalStates.add(getStateByName(this, "s2"));

        //add transitions
        //s0
        getStateByName(this, "s0").addTransition(new Transition('\"', getStateByName(this, "s1")));
        //s1
        for(char c : alphabet) {
            if(c != '\"') {
                getStateByName(this, "s1").addTransition(new Transition(c, getStateByName(this, "s1")));
            }
        }
        getStateByName(this,"s1").addTransition(new Transition('\"', getStateByName(this, "s2")));
        markStates();
    }
}

