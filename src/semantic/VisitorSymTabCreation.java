package semantic;

import grammar.AST;
import grammar.ASTNode_ClassDecl;
import grammar.ASTNode_FuncDef;
import grammar.ASTNode_Prog;

public class VisitorSymTabCreation implements Visitor{
    @Override
    public void visit(ASTNode_Prog node) {
        node.setSymTab(new SymbolTable("Global"));
        //all class declarations within class declaration list
        for(AST classelt : node.getChildren().get(2).getChildren()) {
            ASTNode_ClassDecl curr = (ASTNode_ClassDecl)classelt;
            node.getSymTab().addEntry(curr.getSymTabEntry());
        }
        //all function definitions within function definition list
        for(AST fndefelt : node.getChildren().get(1).getChildren()) {
            ASTNode_FuncDef curr = (ASTNode_FuncDef)fndefelt;
            node.getSymTab().addEntry(curr.getSymTabEntry());
        }
        //node.getSymTab().addEntry();
    }

    @Override
    public void visit(ASTNode_ClassDecl node) {
        node.setSymTab(new SymbolTable(node.getNodeSymbol().lexeme));
        node.getSymTabEntry().setName(node.getChildren().get(2).getNodeSymbol().lexeme);
    }

    @Override
    public void visit(ASTNode_FuncDef node) {
        node.setSymTab(new SymbolTable(node.getNodeSymbol().lexeme));
        node.getSymTabEntry().setName(node.getChildren().get(3).getNodeSymbol().lexeme);
    }
}
