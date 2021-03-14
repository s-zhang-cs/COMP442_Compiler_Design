package lexer;
import buffer.Buffer;
import DFA.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Lexer {
    Buffer buffer;
    ArrayList<Token> matchingLexemes;
    DFAFloat dfaFloat;
    DFAInteger dfaInteger;
    DFAId dfaId;
    DFAString dfaString;
    int line;

    public Lexer (String filePath) throws Exception{
        buffer = new Buffer(new BufferedReader(new FileReader(filePath)));
        matchingLexemes = new ArrayList<>();
        dfaFloat = new DFAFloat("DFAFloat");
        dfaInteger = new DFAInteger("DFAInteger");
        dfaId = new DFAId("DFAId");
        dfaString = new DFAString("DFAString");
        line = 1;
    }

    private void removeWhiteSpace(Buffer buffer) throws Exception{
        while(true) {
            char peek = buffer.peek();
            if(peek == ' ' || peek == '\t' || peek == '\r') {
                buffer.extend();
            }
            else if(peek == '\n') {
                buffer.extend();
                line++;
            }
            else {
                break;
            }
        }
    }

    public void removeSingleLineComment(Buffer buffer) throws Exception{
        int state = 0;
        while(true) {
            char peek = buffer.peek();
            switch(state) {
                case 0:
                    if(peek == '/') {
                        state = 1;
                        buffer.extend();
                        continue;
                    }
                    else {
                        break;
                    }
                case 1:
                    if(peek == '/') {
                        state = 2;
                        buffer.extend();
                        continue;
                    }
                    else {
                        buffer.retract();
                        break;
                    }
                case 2:
                    if(peek != '\n' && peek != (char)65535) {
                        buffer.extend();
                        continue;
                    }
                    else {
                        if(peek == '\n') {
                            buffer.extend();
                            line++;
                        }
                        break;
                    }
            }
            break;
        }
    }

    private void removeMultiLineComment(Buffer buffer) {

    }

    public Token nextToken() throws Exception{
        String currLexeme;

        //remove space and comment
        while(true) {
            removeWhiteSpace(buffer);
            removeSingleLineComment(buffer);
            removeMultiLineComment(buffer);
            if(buffer.peek() != ' ' && buffer.peek() != '\t' && buffer.peek() != '\r' && buffer.peek() != '\n' && buffer.peek() != '\\' && buffer.peek() != (char)65535){
                break;
            }
        }
        buffer.synchPtrBasedOnForwardPtr();
        int[] save = buffer.save();

        //integer parsing
        while(dfaInteger.transition(dfaInteger, buffer.peek()) != false) {
            buffer.extend();
        }
        currLexeme = buffer.copyLexeme();
        if(!currLexeme.equals("")) {
            matchingLexemes.add(new Token("Integer", currLexeme));
        }

        //float parsing
        buffer.load(save);
        while(dfaFloat.transition(buffer.peek()) != false) {
            buffer.extend();
        }
        currLexeme = buffer.copyLexeme();
        if(!currLexeme.equals("")) {
            matchingLexemes.add(new Token("Float", currLexeme));
        }

        //id parsing
        buffer.load(save);
        while(dfaId.transition(dfaId, buffer.peek()) != false) {
            buffer.extend();
        }
        currLexeme = buffer.copyLexeme();
        if(!currLexeme.equals("")) {
            matchingLexemes.add(new Token("Id", currLexeme));
        }

        //string parsing
        buffer.load(save);
        while(dfaString.transition(dfaString, buffer.peek()) != false) {
            buffer.extend();
        }
        currLexeme = buffer.copyLexeme();
        if(currLexeme.length() >= 2 && currLexeme.charAt(0) == '\"' && currLexeme.charAt(currLexeme.length() - 1) == '\"') {
            matchingLexemes.add(new Token("String", currLexeme));
        }

        //operator parsing
        buffer.load(save);
        currLexeme = Character.toString(buffer.peek());
        if(Operators.operators.contains(currLexeme)){
            matchingLexemes.add(new Token("Operator", currLexeme));
            buffer.extend();
        }
        if(currLexeme.equals("=") && buffer.peek() == '=' ||
           currLexeme.equals(":") && buffer.peek() == ':' ||
           currLexeme.equals(">") && buffer.peek() == '=' ||
           currLexeme.equals("<") && buffer.peek() == '>' ||
           currLexeme.equals("<") && buffer.peek() == '='){
            buffer.extend();
            matchingLexemes.add(new Token("Operator", buffer.copyLexeme()));
        }

        //pick longest matching lexeme
        Token longestLexeme = new Token("", "");
        for(Token t : matchingLexemes) {
            if(t.getLength() > longestLexeme.getLength()) {
                longestLexeme = t;
            }
        }

        //advance buffer up to longest matching lexeme and reset
        buffer.load(save);
        for(int i = 0; i < longestLexeme.getLength(); i++) {
            buffer.extend();
        }
        buffer.synchPtrBasedOnForwardPtr();
        matchingLexemes.clear();
        dfaInteger.resetDFA(dfaInteger);
        dfaString.resetDFA(dfaString);
        dfaFloat.resetDFA();
        dfaId.resetDFA(dfaId);
        return longestLexeme.isEmpty()? null : longestLexeme;
    }
}
