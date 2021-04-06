package grammar;

import semantic.SymbolTable;
import semantic.Visitor;
import symbol.Symbol;

import java.util.HashMap;
import java.util.Map;

public class ASTNode_Prog extends AST{

    public ASTNode_Prog(Symbol s) {
        super(s);
        symTabs = new HashMap<>();
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        for (AST child : this.getChildren() ) {
            child.accept(visitor);
        }
        visitor.visit(this);
    }

}
