package semantic;

import grammar.*;

import java.io.FileWriter;
import java.util.List;


public class VisitorSemanticChecks implements Visitor{

    public void writeSemanticError(String error) throws Exception {
        FileWriter fw = new FileWriter("resources/semantic/semantic.outsemanticerrors", true);
        fw.write(error + "\n");
        fw.close();
    }

    @Override
    public void preVisit(AST ast) {

    }

    @Override
    public void preVisit(ASTNode_Prog prog) {

    }

    @Override
    public void preVisit(ASTNode_ClassDecl classDecl) {

    }

    @Override
    public void preVisit(ASTNode_FuncDef funcDef) {

    }

    @Override
    public void preVisit(ASTNode_VarDecl varDecl) {

    }

    @Override
    public void preVisit(ASTNode_FParam fParam) {

    }

    @Override
    public void preVisit(ASTNode_FuncMain funcMain) {

    }

    @Override
    public void preVisit(ASTNode_StatBlock statBlock) {

    }

    @Override
    public void preVisit(ASTNode_MultOp multOp) {

    }

    @Override
    public void preVisit(ASTNode_AddOp addOp) {

    }

    @Override
    public void preVisit(ASTNode_Int integer) {

    }

    @Override
    public void preVisit(ASTNode_Float floatNum) {

    }

    @Override
    public void preVisit(ASTNode_FuncDecl funcDecl) {

    }

    @Override
    public void visit(AST ast) {

    }

    @Override
    public void visit(ASTNode_Prog prog) {
        List<AST> nodeChildren = prog.getChildren();
        for(AST classDecl : nodeChildren.get(2).getChildren()) {
            ASTNode_ClassDecl curr = (ASTNode_ClassDecl) classDecl;
            //linking symtabentry link
            SymbolTable classDeclTable = curr.getSymTabMap().get(curr.getChildren().get(2).getNodeSymbol().lexeme.toUpperCase());
            if(classDeclTable != null) {
                curr.getSymTabEntry().setLink(classDeclTable);
            }
        }
        for(AST funcDef : nodeChildren.get(1).getChildren()) {
            ASTNode_FuncDef curr = (ASTNode_FuncDef) funcDef;
            //linking symtabentry link
            SymbolTable funcDefTable = curr.getSymTabMap().get(AST.getScopeName(funcDef));
            if(funcDefTable != null) {
                curr.getSymTabEntry().setLink(funcDefTable);
            }
        }
        ASTNode_FuncMain main = (ASTNode_FuncMain) nodeChildren.get(0);
        SymbolTable mainTable = main.getSymTabMap().get("GLOBAL_MAIN");
        if(mainTable != null) {
            main.getSymTabEntry().setLink(mainTable);
        }
    }

    @Override
    public void visit(ASTNode_ClassDecl classDecl) throws Exception {
        List<AST> nodeChildren = classDecl.getChildren();
        SymbolTable nodeTable = classDecl.getSymTabMap().get(classDecl.getChildren().get(2).getNodeSymbol().lexeme);

        for(AST membDecl : nodeChildren.get(0).getChildren()) {
            if(membDecl.getChildren().get(0) instanceof ASTNode_VarDecl) {
                ASTNode_VarDecl varDecl = (ASTNode_VarDecl) membDecl.getChildren().get(0);
                //linking symtabentry link
                //if a class table corresponding to var declaration type exists
                SymbolTable varDeclClassTable = varDecl.getSymTabMap().get(varDecl.getChildren().get(2).getNodeSymbol().lexeme.toUpperCase());
                if(varDeclClassTable != null) {
                    varDecl.getSymTabEntry().setLink(varDeclClassTable);
                }
            }
            else if(membDecl.getChildren().get(0) instanceof ASTNode_FuncDecl) {
                ASTNode_FuncDecl funcDecl = (ASTNode_FuncDecl) membDecl.getChildren().get(0);
                //linking symtabentry link
                String funcDeclName = null;
                if(funcDecl.getChildren().size() == 3) {
                    funcDeclName = funcDecl.getChildren().get(2).getNodeSymbol().lexeme;
                }
                else if(funcDecl.getChildren().size() == 2) {
                    funcDeclName = funcDecl.getChildren().get(1).getNodeSymbol().lexeme;
                }
                String funcDeclScope = AST.getScopeName(funcDecl);
                SymbolTable funcDeclTable = funcDecl.getSymTabMap().get(funcDeclScope.toUpperCase() + "_" + funcDeclName.toUpperCase());
                //if this function exists
                if(funcDeclTable != null) {
                    funcDecl.getSymTabEntry().setLink(funcDeclTable);
                }
                //check for 'no definition' semantic error
                else {
                    writeSemanticError("[SEMANTIC ERROR]no definition provided for function: " + funcDeclScope + "::" + funcDeclName + ".");
                }
            }

        }
    }

