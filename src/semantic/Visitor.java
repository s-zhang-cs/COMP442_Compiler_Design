package semantic;
import grammar.*;

public interface Visitor {
    public void visit(ASTNode_Prog prog) throws Exception;
    public void visit(ASTNode_ClassDecl classDecl) throws Exception;
    public void visit(ASTNode_FuncDef funcDef) throws Exception;
    public void visit(ASTNode_VarDecl varDecl);
    public void visit(ASTNode_FParam fParam);
    public void visit(ASTNode_FuncMain funcMain);
}
