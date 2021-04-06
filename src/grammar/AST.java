package grammar;

import semantic.SymTabEntry;
import semantic.SymbolTable;
import semantic.Visitor;
import symbol.Symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AST {
    AST parent;
    AST leftMostSibling;
    AST leftMostChild;
    AST rightSibling;
    Symbol nodeSymbol;
    SymbolTable symTab;
    //only used by root node
    Map<String, SymbolTable> symTabs;


    public AST() {
        leftMostSibling = this;
    }

    public AST(Symbol s) {
        leftMostSibling = this;
        nodeSymbol = s;
    }

    public AST getParent() {
        return parent;
    };

    public SymbolTable getSymTab(){
        return symTab;
    }

    //to be overwritten by children classes (class, func, var)
    public SymTabEntry getSymTabEntry() {
        return null;
    }

    public void setSymTab(SymbolTable s) {
        symTab = s;
    }

    public Map<String, SymbolTable> getSymTabs() {
        return symTabs;
    }

    public void setSymTabs(SymbolTable s) {
        symTabs.put(s.getName(), s);
    }

//    public void recordSymTabs() {
//        for(SymbolTable s : symTabs.values()) {
//            System.out.println(s.toString());
//        }
//    }

    public void accept(Visitor visitor) throws Exception {
        for (AST child : this.getChildren() ) {
            child.accept(visitor);
        }
    }

    public AST getRoot() {
        AST root = this;
        while(root.parent != null) {
            root = root.parent;
        }
        return root;
    }

    public Symbol getNodeSymbol() {
        return nodeSymbol;
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

//    public AST makeFamily(Symbol op, List<AST> l) {
//        AST child = null;
//        for(int i = 0; i < l.size() - 1; i++) {
//            child = l.get(i).makeSiblings(l.get(i+1));
//        }
//        AST node = makeNode(op);
//        node.adoptChildren(child);
//        return node;
//    }

    public List<AST> getChildren() {
        List<AST> children = new ArrayList<>();
        AST curr = leftMostChild;
        while(curr != null) {
            children.add(curr);
            curr = curr.rightSibling;
        }
        return children;
    }

    public void makeNode(Symbol s) {
        nodeSymbol = s;
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
