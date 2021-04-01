package grammar;

import semantic.Visitor;
import symbol.Symbol;

import java.util.ArrayList;
import java.util.List;

public class ASTNode_Prog extends AST{
    ASTNode_ClassList classList;

    public ASTNode_Prog(Symbol s) {
        super(s);
    }

    public void accept(Visitor visitor) {
        for (AST child : this.getChildren() ) {
            child.accept(visitor);
        }
        visitor.visit(this);
    }

}
