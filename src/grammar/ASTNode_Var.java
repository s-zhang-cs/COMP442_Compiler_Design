package grammar;

import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_Var extends AST {
    public ASTNode_Var(Symbol s) {
        super(s);
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.preVisit(this);
        for (AST child : this.getChildren() ) {
            child.accept(visitor);
        }
        visitor.visit(this);
    }
}
