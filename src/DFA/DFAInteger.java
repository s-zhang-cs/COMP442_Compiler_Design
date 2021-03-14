package DFA;

public class DFAInteger extends DFA {

    public DFAInteger(String s) {
        super(s);
        addNumeralAlphabet();
        addErrorState();

        //initialize states
        State s0 = new State("s0");
        states.add(s0);
        State s1 = new State("s1");
        states.add(s1);
        State s2 = new State("s2");
        states.add(s2);

        startState = s0;
        currentState = s0;
        finalStates.add(s1);
        finalStates.add(s2);

        //add transitions
        //s0
        Transition s0_1 = new Transition('0', s1);
        Transition s0_2 = new Transition('1', s2);
        Transition s0_3 = new Transition('2', s2);
        Transition s0_4 = new Transition('3', s2);
        Transition s0_5 = new Transition('4', s2);
        Transition s0_6 = new Transition('5', s2);
        Transition s0_7 = new Transition('6', s2);
        Transition s0_8 = new Transition('7', s2);
        Transition s0_9 = new Transition('8', s2);
        Transition s0_10 = new Transition('9', s2);
        s0.addTransition(s0_1);
        s0.addTransition(s0_2);
        s0.addTransition(s0_3);
        s0.addTransition(s0_4);
        s0.addTransition(s0_5);
        s0.addTransition(s0_6);
        s0.addTransition(s0_7);
        s0.addTransition(s0_8);
        s0.addTransition(s0_9);
        s0.addTransition(s0_10);

        //s2
        Transition s2_1 = new Transition('0', s2);
        Transition s2_2 = new Transition('1', s2);
        Transition s2_3 = new Transition('2', s2);
        Transition s2_4 = new Transition('3', s2);
        Transition s2_5 = new Transition('4', s2);
        Transition s2_6 = new Transition('5', s2);
        Transition s2_7 = new Transition('6', s2);
        Transition s2_8 = new Transition('7', s2);
        Transition s2_9 = new Transition('8', s2);
        Transition s2_10 = new Transition('9', s2);
        s2.addTransition(s2_1);
        s2.addTransition(s2_2);
        s2.addTransition(s2_3);
        s2.addTransition(s2_4);
        s2.addTransition(s2_5);
        s2.addTransition(s2_6);
        s2.addTransition(s2_7);
        s2.addTransition(s2_8);
        s2.addTransition(s2_9);
        s2.addTransition(s2_10);

        markStates();
    }
}
