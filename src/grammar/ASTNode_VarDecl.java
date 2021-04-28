package grammar;

import semantic.SymTabEntryVar;
import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_VarDecl extends AST{
    SymTabEntryVar symTabEntry;

    public ASTNode_VarDecl(Symbol s) {
        super(s);
        symTabEntry = new SymTabEntryVar();
    }

    public SymTabEntryVar getSymTabEntry() {
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
