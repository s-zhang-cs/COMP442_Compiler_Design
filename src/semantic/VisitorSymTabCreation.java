package semantic;

import grammar.*;
import symbol.Symbol;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VisitorSymTabCreation implements Visitor{

    public VisitorSymTabCreation() {
    }

    public void writeSemanticError(String error) throws Exception {
        FileWriter fw = new FileWriter("resources/semantic/semantic.outsemanticerrors", true);
        fw.write(error + "\n");
        fw.close();
    }

    @Override
    public void visit(ASTNode_Prog node) throws Exception {
        node.setSymTab(new SymbolTable("Global"));
        //all class declarations within class declaration list
        for(AST classelt : node.getChildren().get(2).getChildren()) {
            ASTNode_ClassDecl curr = (ASTNode_ClassDecl)classelt;
            if(node.getSymTab().containsValue(curr.getSymTabEntry())) {
                writeSemanticError("[SEMANTIC ERROR]duplicate entry for: " + curr.getSymTabEntry().toString() + " (Global level)");
                continue;
            }
            node.getSymTab().addEntry(curr.getSymTabEntry());
        }
        //all function definitions within function definition list
        for(AST fndefelt : node.getChildren().get(1).getChildren()) {
            ASTNode_FuncDef curr = (ASTNode_FuncDef)fndefelt;
            //need this check to avoid adding member functions
            if(curr.getSymTabEntry().name != null) {
                if(node.getSymTab().containsValue(curr.getSymTabEntry())) {
                    writeSemanticError("[SEMANTIC ERROR]duplicate entry for: " + curr.getSymTabEntry().toString() + " (Global level)");
                    continue;
                }
                node.getSymTab().addEntry(curr.getSymTabEntry());
            }
        }
        //main
        node.getSymTab().addEntry(node.getChildren().get(0).getSymTabEntry());
        //register to symbol table map
        node.getRoot().setSymTabs(node.getSymTab());
    }

    @Override
    public void visit(ASTNode_ClassDecl node) throws Exception {
        //fill the symbol table of the current node
        node.setSymTab(new SymbolTable(node.getChildren().get(2).getNodeSymbol().lexeme));
        AST membList = node.getChildren().get(0);
        for(AST membDecl : membList.getChildren()) {
            if(membDecl.getChildren().get(0) instanceof ASTNode_FuncDef) {
                ASTNode_FuncDef curr = (ASTNode_FuncDef) membDecl.getChildren().get(0);
                if(node.getSymTab().containsValue(curr.getSymTabEntry())) {
                    writeSemanticError("[SEMANTIC ERROR]duplicate entry for: " + curr.getSymTabEntry().toString() + " (Class " + node.getChildren().get(2).getNodeSymbol().lexeme + ")");
                    continue;
                }
                node.getSymTab().addEntry(curr.getSymTabEntry());
            }
            else if(membDecl.getChildren().get(0) instanceof ASTNode_VarDecl) {
                ASTNode_VarDecl curr = (ASTNode_VarDecl) membDecl.getChildren().get(0);
                if(node.getSymTab().containsValue(curr.getSymTabEntry())) {
                    writeSemanticError("[SEMANTIC ERROR]duplicate entry for: " + curr.getSymTabEntry().toString() + " (Class " + node.getChildren().get(2).getNodeSymbol().lexeme + ")");
                    continue;
                }
                node.getSymTab().addEntry(curr.getSymTabEntry());
            }
        }

        //make the symbol table entry of the current node
        List<AST> nodeChildren = node.getChildren();
        node.getSymTabEntry().name = nodeChildren.get(2).getNodeSymbol().lexeme;
        for(AST i : nodeChildren.get(1).getChildren()) {
            String name = i.getNodeSymbol().lexeme;
            node.getSymTabEntry().inherList.add(name);
        }

        //register to symbol table map
        node.getRoot().setSymTabs(node.getSymTab());
    }

    @Override
    public void visit(ASTNode_FuncDef node) throws Exception {
        //fill the symbol table of the current node
        List<AST> nodeChildren = node.getChildren();
        String scope = node.getScope();
        String name = null;
        //global scope
        if(scope.equals("global")) {
            //redefinition of functions in class scope
            if(node.getChildren().size() != 4) {
                return;
            }
            name = node.getChildren().get(3).getNodeSymbol().lexeme;
            node.setSymTab(new SymbolTable("global_" + name));
        }
        //class scope
        else if(node.getParent() instanceof ASTNode_MembDecl){
            name = node.getChildren().get(2).getNodeSymbol().lexeme;
            node.setSymTab(new SymbolTable(node.getScope() + "_" + name));
        }
        //add parameters
        for(AST i : nodeChildren.get(1).getChildren()) {
            if(node.getSymTab().containsValue(i.getSymTabEntry())) {
                writeSemanticError("[SEMANTIC ERROR]duplicate entry for: " + i.getSymTabEntry().toString() + " (Function " + node.getChildren().get(2).getNodeSymbol().lexeme + ")");
                continue;
            }
            node.getSymTab().addEntry(i.getSymTabEntry());
        }

        //make the symbol table entry of the current node
        node.getSymTabEntry().name = name;
        node.getSymTabEntry().returnType = nodeChildren.get(0).getNodeSymbol().lexeme;

        //register to symbol table map
        node.getRoot().setSymTabs(node.getSymTab());
    }

    @Override
    public void visit(ASTNode_VarDecl node) {
        //make the symbol table entry of the current node (no need to propagate further down the tree)
        node.getSymTabEntry().name = node.getChildren().get(1).getNodeSymbol().lexeme;
        String type = node.getChildren().get(2).getNodeSymbol().lexeme;
        if(node.getChildren().get(0).getChildren() != null) {
            for(AST i : node.getChildren().get(0).getChildren()) {
                type += "[" + i.getNodeSymbol().lexeme + "]";
            }
            node.getSymTabEntry().type = type;
        }
    }

    @Override
    public void visit(ASTNode_FParam node) {
        //make the symbol table entry of the current node (no need to propagate further down the tree)
        node.getSymTabEntry().name = node.getChildren().get(1).getNodeSymbol().lexeme;
        String type = node.getChildren().get(2).getNodeSymbol().symbol;
        if(node.getChildren().get(0).getChildren() != null) {
            for(AST i : node.getChildren().get(0).getChildren()) {
                type += "[" + i.getNodeSymbol().lexeme + "]";
            }
            node.getSymTabEntry().type = type;
        }
    }

    @Override
    public void visit(ASTNode_FuncMain node) {
        //make the symbol table entry of the current node
        List<AST> nodeChildren = node.getChildren();
        node.getSymTabEntry().name = "Global_main";
        for(AST i : nodeChildren.get(0).getChildren()) {
            if(i instanceof ASTNode_VarDecl) {
                //node.getSymTab().addEntry(i.getSymTabEntry());
            }
        }

        //register to symbol table map
        node.setSymTab(new SymbolTable("Global_main"));
        node.getRoot().setSymTabs(node.getSymTab());
    }
}
