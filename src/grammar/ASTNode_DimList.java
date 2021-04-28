package grammar;

import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_DimList extends AST{
    public ASTNode_DimList(Symbol s) {
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
