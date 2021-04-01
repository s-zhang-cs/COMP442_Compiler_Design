package grammar;

import semantic.SymTabEntry;
import semantic.SymTabEntryClass;
import semantic.SymTabEntryFunc;
import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_FuncDef extends AST {
    SymTabEntryFunc symTabEntry;

    public ASTNode_FuncDef(Symbol s) {
        super(s);
        symTabEntry = new SymTabEntryFunc(SymTabEntry.Kind.FUNCTION);
    }

    public SymTabEntry getSymTabEntry() {
        return symTabEntry;
    }

    public void accept(Visitor visitor) {
        for (AST child : this.getChildren() ) {
            child.accept(visitor);
        }
        visitor.visit(this);
    }
}
