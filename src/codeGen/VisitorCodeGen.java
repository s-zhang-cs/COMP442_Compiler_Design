package codeGen;

import grammar.*;
import semantic.Visitor;

import java.io.File;
import java.io.FileWriter;
import java.util.Stack;

public class VisitorCodeGen implements Visitor {

    Stack<String> regPool;
    String        moonExec;
    String        moonData;
    String 	      outputFileName;


    public VisitorCodeGen(String outputFileName) {
        // create a pool of registers as a stack of Strings
        // assuming only r1, ..., r12 are available
        regPool = new Stack<>();
        for (Integer i = 12; i>=1; i--) {
            regPool.push("r" + i.toString());
        }
        this.outputFileName = outputFileName;
        moonData = "";
        moonExec = "";
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
    public void visit(ASTNode_Prog prog) throws Exception {
        if (!this.outputFileName.isEmpty()) {
            FileWriter fw = new FileWriter(new File(outputFileName));
            fw.write(moonExec);
            fw.write(moonData);
            fw.close();
        }
    }

    @Override
    public void visit(ASTNode_ClassDecl classDecl) throws Exception {

    }

    @Override
    public void visit(ASTNode_FuncDef funcDef) throws Exception {

    }

    @Override
    public void visit(ASTNode_VarDecl varDecl) {

    }

    @Override
    public void visit(ASTNode_FParam fParam) {

    }

    @Override
    public void visit(ASTNode_FuncMain funcMain) {
//        // generate moon program's entry point
//        moonExec += "entry\n";
//        // make the stack frame pointer (address stored in r14) point
//        // to the top address allocated to the moon processor
//        moonExec += "addi r14,r0,topaddr\n";
//        // propagate acceptance of this visitor to all the children
//        for (Node child : p_node.getChildren())
//            child.accept(this);
//        // generate moon program's end point
//        m_moonDataCode += m_mooncodeindent + "% buffer space used for console output\n";
//        // buffer used by the lib.m subroutines
//        m_moonDataCode += String.format("%-10s" , "buf") + "res 20\n";
//        // halting point of the entire program
//        m_moonExecCode += m_mooncodeindent + "hlt\n";
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
        String r1 = this.regPool.pop();
        moonExec += "% processing: " + integer.getSymTabEntry()  + "\n";
        moonExec += "addi " + r1 + ",r0," + integer.getSymTabEntry().getValue() + "\n";
        moonExec += moonExec + "sw " + integer.getSymTabEntry().getMemOffset() + "(r14)," + r1 + "\n";
        this.regPool.push(r1);
    }

    @Override
    public void visit(ASTNode_Float floatNum) {

    }

    @Override
    public void visit(ASTNode_FuncDecl funcDecl) {

    }
}
