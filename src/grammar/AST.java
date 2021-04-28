package grammar;

import semantic.SymTabEntry;
import semantic.SymbolTable;
import semantic.Visitor;
import symbol.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AST {
    AST parent;
    AST leftMostSibling;
    AST leftMostChild;
    AST rightSibling;
    Symbol nodeSymbol;
    SymTabEntry symTabEntry;
    SymbolTable symTab;
    //only used by root node
    Map<String, SymbolTable> symTabMap;

    public AST() {
        leftMostSibling = this;
    }

    public AST(Symbol s) {
        leftMostSibling = this;
        symTabEntry = new SymTabEntry(SymTabEntry.Kind.UNINITIALIZED);
        nodeSymbol = s;
        symTab = new SymbolTable(s.lexeme);
    }

    public AST getParent() {
        return parent;
    };

    public AST getLeftMostSibling() {
        return leftMostSibling;
    }

    public AST getLeftMostChild() {
        return leftMostChild;
    }

    public AST getRightSibling() {
        return rightSibling;
    }

    public Symbol getNodeSymbol() {
        return nodeSymbol;
    }

    //to be overwritten by children classes (class, func, var)
    public SymTabEntry getSymTabEntry() {
        return symTabEntry;
    }

    public SymbolTable getSymTab(){
        return symTab;
    }

    public void setSymTab(SymbolTable s) {
        symTab = s;
    }

    public Map<String, SymbolTable> getSymTabMap() {
        return getRoot().symTabMap;
    }

    public void addToSymTabMap (SymbolTable s) {
        getRoot().getSymTabMap().put(s.getName(), s);
    }

    public AST getRoot() {
        AST root = this;
        while(root.parent != null) {
            root = root.parent;
        }
        return root;
    }

    public static String getScopeName(AST node) {
        AST curr = node;
        if(curr instanceof ASTNode_ClassDecl && curr.parent instanceof ASTNode_ClassList) {
            return "GLOBAL";
        }
        while(!(curr instanceof ASTNode_ClassDecl) && !(curr instanceof ASTNode_FuncDef) && !(curr instanceof ASTNode_FuncMain)) {
            curr = curr.parent;
        }
        if(curr instanceof ASTNode_ClassDecl) {
            return curr.getChildren().get(2).getNodeSymbol().lexeme;
        }
        if(curr instanceof ASTNode_FuncDef) {
            List<AST> nodeChildren = curr.getChildren();
            if(nodeChildren.size() == 5) {
                return curr.getChildren().get(4).getNodeSymbol().lexeme.toUpperCase() + "_" + curr.getChildren().get(3).getNodeSymbol().lexeme.toUpperCase();
            }
            //class scope function without parameters
            if(nodeChildren.size() == 4 && !nodeChildren.get(2).getNodeSymbol().symbol.equals("FParamList")) {
                return curr.getChildren().get(3).getNodeSymbol().lexeme.toUpperCase() + "_" + curr.getChildren().get(2).getNodeSymbol().lexeme.toUpperCase();
            }
            //global scope function with parameters
            else if(nodeChildren.size() == 4) {
                return "GLOBAL_" + curr.getChildren().get(3).getNodeSymbol().lexeme.toUpperCase();
            }
            //global scope function without parameters
            else if(nodeChildren.size() == 3) {
                return "GLOBAL_" + curr.getChildren().get(2).getNodeSymbol().lexeme.toUpperCase();
            }
        }
        if(curr instanceof ASTNode_FuncMain) {
            return "GLOBAL_MAIN";
        }
        return null;
    }

    public static AST getScopeTree(AST node) {
        AST curr = node;
        if(curr instanceof ASTNode_ClassDecl && curr.parent instanceof ASTNode_ClassList) {
            return curr.getParent().getParent();
        }
        while(!(curr instanceof ASTNode_ClassDecl) && !(curr instanceof ASTNode_FuncDef) && !(curr instanceof ASTNode_FuncMain)) {
            curr = curr.parent;
        }
        return curr;
    }

    //to be overwritten by child classes to allow dynamic dispatch
    public void accept(Visitor visitor) throws Exception {
        visitor.preVisit(this);
        for (AST child : this.getChildren() ) {
            child.accept(visitor);
        }
        visitor.visit(this);
    }

    public AST makeSiblings(AST y) {
        //go to rightmost sibling
        AST xSiblings = this;
        while(xSiblings.rightSibling != null) {
            xSiblings = xSiblings.rightSibling;
        }
        //join the lists
        AST ySiblings = y.leftMostSibling;
        xSiblings.rightSibling = ySiblings;
        //set pointers for the new siblings
        ySiblings.leftMostSibling = xSiblings.leftMostSibling;
        ySiblings.parent = xSiblings.parent;
        while(ySiblings.rightSibling != null) {
            ySiblings = ySiblings.rightSibling;
            ySiblings.leftMostSibling = xSiblings.leftMostSibling;
            ySiblings.parent = xSiblings.parent;
        }
        return ySiblings;
    }

    public AST adoptChildren(AST y) {
        if(leftMostChild != null) {
            leftMostChild.makeSiblings(y);
        }
        else {
            AST ySiblings = y.leftMostSibling;
            leftMostChild = ySiblings;
            while(ySiblings != null) {
                ySiblings.parent = this;
                ySiblings = ySiblings.rightSibling;
            }
        }
        return leftMostChild;
    }

    public List<AST> getChildren() {
        List<AST> children = new ArrayList<>();
        AST curr = leftMostChild;
        while(curr != null) {
            children.add(curr);
            curr = curr.rightSibling;
        }
        return children;
    }

    public String toString()
    {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(nodeSymbol);
        buffer.append('\n');
        AST children = leftMostChild;
        while(children != null) {
            if(children.rightSibling != null) {
                children.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            }
            else {
                children.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
            children = children.rightSibling;
        }
    }

}
