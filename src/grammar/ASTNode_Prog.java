package grammar;

import semantic.Visitor;
import symbol.Symbol;

import java.util.HashMap;

public class ASTNode_Prog extends AST{

    public ASTNode_Prog(Symbol s) {
        super(s);
        //root node (only node to use symTabMap)
        symTabMap = new HashMap<>();
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
