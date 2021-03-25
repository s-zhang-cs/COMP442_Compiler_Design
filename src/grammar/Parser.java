package grammar;

import lexer.Lexer;
import symbol.Symbol;

import java.io.FileWriter;
import java.util.*;

public class Parser {

    private Symbol lookahead;
    private Symbol prevLookahead;
    private Lexer lexer;
    private FileWriter derivationOutput;
    private FileWriter derivationErrorOutput;
    private List<String> derivationRules;
    private List<String> derivationSteps;
    private Stack<AST> semanticRecords;

    public Parser(String filePath) throws Exception{
        lexer = new Lexer(filePath);
        String fileOutput = filePath.replace(".src", ".outderivation");
        derivationOutput = new FileWriter(fileOutput);
        String errorOutput = filePath.replace(".src", ".outsyntaxerrors");
        derivationErrorOutput = new FileWriter(errorOutput);
        derivationRules = new ArrayList<>();
        derivationSteps = new ArrayList<>();
        semanticRecords = new Stack<>();
    }

    public boolean parse() throws Exception{
        lookahead = nextToken();
        if(PROG() && match(new Symbol("$", true))) {
            processDerivation();
            for(String derivation : derivationSteps) {
                derivationOutput.write(derivation + "\n");
            }
            derivationOutput.close();
            derivationErrorOutput.close();
            return true;
        }
        processDerivation();
        for(String derivation : derivationSteps) {
            derivationOutput.write(derivation + "\n");
        }
        derivationOutput.close();
        derivationErrorOutput.close();
        return false;
    }

    private boolean match(Symbol s) throws Exception{
        System.out.println("[DEBUG]Current lookahead: " + lookahead + " (line: " + lexer.getLine() + ")");
        if(lookahead.equals(s)) {
            prevLookahead = lookahead;
            lookahead = nextToken();
            return true;
        }
        derivationErrorOutput.write("Found syntax error at line: " + lexer.getLine() + ". Unmatching token is: " + lookahead + ".");
        lookahead = nextToken();
        return false;
    }

    public void showAST() {
        System.out.println(semanticRecords.pop().toString());
    }

    private boolean skipError(Symbol LHS) throws Exception {
        Set<Symbol> first = LHS.computeFirstSet(LHS);
        Set<Symbol> follow = LHS.computeFollowSet(LHS, new HashSet<Symbol>());
        //no error
        if(first.contains(lookahead) || first.contains(new Symbol("EPSILON", true)) && follow.contains(lookahead)) {
            return true;
        }
        //error detected;
        else {
            //skip error until synchronizing set
            while(!first.contains(lookahead) && !follow.contains(lookahead)) {
                lookahead = nextToken();
                //pop current LHS from stack
                if(first.contains(new Symbol("EPSILON", true)) && follow.contains(lookahead)) {
                    return false;
                }
            }
            return true;
        }
    }

    private void registerDerivation(String s) throws Exception{
        derivationRules.add(s);
    }

    private void processDerivation() {
        for(int i = derivationRules.size() - 1; i >= 0; i--) {
            if(i == derivationRules.size() - 1) {
                derivationSteps.add(derivationRules.get(i));
                continue;
            }
            String[] splited = derivationRules.get(i).split("\\s+");
            String LHS = splited[0];
            String RHS = "";
            for(int j = 2; j < splited.length; j++) {
                RHS += splited[j];
                if(j != splited.length - 1) {
                    RHS += " ";
                }
            }
            String nextDerivation = derivationSteps.get(derivationSteps.size() - 1);
            nextDerivation = replaceLast(LHS, RHS, nextDerivation);
            derivationSteps.add(nextDerivation);
        }
    }

    private String replaceLast(String find, String replace, String string) {
        int lastIndex = string.lastIndexOf(find);
        if (lastIndex == -1) {
            return string;
        }
        String beginString = string.substring(0, lastIndex);
        String endString = string.substring(lastIndex + find.length());
        return beginString + replace + endString;
    }

