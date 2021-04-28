package grammar;

import semantic.SymTabEntryClass;
import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_ClassDecl extends AST{

    SymTabEntryClass symTabEntry;

    public ASTNode_ClassDecl(Symbol s) {
       super(s);
       symTabEntry = new SymTabEntryClass();
    }

    @Override
    public SymTabEntryClass getSymTabEntry() {
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
