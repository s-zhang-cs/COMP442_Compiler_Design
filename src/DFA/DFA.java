package DFA;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

public class DFA {
    Collection<Character> alphabet;
    State startState;
    State currentState;
    Collection<State> states;
    Collection<State> finalStates;

    String name;

    public DFA(String s) {
        alphabet = new HashSet<Character>();
        states = new HashSet<State>();
        finalStates = new HashSet<State>();
        name = s;
    }

    public void markStates() {
        for(State s : states) {
            s.rename(name + "_" + s.name);
        }
    }

    public void addAlphabet(char c) {
        alphabet.add(c);
    }

    public void addEnglishAlphabet() {
        for(char c = 'a'; c <= 'z'; c++) {
            alphabet.add(c);
        }
        for(char c = 'A'; c <= 'Z'; c++) {
            alphabet.add(c);
        }
    }

    public void addNumeralAlphabet() {
        for(char c = '0'; c <= '9'; c++) {
            alphabet.add(c);
        }
    }

    public void addState(State s) {
        states.add(s);
    }

    public void addFinalState(State s) {
        finalStates.add(s);
    }

    public void setStartState(State s) {
        startState = s;
    }

    public void setCurrentState(State s) {
        currentState = s;
    }

    public boolean evaluateInput(DFA dfa, String s) {
        CharacterIterator ci = new StringCharacterIterator(s);
        while(ci.current() != CharacterIterator.DONE) {
            if(transition(dfa, ci.current()) == false) {
                resetDFA(dfa);
                return false;
            }
            ci.next();
        }
        if(finalStates.contains(dfa.currentState)) {
            resetDFA(dfa);
            return true;
        }
        resetDFA(dfa);
        return false;
    }

    public void resetDFA(DFA dfa) {
        dfa.currentState = dfa.startState;
    }

    public DFA getUnionOfTwoDFA(DFA dfa1, DFA dfa2, String s) {
        DFA union = new DFA(s);
        //create alphabet
        for(Character c : alphabet) {
            union.addAlphabet(c);
        }
        //create new states
        for(State i : dfa1.states) {
            for(State j : dfa2.states) {
               State newState = new State((i.name + "___" + j.name));
               union.addState(newState);
               if(isStartState(dfa1, i) && isStartState(dfa2, j)){
                   union.setStartState(newState);
                   union.setCurrentState(newState);
               }
               if(isFinalState(dfa1, i) || isFinalState(dfa2, j)) {
                   union.addFinalState(newState);
               }
            }
        }
        //create new transitions
        for(State i : dfa1.states) {
            for(State j : dfa2.states) {
                for(Character c : alphabet) {
                    State iNext = i.getNextStateFromInput(c);
                    State jNext = j.getNextStateFromInput(c);
                    if(iNext == null) {
                        iNext = dfa1.getStateByName(dfa1, dfa1.name + "_errorState");
                    }
                    if(jNext == null) {
                        jNext = dfa2.getStateByName(dfa2, dfa2.name + "_errorState");
                    }
                    State ij = union.getStateByName(union, i.name + "___" + j.name);
                    State ijNext = union.getStateByName(union, iNext.name + "___" + jNext.name);
                    ij.addTransition(new Transition(c, ijNext));
                }
            }
        }
        union.getStateByName(union, dfa1.name + "_errorState___" + dfa2.name + "_errorState").rename(s + "_errorState");
        return union;
    }

    public State getStateByName(DFA dfa, String s) {
        State state = dfa.states.stream()
            .filter(curr -> s.equals(curr.name))
            .findAny()
            .orElse(null);
        return state;
    }

    public boolean isStartState(DFA dfa, State s) {
        return dfa.startState == s;
    }

    public boolean isFinalState(DFA dfa, State s) {
        return dfa.finalStates.contains(s);
    }

    public boolean transition(DFA dfa, char c) {
        if(dfa.currentState == null) {
            return false;
        }
        for(int i = 0; i < currentState.transitions.size(); i++) {
            Transition currTransition = dfa.currentState.transitions.get(i);
            if(currTransition.input == c) {
                dfa.currentState = currTransition.nextState;
                return true;
            }
        }
        dfa.currentState = getStateByName(this, name + "_errorState");
        return false;
    }

    public void addErrorState() {
        State errorState = new State("errorState");
        for(Character c : alphabet) {
            errorState.addTransition(new Transition(c, errorState));
        }
        states.add(errorState);
    }

    public String toString() {
        String name = "{" + this.name + "}" + "\r\n";
        String alphabet = "alphabet: " + Arrays.toString(this.alphabet.toArray()) + "\r\n";
        String startState = "startState: " + this.startState.name + "\r\n";
        String currentState = "currentState: " + this.currentState.name + "\r\n";
        String states = "states: " + Arrays.toString(this.states.toArray()) + "\r\n";
        String finalStates = "final states: " + Arrays.toString(this.finalStates.toArray()) + "\r\n";

        return name + alphabet + startState + currentState + states + finalStates;
    }

}
