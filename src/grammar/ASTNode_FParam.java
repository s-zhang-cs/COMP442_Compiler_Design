package grammar;

import semantic.SymTabEntryParam;
import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_FParam extends AST{
    SymTabEntryParam symTabEntry;

    public ASTNode_FParam(Symbol s) {
        super(s);
        symTabEntry = new SymTabEntryParam();
    }

    public SymTabEntryParam getSymTabEntry() {
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
