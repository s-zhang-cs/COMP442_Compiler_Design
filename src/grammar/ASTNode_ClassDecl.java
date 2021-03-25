package grammar;

import symbol.Symbol;

public class ASTNode_ClassDecl extends AST{
    ASTNode_Id className;
    ASTNode_InherList inherList;
    ASTNode_MembList membList;

    public ASTNode_ClassDecl(Symbol s) {
       super(s);
    }
//
//    public void setId(ASTNode_Id id) {
//        className = id;
//    }

}
