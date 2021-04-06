package grammar;

import semantic.SymTabEntry;
import semantic.SymTabEntryFunc;
import semantic.Visitor;
import symbol.Symbol;

import java.util.List;

public class ASTNode_FuncDef extends AST {
    SymTabEntryFunc symTabEntry;

    public ASTNode_FuncDef(Symbol s) {
        super(s);
        symTabEntry = new SymTabEntryFunc(SymTabEntry.Kind.FUNCTION);
    }

    public SymTabEntryFunc getSymTabEntry() {
        return symTabEntry;
    }

    public String getScope() {
        //global scope
        if(parent instanceof ASTNode_FuncDefList) {
            return "global";
        }
        //class scope
        else if(parent instanceof ASTNode_MembDecl) {
            return parent.parent.parent.getChildren().get(2).getNodeSymbol().lexeme;
        }
        return null;
    }

    public AST getFuncDef(String funcName, String funcScope) {
        if(!getScope().equals("global")) {
            return null;
        }
        List<AST> classList = getRoot().getChildren().get(2).getChildren();
        for(AST classDecl : classList) {
            String className = classDecl.getChildren().get(2).getNodeSymbol().lexeme;
            if(className.equals(funcScope)) {
                for(AST membDecl : classDecl.getChildren().get(0).getChildren()) {
                    if(membDecl.getChildren().get(0).getChildren().get(2).getNodeSymbol().lexeme.equals(funcName)) {
                        return membDecl.getChildren().get(0);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        for (AST child : this.getChildren() ) {
            child.accept(visitor);
        }
        visitor.visit(this);
    }
}
