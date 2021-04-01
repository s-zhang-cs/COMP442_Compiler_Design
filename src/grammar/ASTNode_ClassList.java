package grammar;

import semantic.SymTabEntryClass;
import semantic.Visitor;
import symbol.Symbol;

import java.util.List;

public class ASTNode_ClassList extends AST{

    SymTabEntryClass symTab;

    public ASTNode_ClassList(Symbol s) {
        super(s);
    }

    public void accept(Visitor visitor) {
        for(AST child: this.getChildren()) {
            child.accept(visitor);
        }
    }
}
