package grammar;

import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_Factor extends AST{
    public ASTNode_Factor(Symbol s) {
        super(s);
    }

    public void accept(Visitor visitor) throws Exception {
        visitor.preVisit(this);
        for (AST child : this.getChildren() ) {
            child.accept(visitor);
        }
        visitor.visit(this);
    }
}
