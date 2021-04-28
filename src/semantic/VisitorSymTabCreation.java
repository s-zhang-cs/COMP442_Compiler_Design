package semantic;

import grammar.*;

import java.io.FileWriter;
import java.util.*;

public class VisitorSymTabCreation implements Visitor{

    int tmpVarCount = 1;

    public VisitorSymTabCreation() {
    }

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

    //SYMTAB DONE
    @Override
    public void visit(ASTNode_Prog prog) throws Exception {
        prog.setSymTab(new SymbolTable("Global"));

        //all class declarations within class declaration list
        for(AST classDecl : prog.getChildren().get(2).getChildren()) {
            ASTNode_ClassDecl curr = (ASTNode_ClassDecl)classDecl;
            if(prog.getSymTab().containsEntry(curr.getSymTabEntry())) {
                writeSemanticError("[SEMANTIC ERROR]duplicate entry for: " + curr.getSymTabEntry().toString() + " (Global level)");
                continue;
            }
            prog.getSymTab().addEntry(curr.getSymTabEntry());
        }

        //all function definitions within function definition list
        for(AST funcDef : prog.getChildren().get(1).getChildren()) {
            ASTNode_FuncDef curr = (ASTNode_FuncDef)funcDef;
            //need this check to avoid adding member functions
            if(curr.getSymTabEntry().name.contains("GLOBAL")) {
                if(prog.getSymTab().containsEntry(curr.getSymTabEntry())) {
                    writeSemanticError("[SEMANTIC ERROR]duplicate entry for: " + curr.getSymTabEntry().toString() + " (Global level)");
                    continue;
                }
                prog.getSymTab().addEntry(curr.getSymTabEntry());
            }
        }

        //main function
        prog.getSymTab().addEntry(prog.getChildren().get(0).getSymTabEntry());

        //register to symbol table map
        prog.addToSymTabMap(prog.getSymTab());
    }

    //SYMTAB DONE
    @Override
    public void visit(ASTNode_ClassDecl classDecl) throws Exception {
        List<AST> nodeChildren = classDecl.getChildren();

        //MAKE SYMBOL TABLE
        //create symbol table
        classDecl.setSymTab(new SymbolTable(classDecl.getChildren().get(2).getNodeSymbol().lexeme));
        //fill symbol table
        AST membList = nodeChildren.get(0);
        for(AST membDecl : membList.getChildren()) {
            if(membDecl.getChildren().get(0) instanceof ASTNode_FuncDecl) {
                ASTNode_FuncDecl curr = (ASTNode_FuncDecl) membDecl.getChildren().get(0);
                if(classDecl.getSymTab().containsEntry(curr.getSymTabEntry())) {
                    writeSemanticError("[SEMANTIC ERROR]duplicate entry for: " + curr.getSymTabEntry().toString() + " (Class " + classDecl.getChildren().get(2).getNodeSymbol().lexeme + ")");
                    continue;
                }
                classDecl.getSymTab().addEntry(curr.getSymTabEntry());
            }
            else if(membDecl.getChildren().get(0) instanceof ASTNode_VarDecl) {
                ASTNode_VarDecl curr = (ASTNode_VarDecl) membDecl.getChildren().get(0);
                if(classDecl.getSymTab().containsEntry(curr.getSymTabEntry())) {
                    writeSemanticError("[SEMANTIC ERROR]duplicate entry for: " + curr.getSymTabEntry().toString() + " (Class " + classDecl.getChildren().get(2).getNodeSymbol().lexeme + ")");
                    continue;
                }
                classDecl.getSymTab().addEntry(curr.getSymTabEntry());
            }
        }

        //MAKE SYMBOL TABLE ENTRY
        classDecl.getSymTabEntry().setName(nodeChildren.get(2).getNodeSymbol().lexeme);
        classDecl.getSymTabEntry().setScope(AST.getScopeName(classDecl));
        for(AST inherList : nodeChildren.get(1).getChildren()) {
            String name = inherList.getNodeSymbol().lexeme;
            classDecl.getSymTabEntry().getInherList().add(name);
        }

        //register to symbol table map
        classDecl.addToSymTabMap(classDecl.getSymTab());
    }

