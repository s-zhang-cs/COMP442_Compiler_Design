package semantic;

import grammar.AST;
import grammar.ASTNode_ClassDecl;
import grammar.ASTNode_FuncDef;
import grammar.ASTNode_VarDecl;

public class SymTabEntry {

    public enum Kind {
        VARIABLE,
        CLASS,
        FUNCTION,
        PARAMETER
    }

    String name;
    Kind kind;


    public SymTabEntry(Kind kind) {
        this.kind = kind;
//        if(node instanceof ASTNode_ClassDecl) {
//            kind = kind.CLASS;
//        }
//        else if(node instanceof ASTNode_FuncDef) {
//            kind = kind.FUNCTION;
//        }
//        else if(node instanceof ASTNode_VarDecl) {
//            kind = kind.VARIABLE;
//        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name + " | " +  kind;
    }

}
