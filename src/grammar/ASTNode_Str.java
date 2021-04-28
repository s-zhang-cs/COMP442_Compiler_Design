package grammar;

import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_Str extends AST {
    public ASTNode_Str(Symbol s) {
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
