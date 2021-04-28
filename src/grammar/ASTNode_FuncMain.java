package grammar;

import semantic.SymTabEntryFunc;
import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_FuncMain extends AST{

    SymTabEntryFunc symTabEntry;

    ASTNode_FuncMain(Symbol s) {
        super(s);
        symTabEntry = new SymTabEntryFunc();
    }

    public SymTabEntryFunc getSymTabEntry() {
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