    //SYMTAB DONE
    @Override
    public void visit(ASTNode_FuncDef funcDef) throws Exception {
        List<AST> nodeChildren = funcDef.getChildren();

        //MAKE SYMBOL TABLE
        //create symbol table
        //class scope function with parameters
        if(nodeChildren.size() == 5) {
            funcDef.setSymTab(new SymbolTable(funcDef.getChildren().get(4).getNodeSymbol().lexeme + "_" + funcDef.getChildren().get(3).getNodeSymbol().lexeme));
        }
        //class scope function without parameters
        else if(nodeChildren.size() == 4 && !nodeChildren.get(2).getNodeSymbol().symbol.equals("FParamList")) {
            funcDef.setSymTab(new SymbolTable(funcDef.getChildren().get(3).getNodeSymbol().lexeme + "_" + funcDef.getChildren().get(2).getNodeSymbol().lexeme));
        }
        //global scope function with parameters
        else if(nodeChildren.size() == 4) {
            funcDef.setSymTab(new SymbolTable("GLOBAL_" + funcDef.getChildren().get(3).getNodeSymbol().lexeme));
        }
        //global scope function without parameters
        else if(nodeChildren.size() == 3) {
            funcDef.setSymTab(new SymbolTable("GLOBAL_" + funcDef.getChildren().get(2).getNodeSymbol().lexeme));
        }

        //FILL SYMBOL TABLE
        //parameters
        for(AST parameter : nodeChildren.get(2).getChildren()) {
            if(funcDef.getSymTab().containsEntry(parameter.getSymTabEntry())) {
                writeSemanticError("[SEMANTIC ERROR]duplicate entry for: " + parameter.getSymTabEntry().toString() + " (Function " + AST.getScopeName(funcDef) + ")");
                continue;
            }
            funcDef.getSymTab().addEntry(parameter.getSymTabEntry());
        }
        //variables
        for(AST statOrVar : nodeChildren.get(0).getChildren()) {
            if(statOrVar instanceof ASTNode_VarDecl) {
                if(funcDef.getSymTab().containsEntry(statOrVar.getSymTabEntry())) {
                    writeSemanticError("[SEMANTIC ERROR]duplicate entry for: " + statOrVar.getSymTabEntry().toString() + " (Function " + AST.getScopeName(funcDef) + ")");
                    continue;
                }
                funcDef.getSymTab().addEntry(statOrVar.getSymTabEntry());
            }
        }


        //MAKE SYMBOL TABLE ENTRY
        String symTabEntryName = AST.getScopeName(funcDef);
        funcDef.getSymTabEntry().setName(symTabEntryName);
        //global scope
        if(symTabEntryName.contains("GLOBAL")) {
            funcDef.getSymTabEntry().setScope("GLOBAL");
        }
        //class scope
        else {
            funcDef.getSymTabEntry().setScope(symTabEntryName);
        }
        funcDef.getSymTabEntry().setReturnType(nodeChildren.get(1).getNodeSymbol().lexeme);

        //register to symbol table map
        funcDef.getRoot().addToSymTabMap(funcDef.getSymTab());
    }

    //SYMTAB DONE
    @Override
    public void visit(ASTNode_VarDecl varDecl) {
        List<AST> nodeChildren = varDecl.getChildren();

        //MAKE SYMBOL TABLE ENTRY
        //name
        varDecl.getSymTabEntry().setName(varDecl.getChildren().get(1).getNodeSymbol().lexeme);
        //type
        String type = nodeChildren.get(2).getNodeSymbol().lexeme;
        List<AST> dimList = nodeChildren.get(0).getChildren();
        List<Integer> dims = new ArrayList<>();
        if(dimList != null) {
            for(AST i : dimList) {
                type += "[" + i.getNodeSymbol().lexeme + "]";
                dims.add(Integer.valueOf(i.getNodeSymbol().lexeme));
            }
        }
        varDecl.getSymTabEntry().setType(type);
        //scope
        varDecl.getSymTabEntry().setScope(AST.getScopeName(varDecl));

        //MEMORY ALLOCATION
        if(nodeChildren.get(2).getNodeSymbol().lexeme.equals("integer") || nodeChildren.get(2).getNodeSymbol().lexeme.equals("int")) {
            int memSize = 4;
            for(Integer i : dims) {
                memSize *= i;
            }
            varDecl.getSymTabEntry().setMemSize(memSize); //int is 4 bytes
        }
        else if(nodeChildren.get(2).getNodeSymbol().lexeme.equals("float")) {
            int memSize = 8;
            for(Integer i : dims) {
                memSize *= i;
            }
            varDecl.getSymTabEntry().setMemSize(memSize); //float is 8 bytes
        }
    }

    //SYMTAB DONE
    @Override
    public void visit(ASTNode_FParam fParam) {
        List<AST> nodeChildren = fParam.getChildren();

        //MAKE SYMBOL TABLE ENTRY
        //name
        fParam.getSymTabEntry().setName(nodeChildren.get(1).getNodeSymbol().lexeme);
        //type
        String type = fParam.getChildren().get(2).getNodeSymbol().lexeme;
        if(fParam.getChildren().get(0).getChildren() != null) {
            for(AST i : fParam.getChildren().get(0).getChildren()) {
                type += "[" + i.getNodeSymbol().lexeme + "]";
            }
            fParam.getSymTabEntry().setType(type);
        }
        //scope
        fParam.getSymTabEntry().setScope(AST.getScopeName(fParam));
    }

