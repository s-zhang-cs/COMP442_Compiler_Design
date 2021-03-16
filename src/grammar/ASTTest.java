package grammar;

import symbol.Symbol;

public class ASTTest {
    public static void main(String[] args) {
        AST node1 = new AST(new Symbol("node1", true));
        AST node2 = new AST(new Symbol("node2", true));
        AST node3 = new AST(new Symbol("node3", true));
        AST node4 = new AST(new Symbol("node4", true));
        AST node5 = new AST(new Symbol("node5", true));
        AST node6 = new AST(new Symbol("node6", true));
        AST node7 = new AST(new Symbol("node7", true));

        System.out.println("~~~~~~~~~~~~~~~~~~~testing adoptChildren, makeSibling and print methods~~~~~~~~~~~~~~~~~~");
        node1.adoptChildren(node2);
        node2.makeSiblings(node3);
        node2.makeSiblings(node4);
        node4.adoptChildren(node5);
        node5.makeSiblings(node6);
        node6.adoptChildren(node7);
        System.out.println(node1);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