    @Override
    public void visit(ASTNode_FuncDef funcDef) throws Exception {
        List<AST> nodeChildren = funcDef.getChildren();
        //variables
        for(AST statOrVar : nodeChildren.get(0).getChildren()) {
            if(statOrVar instanceof ASTNode_VarDecl) {
                ASTNode_VarDecl varDecl = (ASTNode_VarDecl) statOrVar;
                //linking symtabentry link
                //if a class table corresponding to var declaration type exists
                SymbolTable varDeclClassTable = varDecl.getSymTabMap().get(varDecl.getChildren().get(2).getNodeSymbol().lexeme.toUpperCase());
                if(varDeclClassTable != null) {
                    varDecl.getSymTabEntry().setLink(varDeclClassTable);
                }
            }
        }

        //parameters
        for(AST parameter : nodeChildren.get(2).getChildren()) {
            if(parameter instanceof ASTNode_FParam) {
                ASTNode_FParam param = (ASTNode_FParam) parameter;
                //linking symtabentry link
                //if a class table corresponding to parameter type exists
                SymbolTable paramClassTable = param.getSymTabMap().get(param.getChildren().get(2).getNodeSymbol().lexeme.toUpperCase());
                if(paramClassTable != null) {
                    param.getSymTabEntry().setLink(paramClassTable);
                }
            }
        }

        //check for 'no declaration' semantic error

//        SymbolTable symTab = funcDef.getSymTabMap().get(AST.getScopeName(funcDef).toUpperCase());
//        //check for 'no declaration' semantic error
//        if(symTab == null) {
//            writeSemanticError("[SEMANTIC ERROR]no declaration provided for function: " + AST.getScopeName(funcDef) + ".");
//            return;
//        }
    }

    @Override
    public void visit(ASTNode_VarDecl varDecl) {

    }

    @Override
    public void visit(ASTNode_FParam fParam) {

    }

    @Override
    public void visit(ASTNode_FuncMain funcMain) {
        List<AST> nodeChildren = funcMain.getChildren();
        for (AST statOrVar : nodeChildren.get(0).getChildren()) {
            if(statOrVar instanceof ASTNode_VarDecl) {
                ASTNode_VarDecl varDecl = (ASTNode_VarDecl) statOrVar;
                //linking symtabentry link
                //if a class table corresponding to var declaration type exists
                SymbolTable varDeclClassTable = varDecl.getSymTabMap().get(varDecl.getChildren().get(2).getNodeSymbol().lexeme.toUpperCase());
                if(varDeclClassTable != null) {
                    varDecl.getSymTabEntry().setLink(varDeclClassTable);
                }
            }
        }
    }

    @Override
    public void visit(ASTNode_StatBlock statBlock) {

    }

    @Override
    public void visit(ASTNode_MultOp multOp) {

    }

    @Override
    public void visit(ASTNode_AddOp addOp) {

    }

    @Override
    public void visit(ASTNode_Int integer) {

    }

    @Override
    public void visit(ASTNode_Float floatNum) {

    }

    @Override
    public void visit(ASTNode_FuncDecl funcDecl) {

    }
}
