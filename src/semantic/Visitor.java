package semantic;
import grammar.*;

public interface Visitor {
    //default behaviour
    public void preVisit(AST ast);
    public void visit(AST ast);

    //node-specific behaviour
    public void preVisit(ASTNode_Prog prog);
    public void preVisit(ASTNode_ClassDecl classDecl);
    public void preVisit(ASTNode_FuncDef funcDef);
    public void preVisit(ASTNode_VarDecl varDecl);
    public void preVisit(ASTNode_FParam fParam);
    public void preVisit(ASTNode_FuncMain funcMain);
    public void preVisit(ASTNode_StatBlock statBlock);
    public void preVisit(ASTNode_MultOp multOp);
    public void preVisit(ASTNode_AddOp addOp);
    public void preVisit(ASTNode_Int integer);
    public void preVisit(ASTNode_Float floatNum);
    public void preVisit(ASTNode_FuncDecl funcDecl);

    public void visit(ASTNode_Prog prog) throws Exception;
    public void visit(ASTNode_ClassDecl classDecl) throws Exception;
    public void visit(ASTNode_FuncDef funcDef) throws Exception;
    public void visit(ASTNode_VarDecl varDecl);
    public void visit(ASTNode_FParam fParam);
    public void visit(ASTNode_FuncMain funcMain);
    public void visit(ASTNode_StatBlock statBlock);
    public void visit(ASTNode_MultOp multOp);
    public void visit(ASTNode_AddOp addOp);
    public void visit(ASTNode_Int integer);
    public void visit(ASTNode_Float floatNum);
    public void visit(ASTNode_FuncDecl funcDecl);

}
