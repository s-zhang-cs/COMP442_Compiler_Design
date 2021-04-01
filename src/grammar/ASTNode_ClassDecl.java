package grammar;

import semantic.SymTabEntry;
import semantic.SymTabEntryClass;
import semantic.Visitor;
import symbol.Symbol;

import java.util.ArrayList;
import java.util.List;

public class ASTNode_ClassDecl extends AST{

    SymTabEntryClass symTabEntry;

    public ASTNode_ClassDecl(Symbol s) {
       super(s);
       symTabEntry = new SymTabEntryClass(SymTabEntry.Kind.CLASS);
    }

    public SymTabEntryClass getSymTabEntry() {
        return symTabEntry;
    }

    public void accept(Visitor visitor) {
        for (AST child : this.getChildren() ) {
            child.accept(visitor);
        }
        visitor.visit(this);
    }
}
