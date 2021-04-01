package grammar;

import semantic.SymbolTable;
import semantic.Visitor;
import symbol.Symbol;

import java.util.ArrayList;
import java.util.List;

public class AST {
    AST parent;
    AST leftMostSibling;
    AST leftMostChild;
    AST rightSibling;
    Symbol nodeSymbol;
    SymbolTable symTab;

    public AST() {
        leftMostSibling = this;
    }

    public AST(Symbol s) {
        leftMostSibling = this;
        nodeSymbol = s;
    }

    public SymbolTable getSymTab(){
        return symTab;
    }

    public void setSymTab(SymbolTable s) {
        symTab = s;
    }

    public void accept(Visitor visitor) {

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
