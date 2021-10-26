package grammar;

import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_Dot extends AST {
    public ASTNode_Dot(Symbol s) {
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
