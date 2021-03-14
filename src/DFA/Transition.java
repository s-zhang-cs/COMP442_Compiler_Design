package DFA;

public class Transition {
    char input;
    State nextState;

    public Transition(char c, State s) {
        input = c;
        nextState = s;
    }

    public String toString() {
        return input + " -> " + nextState.name + "\r\n";
    }
}
