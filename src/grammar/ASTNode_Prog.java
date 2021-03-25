package grammar;

import symbol.Symbol;

public class ASTNode_Prog extends AST{
    ASTNode_ClassList classList;

    public ASTNode_Prog(Symbol s) {
        super(s);
    }
}
