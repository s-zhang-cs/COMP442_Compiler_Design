package grammar;

import semantic.SymTabEntryClass;
import semantic.SymTabEntryMain;
import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_FuncMain extends AST{
    SymTabEntryMain symTabEntry;

    ASTNode_FuncMain(Symbol s) {
        super(s);
        symTabEntry = new SymTabEntryMain();
    }

    public SymTabEntryMain getSymTabEntry() {
        return symTabEntry;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        for (AST child : this.getChildren() ) {
            child.accept(visitor);
        }
        visitor.visit(this);
    }
}