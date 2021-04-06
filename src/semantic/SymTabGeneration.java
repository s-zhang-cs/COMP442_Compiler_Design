package semantic;

import grammar.AST;
import grammar.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SymTabGeneration {
    AST source;

    public SymTabGeneration(String filePath) throws Exception{
        source = new Parser(filePath).getAST();
    }

    public SymbolTable generate() throws Exception {
        source.accept(new VisitorSymTabCreation());
        source.accept(new VisitorSemanticChecks());
        return source.getSymTab();
    }

    public void recordSymTabs() throws IOException {
        FileWriter fw = new FileWriter(new File("resources/semantic/semantic.outSymbolTables"));
        for(SymbolTable s : source.getSymTabs().values()) {
            fw.write(s.toString() + "\n");
        }
        fw.close();
    }
}
