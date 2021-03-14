package grammar;

import java.util.List;

public class AST {
    AST parent;
    AST leftMostSibling;
    AST leftMostChild;
    AST rightSibling;
    Symbol nodeSymbol;

//    public AST() {
//        node = new Symbol("UNDEFINED", true);
//    }

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

    public AST makeFamily(Symbol op, List<AST> l) {
        AST child = null;
        for(int i = 0; i < l.size() - 1; i++) {
            child = l.get(i).makeSiblings(l.get(i+1));
        }
        AST node = makeNode(op);
        node.adoptChildren(child);
        return node;
    }

    public AST makeNode(Symbol s) {
        AST node = new AST();
        node.nodeSymbol = new Symbol(s.symbol, s.isTerminal);
        return node;
    }

    public Symbol getNodeSymbol() {
        return nodeSymbol;
    }
}
