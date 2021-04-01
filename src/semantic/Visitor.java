package semantic;
import grammar.ASTNode_ClassDecl;
import grammar.ASTNode_FuncDef;
import grammar.ASTNode_Prog;

public interface Visitor {
    public void visit(ASTNode_Prog prog);
    public void visit(ASTNode_ClassDecl classDecl);
    public void visit(ASTNode_FuncDef funcDef);
}
