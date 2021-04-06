package semantic;

import grammar.*;

import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class VisitorSemanticChecks implements Visitor{

    public VisitorSemanticChecks() {

    }

    public void writeSemanticError(String error) throws Exception {
        FileWriter fw = new FileWriter("resources/semantic/semantic.outsemanticerrors", true);
        fw.write(error + "\n");
        fw.close();
    }

    @Override
    public void visit(ASTNode_Prog prog) {
    }

    @Override
    public void visit(ASTNode_ClassDecl node) throws Exception {
        List<AST> nodeChildren = node.getChildren();
        AST membList = node.getChildren().get(0);
        for(AST membDecl : membList.getChildren()) {
            //check for 'no definition' semantic error
            if(membDecl.getChildren().get(0) instanceof ASTNode_FuncDef) {
                ASTNode_FuncDef funcDef = (ASTNode_FuncDef) membDecl.getChildren().get(0);
                String funcDefScope = node.getChildren().get(2).getNodeSymbol().lexeme;
                String funcDefName = funcDef.getChildren().get(2).getNodeSymbol().lexeme;
                SymbolTable classDecl = node.getRoot().getSymTabs().get(funcDefScope);
                boolean undefined = true;
                for(SymTabEntry i : classDecl.entries) {
                    if(i.kind == SymTabEntry.Kind.FUNCTION && i.name.equals(funcDefName)) {
                        undefined = false;
                    }
                }
                if(undefined) {
                    writeSemanticError("[SEMANTIC ERROR]no definition provided for function: " + funcDefScope + "::" + funcDefName + ".");
                }
            }
        }
    }

    @Override
    public void visit(ASTNode_FuncDef node) throws Exception {
        List<AST> nodeChildren = node.getChildren();
        String scope = node.getScope();
        //global scope
        if(scope.equals("global")) {
            //redefinition of functions in class scope, add variables in this pass
            if(node.getChildren().size() != 4) {
                String funcName = node.getChildren().get(3).getNodeSymbol().lexeme;
                String funcScope = node.getChildren().get(4).getNodeSymbol().lexeme;
                SymbolTable symTab = node.getRoot().getSymTabs().get(funcScope + "_" + funcName);
                //check for 'no declaration' semantic error
                if(symTab == null) {
                    writeSemanticError("[SEMANTIC ERROR]no declaration provided for function: " + funcScope + "::" + funcName + ".");
                    return;
                }

                //add variables
                for(AST stat : nodeChildren.get(0).getChildren()) {
                    if(stat instanceof ASTNode_VarDecl) {
                        symTab.addEntry(stat.getSymTabEntry());
                    }
                }
            }
        }
    }

    @Override
    public void visit(ASTNode_VarDecl varDecl) {
    }

    @Override
    public void visit(ASTNode_FParam fParam) {
    }

    @Override
    public void visit(ASTNode_FuncMain funcMain) {

    }
}
