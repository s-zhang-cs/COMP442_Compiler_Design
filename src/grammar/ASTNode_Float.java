package grammar;

import semantic.SymTabEntryLitval;
import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_Float extends AST {
    SymTabEntryLitval symTabEntry;

    public ASTNode_Float(Symbol s) {
        super(s);
        symTabEntry = new SymTabEntryLitval();
    }

    public SymTabEntryLitval getSymTabEntry() {
        return symTabEntry;
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
