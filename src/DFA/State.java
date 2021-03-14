package DFA;

import java.util.ArrayList;

public class State {
    String name;
    ArrayList<Transition> transitions;

    public State(String s) {
        name = s;
        transitions = new ArrayList<Transition>();
    }

    public State(String s, ArrayList<Transition> t) {
        name = s;
        transitions = t;
    }

    public void addTransition(Transition t) {
        transitions.add(t);
    }

    public State getNextStateFromInput(char c) {
        for(int i = 0; i < transitions.size(); i++) {
            Transition currTransition = transitions.get(i);
            if(currTransition.input == c) {
                return currTransition.nextState;
            }
        }
        return null;
    }

    public void rename(String s) {
        name = s;
    }

    public String toString() {
        String num = "\r\n" + "state name: " + this.name + "\r\n";
        String transitions = "";
        if(this.transitions != null) {
            for (Transition t : this.transitions) {
                transitions += t;
            }
        }
        return num + transitions;
    }
}