    public boolean APARAMS() throws Exception{
        if(!skipError(new Symbol("AParams", false))) return false;
        boolean match = true;
        Symbol aParams = new Symbol("AParams", false);
        List<List<Symbol>> first = Grammar.productions.get(aParams);
        if(aParams.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<AParams> ::= <Expr> <AParamsTail>
            if(EXPR() && APARAMSTAIL()) {
                registerDerivation("<AParams> ::= <Expr> <AParamsTail>");
            }
            else {
                match = false;
            }
        }
        //<AParams> ::= epsilon
        else if(aParams.computeFollowSet(aParams, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<AParams> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean APARAMSTAIL() throws Exception{
        if(!skipError(new Symbol("AParamsTail", false))) return false;
        boolean match = true;
        Symbol aParamsTail = new Symbol("AParamsTail", false);
        if(lookahead.equals(new Symbol(",", true))) {
            //<AParamsTail> ::= ',' <Expr> <AParamsTail>
            if(match(new Symbol(",", true)) && EXPR() && APARAMSTAIL()) {
                registerDerivation("<AParamsTail> ::= ',' <Expr> <AParamsTail>");
            }
            else {
                match = false;
            }
        }
        //<AParamsTail> ::= epsilon
        else if(aParamsTail.computeFollowSet(aParamsTail, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<AParamsTail> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean ADDOP() throws Exception {
        if(!skipError(new Symbol("AddOp", false))) return false;
        boolean match = true;
        //<AddOp> ::= '+'
        if(lookahead.equals(new Symbol("+", true))) {
            if(match(new Symbol("+", true))) {
                registerDerivation("<AddOp> ::= '+'");
            }
        }
        //<AddOp> ::= '-'
        else if(lookahead.equals(new Symbol("-", true))) {
            if(match(new Symbol("-", true))) {
                registerDerivation("<AddOp> ::= '-'");
            }
        }
        //<AddOp> ::= 'or'
        else if(lookahead.equals(new Symbol("|", true))) {
            if(match(new Symbol("|", true))) {
                registerDerivation("<AddOp> ::= 'or'");
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean ARITHEXPR() throws Exception {
        if(!skipError(new Symbol("ArithExpr", false))) return false;
        boolean match = true;
        Symbol arithExpr = new Symbol("ArithExpr", false);
        List<List<Symbol>> first = Grammar.productions.get(arithExpr);
        if(arithExpr.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<ArithExpr> ::= <Term> <ArithExprTail>
            if(TERM() && ARITHEXPRTAIL()) {
                registerDerivation("<ArithExpr> ::= <Term> <ArithExprTail>");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean ARITHEXPRTAIL() throws Exception {
        if(!skipError(new Symbol("ArithExprTail", false))) return false;
        boolean match = true;
        Symbol arithExprTail = new Symbol("ArithExprTail", false);
        List<List<Symbol>> first = Grammar.productions.get(arithExprTail);
        if(arithExprTail.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<ArithExprTail> ::= <AddOp> <Term> <ArithExprTail>
            if(ADDOP() && TERM() && ARITHEXPRTAIL()) {
                registerDerivation("<ArithExprTail> ::= <AddOp> <Term> <ArithExprTail>");
            }
            else {
                match = false;
            }
        }
        //<ArithExprTail> ::= epsilon
        else if(arithExprTail.computeFollowSet(arithExprTail, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<ArithExprTail> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean ARRAYSIZEREPT() throws Exception {
        if(!skipError(new Symbol("ArraySizeRept", false))) return false;
        boolean match = true;
        Symbol arraySizeRept = new Symbol("ArraySizeRept", false);
        if(lookahead.equals(new Symbol("[", true))) {
            //<ArraySizeRept> ::= '[' <IntNum> ']' <ArraySizeRept>
            if(match(new Symbol("[", true)) && INTNUM() && match(new Symbol("]", true)) && ARRAYSIZEREPT()) {
                registerDerivation("<ArraySizeRept> ::= '[' <IntNum> ']' <ArraySizeRept>");
            }
            else {
                match = false;
            }
        }
        //<ArraySizeRept> ::= epsilon
        else if(arraySizeRept.computeFollowSet(arraySizeRept, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<ArraySizeRept> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean ASSIGNOP() throws Exception {
        if(!skipError(new Symbol("AssignOp", false))) return false;
        boolean match = true;
        if(lookahead.equals(new Symbol("assign", true))) {
            //<AssignOp> ::= 'assign'
            if(match(new Symbol("assign", true))) {
                registerDerivation("<AssignOp> ::= 'assign'");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean ASSIGNSTATTAIL() throws Exception {
        if(!skipError(new Symbol("AssignStatTail", false))) return false;
        boolean match = true;
        Symbol assignStatTail = new Symbol("AssignStatTail", false);
        List<List<Symbol>> first = Grammar.productions.get(assignStatTail);
        if(assignStatTail.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<AssignStatTail> ::= <AssignOp> <Expr>
            if(ASSIGNOP() && EXPR()) {
                registerDerivation("<AssignStatTail> ::= <AssignOp> <Expr>");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean CLASSDECL() throws Exception {
        if(!skipError(new Symbol("ClassDecl", false))) return false;
        boolean match = true;
        Symbol classDecl = new Symbol("ClassDecl", false);
        if(lookahead.equals(new Symbol("class", true))) {
            //<ClassDecl> ::= 'class' 'id' <Inherit> '{' <ClassDeclBody> '}' ';' <ClassDecl>
            if(markTree()
                    && match(new Symbol("class", true)) && match(new Symbol("id", true)) && makeLeaf(new ASTNode_Id(prevLookahead))
                    && markTree() && INHERIT() && makeTree(new ASTNode_InherList(new Symbol("InherList", false)))
                    && match(new Symbol("{", true))
                    && markTree() && CLASSDECLBODY() && makeTree(new ASTNode_MembList(new Symbol("MembList", false)))
                    && match(new Symbol("}", true)) && match(new Symbol(";", true))
              && makeTree(new ASTNode_ClassDecl(new Symbol("ClassDecl", false)))
              && CLASSDECL()
              )
            {
                registerDerivation("<ClassDecl> ::= 'class' 'id' <Inherit> '{' <ClassDeclBody> '}' ';' <ClassDecl>");
            }
            else {
                match = false;
            }
        }
        //<ClassDecl> ::= epsilon
        else if(classDecl.computeFollowSet(classDecl, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<ClassDecl> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean CLASSDECLBODY() throws Exception {
        if(!skipError(new Symbol("ClassDeclBody", false))) return false;
        boolean match = true;
        Symbol classDeclBody = new Symbol("ClassDeclBody", false);
        List<List<Symbol>> first = Grammar.productions.get(classDeclBody);
        if(classDeclBody.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<ClassDeclBody> ::= <Visibility> <MemberDecl> <ClassDeclBody>
            if(markTree()
                    && VISIBILITY()
                    && MEMBERDECL()
               && makeTree(new ASTNode_MembDecl(new Symbol("MembDecl", false)))
               && CLASSDECLBODY()
            ) {
                registerDerivation("<ClassDeclBody> ::= <Visibility> <MemberDecl> <ClassDeclBody> ");
            }
            else {
                match = false;
            }
        }
        //<ClassDeclBody> ::= epsilon
        else if(classDeclBody.computeFollowSet(classDeclBody, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<ClassDeclBody> ::= epsilon ");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean CLASSMETHOD() throws Exception {
        if(!skipError(new Symbol("ClassMethod", false))) return false;
        boolean match = true;
        Symbol classMethod = new Symbol("ClassMethod", false);
        if(lookahead.equals(new Symbol("sr", true))) {
            //<ClassMethod> ::= 'sr' 'id'
            if(match(new Symbol("sr", true)) && match(new Symbol("id", true))) {
                registerDerivation("<ClassMethod> ::= 'sr' 'id'");
            }
            else {
                match = false;
            }
        }
        //<ClassMethod> ::= epsilon
        else if(classMethod.computeFollowSet(classMethod, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<ClassMethod> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean EXPR() throws Exception {
        if(!skipError(new Symbol("Expr", false))) return false;
        boolean match = true;
        Symbol expr = new Symbol("Expr", false);
        List<List<Symbol>> first = Grammar.productions.get(expr);
        if(expr.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<Expr> ::= <ArithExpr> <ExprTail>
            if(ARITHEXPR() && EXPRTAIL()) {
                registerDerivation("<Expr> ::= <ArithExpr> <ExprTail>");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean EXPRTAIL() throws Exception {
        if(!skipError(new Symbol("ExprTail", false))) return false;
        boolean match = true;
        Symbol exprTail = new Symbol("ExprTail", false);
        List<List<Symbol>> first = Grammar.productions.get(exprTail);
        if(exprTail.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<ExprTail> ::= <RelOp> <ArithExpr>
            if(RELOP() && ARITHEXPR())
            {
                registerDerivation("<ExprTail> ::= <RelOp> <ArithExpr>");
            }
            else {
                match = false;
            }
        }
        //<ExprTail> ::= epsilon
        else if(exprTail.computeFollowSet(exprTail, new HashSet<>()).contains(lookahead)) {
            registerDerivation("<ExprTail> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FPARAMS() throws Exception {
        if(!skipError(new Symbol("FParams", false))) return false;
        boolean match = true;
        Symbol fParams = new Symbol("FParams", false);
        List<List<Symbol>> first = Grammar.productions.get(fParams);
        if(fParams.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<FParams> ::= <Type> 'id' <ArraySizeRept> <FParamsTail>
            if(markTree()
                &&markTree() && TYPE()
                && match(new Symbol("id", true)) && makeLeaf(new ASTNode_Id(prevLookahead))
                && ARRAYSIZEREPT()
                && makeTree(new ASTNode_FParam(new Symbol("FParam", false))) && FPARAMSTAIL()
              && makeTree(new ASTNode_FParamList(new Symbol("FParamList", false))))
            {
                registerDerivation("<FParams> ::= <Type> 'id' <ArraySizeRept> <FParamsTail>");
            }
            else {
                match = false;
            }
        }
        //<FParams> ::= epsilon
        else if(fParams.computeFollowSet(fParams, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<FParams> ::= epsilon ");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FPARAMSTAIL() throws Exception{
        if(!skipError(new Symbol("FParamsTail", false))) return false;
        boolean match = true;
        Symbol fParamsTail = new Symbol("FParamsTail", false);
        if(lookahead.equals(new Symbol(",", true))) {
            //<FParamsTail> ::= ',' <Type> 'id' <ArraySizeRept> <FParamsTail>
            if( markTree() && match(new Symbol(",", true))
                    && TYPE()
                    && match(new Symbol("id", true)) && makeLeaf(new ASTNode_Id(prevLookahead))
                    && ARRAYSIZEREPT()
             && makeTree(new ASTNode_FParam(new Symbol("FParam", false)))
             && FPARAMSTAIL())
            {
                registerDerivation("<FParamsTail> ::= ',' <Type> 'id' <ArraySizeRept> <FParamsTail>");
            }
            else {
                match = false;
            }
        }
        //<FParamsTail> ::= epsilon
        else if(fParamsTail.computeFollowSet(fParamsTail, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<FParamsTail> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FACTOR() throws Exception{
        if(!skipError(new Symbol("Factor", false))) return false;
        boolean match = true;
        Symbol factor = new Symbol("Factor", false);
        List<List<Symbol>> first = Grammar.productions.get(factor);
        if(factor.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<Factor> ::= <FuncOrVar>
            if(FUNCORVAR()) {
                registerDerivation("<Factor> ::= <FuncOrVar>");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("intnum", true))) {
            //<Factor> ::= 'intnum'
            if(match(new Symbol("intnum", true))) {
                registerDerivation("<Factor> ::= 'intnum'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("floatnum", true))) {
            //<Factor> ::= 'floatnum'
            if(match(new Symbol("floatnum", true))) {
                registerDerivation("<Factor> ::= 'floatnum'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("stringlit", true)))
        {
            //<Factor> ::= 'stringlit'
            if(match(new Symbol("stringlit", true))) {
                registerDerivation("<Factor> ::= 'stringlit'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("(", true)))
        {
            //<Factor> ::= '(' <Expr> ')'
            if(match(new Symbol("(", true)) && EXPR() && match(new Symbol(")", true))) {
                registerDerivation("<Factor> ::= '(' <Expr> ')'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("not", true)))
        {
            //<Factor> ::= 'not' <Factor>
            if(match(new Symbol("not", true)) && FACTOR()) {
                registerDerivation("<Factor> ::= 'not' <Factor>");
            }
            else {
                match = false;
            }
        }
        else if(factor.computeFirstSetForSymbolString(first.get(6)).contains(lookahead)) {
            //<Factor> ::= <Sign> <Factor>
            if(SIGN() && FACTOR())
            {
                registerDerivation("<Factor> ::= <Sign> <Factor>");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("qm", true))) {
            //<Factor> ::= 'qm' '[' <Expr> ':' <Expr> ':' <Expr> ']'
            if(match(new Symbol("qm", true))&& match(new Symbol("[", true)) && EXPR()
                    && match(new Symbol(":", true)) && EXPR() && match(new Symbol(":", true))
                    && EXPR() && match(new Symbol("]", true)))
            {
                registerDerivation("<Factor> ::= 'qm' '[' <Expr> ':' <Expr> ':' <Expr> ']'");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCBODY() throws Exception {
        if(!skipError(new Symbol("FuncBody", false))) return false;
        boolean match = true;
        if(lookahead.equals(new Symbol("{", true))) {
            //<FuncBody> ::= '{' <MethodBodyVar> <StatementList> '}'
            if(match(new Symbol("{", true)) && METHODBODYVAR()
                    && STATEMENTLIST() && match(new Symbol("}", true)))
            {
                registerDerivation("<FuncBody> ::= '{' <MethodBodyVar> <StatementList> '}'");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCDECL() throws Exception{
        if(!skipError(new Symbol("FuncDecl", false))) return false;
        boolean match = true;
        if(lookahead.equals(new Symbol("func", true)))
        {
            //<FuncDecl> ::= 'func' 'id' '(' <FParams> ')' ':' <FuncDeclTail> ';'
            if(match(new Symbol("func", true))
                    && match(new Symbol("id", true)) && makeLeaf(new ASTNode_Id(prevLookahead))
                    && match(new Symbol("(", true))
                    && FPARAMS()
                    && match(new Symbol(")", true)) && match(new Symbol(":", true))
                    && FUNCDECLTAIL() && match(new Symbol(";", true)))
            {
                registerDerivation("<FuncDecl> ::= 'func' 'id' '(' <FParams> ')' ':' <FuncDeclTail> ';' ");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCDECLTAIL() throws Exception {
        if(!skipError(new Symbol("FuncDeclTail", false))) return false;
        boolean match = true;
        Symbol funcDeclTail = new Symbol("FuncDeclTail", false);
        List<List<Symbol>> first = Grammar.productions.get(funcDeclTail);
        if(funcDeclTail.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<FuncDeclTail> ::= <Type>
            if(TYPE()){
                registerDerivation("<FuncDeclTail> ::= <Type>");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("void", true))) {
            //<FuncDeclTail> ::= 'void'
            if(match(new Symbol("void", true))) {
                registerDerivation("<FuncDeclTail> ::= 'void'");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCDEF() throws Exception {
        if(!skipError(new Symbol("FuncDef", false))) return false;
        boolean match = true;
        Symbol funcDef = new Symbol("FuncDef", false);
        List<List<Symbol>> first = Grammar.productions.get(funcDef);
        if(funcDef.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<FuncDef> ::= <Function> <FuncDef>
            if(FUNCTION() && FUNCDEF()) {
                registerDerivation("<FuncDef> ::= <Function> <FuncDef>");
            }
            else {
                match = false;
            }
        }
        //<FuncDef> ::= epsilon
        else if(funcDef.computeFollowSet(funcDef, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<FuncDef> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCHEAD() throws Exception {
        if(!skipError(new Symbol("FuncHead", false))) return false;
        boolean match = true;
        if(lookahead.equals(new Symbol("func", true)))
        {
            //<FuncHead> ::= 'func' 'id' <ClassMethod> '(' <FParams> ')' ':' <FuncDeclTail>
            if(match(new Symbol("func", true)) && match(new Symbol("id", true)) && CLASSMETHOD()
                    && match(new Symbol("(", true)) && FPARAMS() && match(new Symbol(")", true))
                    && match(new Symbol(":", true)) && FUNCDECLTAIL())
            {
                registerDerivation("<FuncHead> ::= 'func' 'id' <ClassMethod> '(' <FParams> ')' ':' <FuncDeclTail> ");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCORASSIGNSTAT() throws Exception {
        if(!skipError(new Symbol("FuncOrAssignStat", false))) return false;
        boolean match = true;
        if(lookahead.equals(new Symbol("id", true)))
        {
            //<FuncOrAssignStat> ::= 'id' <FuncOrAssignStatIdnest>
            if(match(new Symbol("id", true)) && FUNCORASSIGNSTATIDNEST())
            {
                registerDerivation("<FuncOrAssignStat> ::= 'id' <FuncOrAssignStatIdnest> ");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCORASSIGNSTATIDNEST() throws Exception {
        if(!skipError(new Symbol("FuncOrAssignStatIdnest", false))) return false;
        boolean match = true;
        Symbol funcOrAssignStatIdNest = new Symbol("FuncOrAssignStatIdnest", false);
        List<List<Symbol>> first = Grammar.productions.get(funcOrAssignStatIdNest);
        if(funcOrAssignStatIdNest.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<FuncOrAssignStatIdnest> ::= <IndiceRep> <FuncOrAssignStatIdnestVarTail>
            if(INDICEREP() && FUNCORASSIGNSTATIDNESTVARTAIL())
            {
                registerDerivation("<FuncOrAssignStatIdnest> ::= <IndiceRep> <FuncOrAssignStatIdnestVarTail> ");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("(", true)))
        {
            //<FuncOrAssignStatIdnest> ::= '(' <AParams> ')' <FuncOrAssignStatIdnestFuncTail>
            if(match(new Symbol("(", true)) && APARAMS() && match(new Symbol(")", true))
                    && FUNCORASSIGNSTATIDNESTFUNCTAIL())
            {
                registerDerivation("<FuncOrAssignStatIdnest> ::= '(' <AParams> ')' <FuncOrAssignStatIdnestFuncTail> ");
            }
            else {
                match = false;
            }
        }
        //<FuncOrAssignStatIdnest> ::= EPSILON
        else if(funcOrAssignStatIdNest.computeFollowSet(funcOrAssignStatIdNest, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<FuncOrAssignStatIdnest> ::= EPSILON");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCORASSIGNSTATIDNESTFUNCTAIL() throws Exception {
        if(!skipError(new Symbol("FuncOrAssignStatIdnestFuncTail", false))) return false;
        boolean match = true;
        Symbol funcOrAssignStatIdnestFuncTail = new Symbol("FuncOrAssignStatIdnestFuncTail", false);
        if(lookahead.equals(new Symbol(".", true))) {
            //<FuncOrAssignStatIdnestFuncTail> ::= '.' 'id' <FuncStatTail>
            if(match(new Symbol(".", true)) && match(new Symbol("id", true)) && FUNCSTATTAIL()) {
                registerDerivation("<FuncOrAssignStatIdnestFuncTail> ::= '.' 'id' <FuncStatTail> ");
            }
            else {
                match = false;
            }
        }
        //<FuncOrAssignStatIdnestFuncTail> ::= epsilon
        else if(funcOrAssignStatIdnestFuncTail.computeFollowSet(funcOrAssignStatIdnestFuncTail, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<FuncOrAssignStatIdnestFuncTail> ::= epsilon ");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCORASSIGNSTATIDNESTVARTAIL() throws Exception {
        if(!skipError(new Symbol("FuncOrAssignStatIdnestVarTail", false))) return false;
        boolean match = true;
        Symbol funcOrAssignStatIdnestVarTail = new Symbol("FuncOrAssignStatIdnestVarTail", false);
        List<List<Symbol>> first = Grammar.productions.get(funcOrAssignStatIdnestVarTail);
        if(lookahead.equals(new Symbol(".", true))) {
            //<FuncOrAssignStatIdnestVarTail> ::= '.' 'id' <FuncOrAssignStatIdnest>
            if(match(new Symbol(".", true)) && match(new Symbol("id", true)) && FUNCORASSIGNSTATIDNEST())
            {
                registerDerivation("<FuncOrAssignStatIdnestVarTail> ::= '.' 'id' <FuncOrAssignStatIdnest>");
            }
            else {
                match = false;
            }
        }
        else if(funcOrAssignStatIdnestVarTail.computeFirstSetForSymbolString(first.get(1)).contains(lookahead)) {
            //<FuncOrAssignStatIdnestVarTail> ::= <AssignStatTail>
            if(ASSIGNSTATTAIL())
            {
                registerDerivation("<FuncOrAssignStatIdnestVarTail> ::= <AssignStatTail> ");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCORVAR() throws Exception{
        if(!skipError(new Symbol("FuncOrVar", false))) return false;
        boolean match = true;
        if(lookahead.equals(new Symbol("id", true))) {
            //<FuncOrVar> ::= 'id' <FuncOrVarIdnest>
            if(match(new Symbol("id", true)) && FUNCORVARIDNEST()) {
                registerDerivation("<FuncOrVar> ::= 'id' <FuncOrVarIdnest>");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCORVARIDNEST() throws Exception {
        if(!skipError(new Symbol("FuncOrVarIdnest", false))) return false;
        boolean match = true;
        Symbol funcOrVarIdnest = new Symbol("FuncOrVarIdnest", false);
        List<List<Symbol>> first = Grammar.productions.get(funcOrVarIdnest);
        if(funcOrVarIdnest.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<FuncOrVarIdnest> ::= <IndiceRep> <FuncOrVarIdnestTail>
            if(INDICEREP() && FUNCORVARIDNESTTAIL())
            {
                registerDerivation("<FuncOrVarIdnest> ::= <IndiceRep> <FuncOrVarIdnestTail>");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("(", true))) {
            //<FuncOrVarIdnest> ::= '(' <AParams> ')' <FuncOrVarIdnestTail>
            if(match(new Symbol("(", true)) && APARAMS() && match(new Symbol(")", true)) && FUNCORVARIDNESTTAIL())
            {
                registerDerivation("<FuncOrVarIdnest> ::= '(' <AParams> ')' <FuncOrVarIdnestTail>");
            }
            else {
                match = false;
            }
        }
        //<FuncOrVarIdnest> ::= EPSILON
        else if(funcOrVarIdnest.computeFollowSet(funcOrVarIdnest, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<FuncOrVarIdnest> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCORVARIDNESTTAIL() throws Exception {
        if(!skipError(new Symbol("FuncOrVarIdnestTail", false))) return false;
        boolean match = true;
        Symbol funcOrVarIdnestTail = new Symbol("FuncOrVarIdnestTail", false);
        if(lookahead.equals(new Symbol(".", true))) {
            //<FuncOrVarIdnestTail> ::= '.' 'id' <FuncOrVarIdnest>
            if(match(new Symbol(".", true)) && match(new Symbol("id", true)) && FUNCORVARIDNEST())
            {
                registerDerivation("<FuncOrVarIdnestTail> ::= '.' 'id' <FuncOrVarIdnest>");
            }
            else {
                match = false;
            }
        }
        //<FuncOrVarIdnestTail> ::= epsilon
        else if(funcOrVarIdnestTail.computeFollowSet(funcOrVarIdnestTail, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<FuncOrVarIdnestTail> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCSTATTAIL() throws Exception {
        if(!skipError(new Symbol("FuncStatTail", false))) return false;
        boolean match = true;
        Symbol funcStatTail = new Symbol("FuncStatTail", false);
        List<List<Symbol>> first = Grammar.productions.get(funcStatTail);
        if(funcStatTail.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<FuncStatTail> ::= <IndiceRep> <FuncStatTailIdnest>
            if(INDICEREP() && FUNCSTATTAILIDNEST())
            {
                registerDerivation("<FuncStatTail> ::= <IndiceRep> <FuncStatTailIdnest>");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("(", true)))
        {
            //<FuncStatTail> ::= '(' <AParams> ')' <FuncStatTailIdnest>
            if(match(new Symbol("(", true)) && APARAMS() && match(new Symbol(")", true)) && FUNCSTATTAILIDNEST())
            {
                registerDerivation("<FuncStatTail> ::= '(' <AParams> ')' <FuncStatTailIdnest>");
            }
            else {
                match = false;
            }
        }
        //<FuncStatTail> ::= EPSILON
        else if(funcStatTail.computeFollowSet(funcStatTail, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<FUNCSTATTAIL> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCSTATTAILIDNEST() throws Exception {
        if(!skipError(new Symbol("FuncStatTailIdnest", false))) return false;
        boolean match = true;
        Symbol funcStatTailIdnest = new Symbol("FuncStatTailIdnest", false);
        if(lookahead.equals(new Symbol(".", true))) {
            //<FuncStatTailIdnest> ::= '.' 'id' <FuncStatTail>
            if(match(new Symbol(".", true)) && match(new Symbol("id", true)) && FUNCSTATTAIL())
            {
                registerDerivation("<FuncStatTailIdnest> ::= '.' 'id' <FuncStatTail>");
            }
            else {
                match = false;
            }
        }
        //<FuncStatTailIdnest> ::= epsilon
        else if(funcStatTailIdnest.computeFollowSet(funcStatTailIdnest, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<FuncStatTailIdnest> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean FUNCTION() throws Exception {
        if(!skipError(new Symbol("Function", false))) return false;
        boolean match = true;
        Symbol function = new Symbol("Function", false);
        List<List<Symbol>> first = Grammar.productions.get(function);
        if(function.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<Function> ::= <FuncHead> <FuncBody>
            if(FUNCHEAD() && FUNCBODY())
            {
                registerDerivation("<Function> ::= <FuncHead> <FuncBody>");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean INDICEREP() throws Exception {
        if(!skipError(new Symbol("IndiceRep", false))) return false;
        boolean match = true;
        Symbol indiceRep = new Symbol("IndiceRep", false);
        if(lookahead.equals(new Symbol("[", true)))
        {
            //<IndiceRep> ::= '[' <Expr> ']' <IndiceRep>
            if(match(new Symbol("[", true)) && EXPR() && match(new Symbol("]", true)) && INDICEREP())
            {
                registerDerivation("<IndiceRep> ::= '[' <Expr> ']' <IndiceRep>");
            }
            else {
                match = false;
            }
        }
        //<IndiceRep> ::= epsilon
        else if(indiceRep.computeFollowSet(indiceRep, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<IndiceRep> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean INHERIT() throws Exception{
        if(!skipError(new Symbol("Inherit", false))) return false;
        boolean match = true;
        Symbol inherit = new Symbol("Inherit", false);
        if(lookahead.equals(new Symbol("inherits", true)))
        {
            //<Inherit> ::= 'inherits' 'id' <NestedId>
            if(
                    match(new Symbol("inherits", true))
                    && match(new Symbol("id", true)) && makeLeaf(new ASTNode_Inher(prevLookahead))
                    && NESTEDID()
              )
            {
                registerDerivation("<Inherit> ::= 'inherits' 'id' <NestedId>");
            }
            else {
                match = false;
            }
        }
        //<Inherit> ::= epsilon
        else if(inherit.computeFollowSet(inherit, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<Inherit> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean INTNUM() throws Exception {
        if(!skipError(new Symbol("IntNum", false))) return false;
        boolean match = true;
        Symbol intNum = new Symbol("IntNum", false);
        if(lookahead.equals(new Symbol("intnum", true))) {
            //<IntNum> ::= 'intnum'
            if(match(new Symbol("intnum", true))) {
                registerDerivation("<IntNum> ::= 'intnum'");
            }
            else {
                match = false;
            }
        }
        //<IntNum> ::= epsilon
        else if(intNum.computeFollowSet(intNum, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<IntNum> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean MEMBERDECL() throws Exception {
        if(!skipError(new Symbol("MemberDecl", false))) return false;
        boolean match = true;
        Symbol memberDecl = new Symbol("MemberDecl", false);
        List<List<Symbol>> first = Grammar.productions.get(memberDecl);
        if(memberDecl.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<MemberDecl> ::= <FuncDecl>
            if(markTree() && FUNCDECL() && makeTree(new ASTNode_FuncDef(new Symbol("FuncDef", false)))) {
                registerDerivation("<MemberDecl> ::= <FuncDecl>");
            }
            else {
                match = false;
            }
        }
        else if(memberDecl.computeFirstSetForSymbolString(first.get(1)).contains(lookahead)) {
            //<MemberDecl> ::= <VarDecl>
            if(VARDECL())
            {
                registerDerivation("<MemberDecl> ::= <VarDecl>");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean METHODBODYVAR() throws Exception {
        if(!skipError(new Symbol("MethodBodyVar", false))) return false;
        boolean match = true;
        Symbol methodBodyVar = new Symbol("MethodBodyVar", false);
        if(lookahead.equals(new Symbol("var", true)))
        {
            //<MethodBodyVar> ::= 'var' '{' <VarDeclRep> '}'
            if(match(new Symbol("var", true)) && match(new Symbol("{", true)) && VARDECLREP() && match(new Symbol("}", true))) {
                registerDerivation("<MethodBodyVar> ::= 'var' '{' <VarDeclRep> '}'");
            }
            else {
                match = false;
            }
        }
        //<MethodBodyVar> ::= epsilon
        else if(methodBodyVar.computeFollowSet(methodBodyVar, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<MethodBodyVar> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean MULTOP() throws Exception {
        if(!skipError(new Symbol("MultOp", false))) return false;
        boolean match = true;
        if(lookahead.equals(new Symbol("*", true)))
        {
            //<MultOp> ::= '*'
            if(match(new Symbol("*", true))) {
                registerDerivation("<MultOp> ::= '*'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("/", true)))
        {
            //<MultOp> ::= '/'
            if(match(new Symbol("/", true))) {
                registerDerivation("<MultOp> ::= '/'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("&", true)))
        {
            //<MultOp> ::= 'and'
            if(match(new Symbol("&", true))) {
                registerDerivation("<MultOp> ::= 'and'");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean NESTEDID() throws Exception {
        if(!skipError(new Symbol("NestedId", false))) return false;
        boolean match = true;
        Symbol nestedId = new Symbol("NestedId", false);
        if(lookahead.equals(new Symbol(",", true))) {
            //<NestedId> ::= ',' 'id' <NestedId>
            if(
                    match(new Symbol(",", true))
                    && match(new Symbol("id", true)) && makeLeaf(new ASTNode_Inher(prevLookahead))
                    && NESTEDID())
            {
                registerDerivation("<NestedId> ::= ',' 'id' <NestedId>");
            }
            else {
                match = false;
            }
        }
        //<NestedId> ::= epsilon
        else if(nestedId.computeFollowSet(nestedId, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<NestedId> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean PROG() throws Exception {
        if(!skipError(new Symbol("Prog", false))) return false;
        boolean match = true;
        Symbol prog = new Symbol("Prog", false);
        List<List<Symbol>> first = Grammar.productions.get(prog);
        if(prog.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<Prog> ::= <ClassDecl> <FuncDef> 'main' <FuncBody>
            if(markTree()
                    && markTree() && CLASSDECL() && makeTree(new ASTNode_ClassList(new Symbol("ClassList", false)))
                    && markTree() && FUNCDEF() && makeTree(new ASTNode_FuncDefList(new Symbol("FuncDefList", false)))
                    && match(new Symbol("main", true))
                    && FUNCBODY()
               && makeTree(new ASTNode_Prog(new Symbol("Prog", false))))
            {
                registerDerivation("<Prog> ::= <ClassDecl> <FuncDef> 'main' <FuncBody>");
            }
            else {
                match = false;
            }
        }
        //<Prog> ::= EPSILON
        else if(prog.computeFollowSet(prog, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<Prog> ::= EPSILON");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean RELOP() throws Exception {
        if(!skipError(new Symbol("RelOp", false))) return false;
        boolean match = true;
        if(lookahead.equals(new Symbol("eq", true))) {
            //<RelOp> ::= 'eq'
            if(match(new Symbol("eq", true))) {
                registerDerivation("<RelOp> ::= 'eq'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("neq", true))) {
            //<RelOp> ::= 'neq'
            if(match(new Symbol("neq", true))) {
                registerDerivation("<RelOp> ::= 'neq'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("lt", true))) {
            //<RelOp> ::= 'lt'
            if(match(new Symbol("lt", true))) {
                registerDerivation("<RelOp> ::= 'lt'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("gt", true)))
        {
            //<RelOp> ::= 'gt'
            if(match(new Symbol("gt", true))) {
                registerDerivation("<RelOp> ::= 'gt'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("leq", true)))
        {
            //<RelOp> ::= 'leq'
            if(match(new Symbol("leq", true))) {
                registerDerivation("<RelOp> ::= 'leq'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("geq", true)))
        {
            //<RelOp> ::= 'geq'
            if(match(new Symbol("geq", true))) {
                registerDerivation("<RelOp> ::= 'geq'");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean SIGN() throws Exception {
        if(!skipError(new Symbol("Sign", false))) return false;
        boolean match = true;
        if(lookahead.equals(new Symbol("+", true)))
        {
            //<Sign> ::= '+'
            if(match(new Symbol("+", true))) {
                registerDerivation("<Sign> ::= '+'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("-", true)))
        {
            //<Sign> ::= '-'
            if(match(new Symbol("-", true))) {
                registerDerivation("<Sign> ::= '-'");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean STATBLOCK() throws Exception {
        if(!skipError(new Symbol("StatBlock", false))) return false;
        boolean match = true;
        Symbol statBlock = new Symbol("StatBlock", false);
        List<List<Symbol>> first = Grammar.productions.get(statBlock);
        if(lookahead.equals(new Symbol("{", true)))
        {
            //<StatBlock> ::= '{' <StatementList> '}'
            if(match(new Symbol("{", true)) && STATEMENTLIST() && match(new Symbol("}", true))) {
                registerDerivation("<StatBlock> ::= '{' <StatementList> '}'");
            }
            else {
                match = false;
            }
        }
        else if(statBlock.computeFirstSetForSymbolString(first.get(1)).contains(lookahead)) {
            //<StatBlock> ::= <Statement>
            if(STATEMENT()) {
                registerDerivation("<StatBlock> ::= <Statement>");
            }
            else {
                match = false;
            }
        }
        //<StatBlock> ::= epsilon
        else if(statBlock.computeFollowSet(statBlock, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<StatBlock> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean STATEMENT() throws Exception {
        if(!skipError(new Symbol("Statement", false))) return false;
        boolean match = true;
        Symbol statement = new Symbol("Statement", false);
        List<List<Symbol>> first = Grammar.productions.get(statement);
        if(statement.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<Statement> ::= <FuncOrAssignStat> ';'
            if(FUNCORASSIGNSTAT() && match(new Symbol(";", true))) {
                registerDerivation("<Statement> ::= <FuncOrAssignStat> ';'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("if", true))) {
            //<Statement> ::= 'if' '(' <Expr> ')' 'then' <StatBlock> 'else' <StatBlock> ';'
            if(match(new Symbol("if", true)) && match(new Symbol("(", true)) && EXPR()
                    && match(new Symbol(")", true)) && match(new Symbol("then", true)) && STATBLOCK()
                    && match(new Symbol("else", true)) && STATBLOCK() && match(new Symbol(";", true)))
            {
                registerDerivation("<Statement> ::= 'if' '(' <Expr> ')' 'then' <StatBlock> 'else' <StatBlock> ';'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("while", true))) {
            //<Statement> ::= 'while' '(' <Expr> ')' <StatBlock> ';'
            if(match(new Symbol("while", true)) && match(new Symbol("(", true)) && EXPR()
                    && match(new Symbol(")", true)) && STATBLOCK() && match(new Symbol(";", true)))
            {
                registerDerivation("<Statement> ::= 'while' '(' <Expr> ')' <StatBlock> ';'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("read", true))) {
            //<Statement> ::= 'read' '(' <Variable> ')' ';'
            if(match(new Symbol("read", true)) && match(new Symbol("(", true)) && VARIABLE()
                    && match(new Symbol(")", true)) && match(new Symbol(";", true)))
            {
                registerDerivation("<Statement> ::= 'read' '(' <Variable> ')' ';'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("write", true))) {
            //<Statement> ::= 'write' '(' <Expr> ')' ';'
            if(match(new Symbol("write", true)) && match(new Symbol("(", true)) && EXPR()
                    && match(new Symbol(")", true)) && match(new Symbol(";", true)))
            {
                registerDerivation("<Statement> ::= 'write' '(' <Expr> ')' ';'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("return", true))) {
            //<Statement> ::= 'return' '(' <Expr> ')' ';'
            if(match(new Symbol("return", true)) && match(new Symbol("(", true)) && EXPR()
                    && match(new Symbol(")", true)) && match(new Symbol(";", true)))
            {
                registerDerivation("<Statement> ::= 'return' '(' <Expr> ')' ';'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("break", true))) {
            //<Statement> ::= 'break' ';'
            if(match(new Symbol("break", true)) && match(new Symbol(";", true))) {
                registerDerivation("<Statement> ::= 'break' ';'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals("continue")) {
            //<Statement> ::= 'continue' ';'
            if(match(new Symbol("continue", true)) && match(new Symbol(";", true))) {
                registerDerivation("<Statement> ::= 'continue' ';'");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean STATEMENTLIST() throws Exception {
        if(!skipError(new Symbol("StatementList", false))) return false;
        boolean match = true;
        Symbol statementList = new Symbol("StatementList", false);
        List<List<Symbol>> first = Grammar.productions.get(statementList);
        if(statementList.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<StatementList> ::= <Statement> <StatementList>
            if(STATEMENT() && STATEMENTLIST()) {
                registerDerivation("<StatementList> ::= <Statement> <StatementList>");
            }
            else {
                match = false;
            }
        }
        //<StatementList> ::= epsilon
        else if(statementList.computeFollowSet(statementList, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<StatementList> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean TERM() throws Exception {
        if(!skipError(new Symbol("Term", false))) return false;
        boolean match = true;
        Symbol term = new Symbol("Term", false);
        List<List<Symbol>> first = Grammar.productions.get(term);
        if(term.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<Term> ::= <Factor> <TermTail>
            if(FACTOR() && TERMTAIL()) {
                registerDerivation("<Term> ::= <Factor> <TermTail>");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean TERMTAIL() throws Exception {
        if(!skipError(new Symbol("TermTail", false))) return false;
        boolean match = true;
        Symbol termTail = new Symbol("TermTail", false);
        List<List<Symbol>> first = Grammar.productions.get(termTail);
        if(termTail.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<TermTail> ::= <MultOp> <Factor> <TermTail>
            if(MULTOP() && FACTOR() && TERMTAIL()) {
                registerDerivation("<TermTail> ::= <MultOp> <Factor> <TermTail>");
            }
            else {
                match = false;
            }
        }
        //<TermTail> ::= epsilon
        else if(termTail.computeFollowSet(termTail, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<TermTail> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean TYPE() throws Exception {
        if(!skipError(new Symbol("Type", false))) return false;
        boolean match = true;
        if(lookahead.equals(new Symbol("integer", true))) {
            //<Type> ::= 'integer'
            if(match(new Symbol("integer", true)) && makeLeaf(new ASTNode_Type(prevLookahead))) {
                registerDerivation("<Type> ::= 'integer'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("float", true))) {
            //<Type> ::= 'float'
            if(match(new Symbol("float", true)) && makeLeaf(new ASTNode_Type(prevLookahead))) {
                registerDerivation("<Type> ::= 'float'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("string", true))) {
            //<Type> ::= 'string'
            if(match(new Symbol("string", true)) && makeLeaf(new ASTNode_Type(prevLookahead))) {
                registerDerivation("<Type> ::= 'string'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("id", true))) {
            //<Type> ::= 'id'
            if(match(new Symbol("id", true)) && makeLeaf(new ASTNode_Type(prevLookahead))) {
                registerDerivation("<Type> ::= 'id'");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean VARDECL() throws Exception {
        if(!skipError(new Symbol("VarDecl", false))) return false;
        boolean match = true;
        Symbol varDecl = new Symbol("VarDecl", false);
        List<List<Symbol>> first = Grammar.productions.get(varDecl);
        if(varDecl.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<VarDecl> ::= <Type> 'id' <ArraySizeRept> ';'
            if(TYPE() && match(new Symbol("id", true)) && ARRAYSIZEREPT() && match(new Symbol(";", true))) {
                registerDerivation("<VarDecl> ::= <Type> 'id' <ArraySizeRept> ';'");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean VARDECLREP() throws Exception {
        if(!skipError(new Symbol("VarDeclRep", false))) return false;
        boolean match = true;
        Symbol varDeclRep = new Symbol("VarDeclRep", false);
        List<List<Symbol>> first = Grammar.productions.get(varDeclRep);
        if(varDeclRep.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<VarDeclRep> ::= <VarDecl> <VarDeclRep>
            if(VARDECL() && VARDECLREP()) {
                registerDerivation("<VarDeclRep> ::= <VarDecl> <VarDeclRep>");
            }
            else {
                match = false;
            }
        }
        //<VarDeclRep> ::= epsilon
        else if(varDeclRep.computeFollowSet(varDeclRep, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<VarDeclRep> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean VARIABLE() throws Exception {
        if(!skipError(new Symbol("Variable", false))) return false;
        boolean match = true;
        if(lookahead.equals(new Symbol("id", true)))
        {
            //<Variable> ::= 'id' <VariableIdnest>
            if(match(new Symbol("id", true)) && VARIABLEIDNEST()) {
                registerDerivation("<Variable> ::= 'id' <VariableIdnest>");
            }
            else {
                match = false;
            }
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean VARIABLEIDNEST() throws Exception {
        if(!skipError(new Symbol("VariableIdnest", false))) return false;
        boolean match = true;
        Symbol variableIdnest = new Symbol("VariableIdnest", false);
        List<List<Symbol>> first = Grammar.productions.get(variableIdnest);
        if(variableIdnest.computeFirstSetForSymbolString(first.get(0)).contains(lookahead)) {
            //<VariableIdnest> ::= <IndiceRep> <VariableIdnestTail>
            if(markTree() && INDICEREP()  && VARIABLEIDNESTTAIL()) {
                registerDerivation("<VariableIdnest> ::= <IndiceRep> <VariableIdnestTail>");
            }
            else {
                match = false;
            }
        }
        //<VariableIdnest> ::= EPSILON
        else if(variableIdnest.computeFollowSet(variableIdnest, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<VariableIdnest> ::= EPSILON");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean VARIABLEIDNESTTAIL() throws Exception {
        if(!skipError(new Symbol("VariableIdnestTail", false))) return false;
        boolean match = true;
        Symbol variableIdnestTail = new Symbol("VariableIdnestTail", false);
        if(lookahead.equals(new Symbol(".", true)))
        {
            //<VariableIdnestTail> ::= '.' 'id' <VariableIdnest>
            if(match(new Symbol(".", true)) && match(new Symbol("id", true)) && VARIABLEIDNEST()) {
                registerDerivation("<VariableIdnestTail> ::= '.' 'id' <VariableIdnest>");
            }
            else {
                match = false;
            }
        }
        //<VariableIdnestTail> ::= epsilon
        else if(variableIdnestTail.computeFollowSet(variableIdnestTail, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<VariableIdnestTail> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public boolean VISIBILITY() throws Exception {
        if(!skipError(new Symbol("Visibility", false))) return false;
        boolean match = true;
        Symbol visibility = new Symbol("Visibility", false);
        if(lookahead.equals(new Symbol("public", true))) {
            //<Visibility> ::= 'public'
            if(match(new Symbol("public", true)) && makeLeaf(new ASTNode_Visibility(prevLookahead))) {
                registerDerivation("<Visibility> ::= 'public'");
            }
            else {
                match = false;
            }
        }
        else if(lookahead.equals(new Symbol("private", true))) {
            //<Visibility> ::= 'private'
            if(match(new Symbol("private", true)) && makeLeaf(new ASTNode_Visibility(prevLookahead))) {
                registerDerivation("<Visibility> ::= 'private'");
            }
            else {
                match = false;
            }
        }
        //<Visibility> ::= epsilon
        else if(visibility.computeFollowSet(visibility, new HashSet<Symbol>()).contains(lookahead)) {
            registerDerivation("<Visibility> ::= epsilon");
        }
        else {
            match = false;
        }
        return match;
    }

    public Symbol nextToken() throws Exception{
        Symbol s = null;
        if(!lexer.reachedEOF()) {
            s = lexer.nextToken();
        }
        else {
            s = new Symbol("$", "EOF", true);
        }
        return s;
    }

    private boolean makeLeaf(AST ast) {
        semanticRecords.push(ast);
        return true;
    }

    private boolean makeNode(AST ast) {
        semanticRecords.push(ast);
        return true;
    }

    private boolean makeTree(AST ast) {
        AST child = semanticRecords.pop();
        while(child != null) {
            ast.adoptChildren(child);
            child = semanticRecords.pop();
        }
        semanticRecords.push(ast);
        return true;
    }

    private boolean migrateTree() {
        //no need to migrate when nothing is there from sibling
        if(semanticRecords.peek() == null) {
            return true;
        }
        Queue<AST> q = new LinkedList<>();
        while(semanticRecords.peek() != null) {
            q.add(semanticRecords.pop());
        }
        semanticRecords.push(null);
        while(!q.isEmpty()) {
            semanticRecords.push(q.remove());
        }
        return true;
    }

//    private boolean makeTree(Symbol s, int n) {
//        return true;
//    }

    private boolean markTree() {
        semanticRecords.push(null);
        return true;
    }
}