    //
    @Override
    public void visit(ASTNode_FuncMain node) {
        List<AST> nodeChildren = node.getChildren();

        //MAKE SYMBOL TABLE ENTRY
        node.setSymTab(new SymbolTable("GLOBAL_MAIN"));
        node.getSymTabEntry().setName("GLOBAL_MAIN");
        node.getSymTabEntry().setScope("GLOBAL");

        for(AST statOrVar : nodeChildren.get(0).getChildren()) {
            if(statOrVar instanceof ASTNode_VarDecl) {
                node.getSymTab().addEntry(statOrVar.getSymTabEntry());
                //MEMORY ALLOCATION FOR VARS
                statOrVar.getSymTabEntry().memOffset = AST.getScopeTree(node).getSymTabEntry().memSize - statOrVar.getSymTabEntry().memSize;
                AST.getScopeTree(node).getSymTabEntry().memSize -= statOrVar.getSymTabEntry().memSize;
            }
            else if(statOrVar instanceof ASTNode_Stat) {
                //MEMORY ALLOCATION FOR BASIC OPERATIONS ( + - * / )
                fetchLitAndTmpVals(statOrVar, node);
            }
        }

        //register to symbol table map
        node.getRoot().addToSymTabMap(node.getSymTab());
    }

    private void fetchLitAndTmpVals(AST node, AST scope) {
        for(AST i : node.getChildren()) {
            //dfs traversal of tree
            fetchLitAndTmpVals(i, scope);
            //for literal vals
            if(i instanceof ASTNode_Int) {
                i.getSymTabEntry().memOffset = scope.getSymTabEntry().memSize - 4;
                scope.getSymTabEntry().memSize -= 4;
                scope.getSymTab().addEntry(i.getSymTabEntry());
            }
            else if(i instanceof  ASTNode_Float) {
                i.getSymTabEntry().memOffset = scope.getSymTabEntry().memSize - 8;
                scope.getSymTabEntry().memSize -= 8;
                scope.getSymTab().addEntry(i.getSymTabEntry());
            }
            //for temporary vars
            else if(i instanceof  ASTNode_AddOp || i instanceof  ASTNode_MultOp) {
                AST leftOp = i.getChildren().get(0);
                AST rightOp = i.getChildren().get(1);
                int reserveBytes;
                if(leftOp instanceof ASTNode_Int && rightOp instanceof ASTNode_Int) {
                    reserveBytes = 4;
                }
                else {
                    reserveBytes = 8;
                }
                i.getSymTabEntry().memOffset = scope.getSymTabEntry().memSize - reserveBytes;
                scope.getSymTabEntry().memSize -= reserveBytes;
                scope.getSymTab().addEntry(i.getSymTabEntry());
            }
        }
    }

    @Override
    public void visit(ASTNode_StatBlock node) {
        List<AST> nodeChildren = node.getChildren();
        //memAllocation
        for (AST statOrVar : nodeChildren) {
            if(statOrVar instanceof ASTNode_VarDecl) {
                statOrVar.getSymTabEntry().memOffset = AST.getScopeTree(node).getSymTabEntry().memSize - statOrVar.getSymTabEntry().memSize;
                AST.getScopeTree(node).getSymTabEntry().memSize -= statOrVar.getSymTabEntry().memSize;
            }
        }
    }

    @Override
    public void visit(ASTNode_MultOp multOp) {
        multOp.getSymTabEntry().setName("t" + tmpVarCount++);
        multOp.getSymTabEntry().setKind(SymTabEntry.Kind.TEMPVAR);
        multOp.getSymTabEntry().setMemSize(4);
    }

    @Override
    public void visit(ASTNode_AddOp addOp) {
        addOp.getSymTabEntry().setName("t" + tmpVarCount++);
        addOp.getSymTabEntry().setKind(SymTabEntry.Kind.TEMPVAR);
        addOp.getSymTabEntry().setMemSize(4);
    }

    @Override
    public void visit(ASTNode_Int integer) {
        integer.getSymTabEntry().setValue(integer.getNodeSymbol().lexeme);
        integer.getSymTabEntry().setScope(AST.getScopeName(integer));
        integer.getSymTabEntry().setName("t" + tmpVarCount++);
        integer.getSymTabEntry().setMemSize(4);
    }

    @Override
    public void visit(ASTNode_Float floatNum) {
        floatNum.getSymTabEntry().setValue(floatNum.getNodeSymbol().lexeme);
        floatNum.getSymTabEntry().setScope(AST.getScopeName(floatNum));
        floatNum.getSymTabEntry().setName("t" + tmpVarCount++);
        floatNum.getSymTabEntry().setMemSize(8);
    }

    @Override
    public void visit(ASTNode_FuncDecl funcDecl) {
        List<AST> nodeChildren = funcDecl.getChildren();
        SymTabEntryFunc entry = funcDecl.getSymTabEntry();
        if(nodeChildren.size() == 3) {
            entry.setName(nodeChildren.get(2).getNodeSymbol().lexeme);
        }
        else if(nodeChildren.size() == 2) {
            entry.setName(nodeChildren.get(1).getNodeSymbol().lexeme);
        }
        entry.setScope(AST.getScopeName(funcDecl));
        entry.setKind(SymTabEntry.Kind.FUNCTION);
        entry.setReturnType(nodeChildren.get(0).getNodeSymbol().lexeme);
    }
}
