package semantic;

import grammar.AST;
import grammar.Grammar;
import grammar.Parser;

import java.io.BufferedReader;
import java.io.FileReader;

public class SymTabGeneration {
    AST source;
    SymbolTable symTab;


    public SymTabGeneration(String filePath) throws Exception{
        source = new Parser(filePath).getAST();
        symTab = new SymbolTable("prog");
    }

    public SymbolTable generate() {
        source.accept(new VisitorSymTabCreation());
        return source.getSymTab();
    }

    public void showSymTab() {
        System.out.println(source.getSymTab().toString());
    }

}
