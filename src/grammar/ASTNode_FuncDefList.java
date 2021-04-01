package grammar;

import semantic.Visitor;
import symbol.Symbol;

public class ASTNode_FuncDefList extends AST{
    ASTNode_FuncDefList(Symbol s) {
        super(s);
    }

    public void accept(Visitor visitor) {
        for(AST child: this.getChildren()) {
            child.accept(visitor);
        }
    }
}
