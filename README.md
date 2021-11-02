# COMPILER FRONTEND

Part of the course work for COMP442 (Compiler Design) taught by professor Joey Paquet at Concordia University for Winter2021.
Includes implementation of the compiler frontend (lexer, parser and AST generation). The following diagram shows the program 
architecture. Only the conceptually important attributes and methods are shown.

<img src="./resources/doc/Untitled Diagram.drawio.png">

Since it is my first time implementing a compiler. The program results from a lot of trial and error and bears its
cost. I believe that after continuous improvement the frontend is now rather clean, and I thus only uploaded the frontend. 
I will upload the "cleaned" backend part once no other job takes priority.

The lexical specification is as follow:

|   element	|   ::-	|   composition	|
|---	|---	|---	|
|   id	|   ::-	|   letter alphanum*	|
|   alphanum	|   ::-	|   letter  ⎮ digit ⎮ _	|
|   integer	|   ::-	|   nonzero digit* ⎮ 0	|
|   float	|   ::-	|   integer fraction [e[ + ⎮ - ] integer]	|
|   fraction	|   ::-	|   .digit* nonzero ⎮ .0	|
|   letter	|   ::-	|   a..z ⎮ A..Z	|
|   digit	|   ::-	|   0..9	|
|   nonzero	|   ::-	|   1..9	|
|   string	|   ::-	|   " character* "	|
|   character   |   ::- |    alphanum ⎮ space   |

The grammar is as follow:
```$xslt
G = (N, T, S, R)

N- Nonterminal Symbols
START, aParams, aParamsTail, addOp, arithExpr, arraySize, assignOp, assignStat, classDecl,
expr, fParams, fParamsTail, factor, funcBody, funcDecl, funcDef, funcHead, functionCall,
idnest, indice, memberDecl, multOp, prog, relExpr, sign, statBlock, statement, term, type,
varDecl, variable, visibility

T- Terminal Symbols
, +, -, I, [, intlit, ], =, class, id, {, }, ; , (, ) , float lit, ! , :, void, . , *, I, &,
inherits, sr, main, eq, geq, gt, leq, lt, neq, if, then, else, read, return, while, write,
float, integer, private, public, func, var, break, continue, string, qm, stringlit

S- Starting Symbol
START

R – Rules
<START> ::= <prog>
<prog> ::= {{<classDecl>}} {{<funcDef>}} 'main' <funcBody>
<classDecl> ::= 'class' 'id' [['inherits' 'id' {{',' 'id'}}]] '{' {{<visibility> <memberDecl>}} '}' ';'
<visibility> ::= 'public' | 'private' | EPSILON
<memberDecl> ::= <funcDecl> | <varDecl>
<funcDecl> ::= 'func' 'id' '(' <fParams> ')' ':' <type> ';'
            | 'func' 'id' '(' <fParams> ')' ':' 'void' ';'
<funcHead> ::= 'func' [['id' 'sr']] 'id' '(' <fParams> ')' ':' <type>
            | 'func' [['id' 'sr']] 'id' '(' <fParams> ')' ':' 'void'
<funcDef> ::= <funcHead> <funcBody>
<funcBody> ::= '{' [[ 'var' '{' {{<varDecl>}} '}' ]] {{<statement>}} '}'
<varDecl> ::= <type> 'id' {{<arraySize>}} ';'
<statement> ::= <assignStat> ';'
            | 'if' '(' <relExpr> ')' 'then' <statBlock> 'else' <statBlock> ';'
            | 'while' '(' <relExpr> ')' <statBlock> ';'
            | 'read' '(' <variable> ')' ';'
            | 'write' '(' <expr> ')' ';'
            | 'return' '(' <expr> ')' ';'
            | 'break' ';'
            | 'continue' '; '
            | <functionCall> ';'
<assignStat> ::= <variable> <assignOp> <expr>
<statBlock> ::= '{' {{<statement>}} '}' | <statement> | EPSILON
<expr> ::= <arithExpr> | <relExpr>
<relExpr> ::= <arithExpr> <relOp> <arithExpr>
<arithExpr> ::= <arithExpr> <addOp> <term> | <term>
<sign> ::= '+' | '-'
<term> ::= <term> <multOp> <factor> | <factor>
<factor> ::= <variable>
            | <functionCall>
            | 'intLit' | 'floatLit' | 'stringLit'
            | '(' <arithExpr> ')'
            | 'not' <factor>
            | <sign> <factor>
            | 'qm' '[' <expr> ':' <expr> ':' <expr> ']'
<variable> ::= {{<idnest>}} 'id' {{<indice>}}
<functionCall> ::= {{<idnest>}} 'id' '(' <aParams> ')'
<idnest> ::= 'id' {{<indice>}} '.'
            | 'id' '(' <aParams> ')' '.'
<indice> ::= '[' <arithExpr> ']'
<arraySize> ::= '[' 'intNum' ']' | '[' ']'
<type> ::= 'integer' | 'float' | 'string' | 'id'
<fParams> ::= <type> 'id' {{<arraySize>}} {{<fParamsTail>}} | EPSILON
<aParams> ::= <expr> {{<aParamsTail>}} | EPSILON
<fParamsTail> ::= ',' <type> 'id' {{<arraySize>}}
<aParamsTail> ::= ',' <expr>
<assignOp> ::= '='
<relOp> ::= 'eq' | 'neq' | 'lt' | 'gt' | 'leq' | 'geq'
<addOp> ::= '+' | '-' | 'or'
<multOp> ::= '*' | '/' | 'and'
```

DEMO:
for the following 'moon' source code:
```//Sort the array
   func bubbleSort(integer arr[], integer size) : void
   {
     var
     {
       integer n;
       integer i;
       integer j;
       integer temp;
     }
     n = size;
     i = 0;
     j = 0;
     temp = 0;
     while (i < n-1) {
       while (j < n-i-1) {
         if (arr[j] > arr[j+1])
           then {
             // swap temp and arr[i]
             temp = arr[j];
             arr[j] = arr[j+1];
             arr[j+1] = temp;
           } else ;
           j = j+1;
         };
       i = i+1;
     };
   }
   
   //Print the array
   func printArray(integer arr[], integer size) : void
   {
     var
     {
       integer n;
       integer i;
     }
     n = size;
     i = 0;
     while (i<n) {
       write(arr[i]);
         i = i+1;
     };
   }
   
   // main funtion to test above
   main
   {
     var
     {
       integer arr[7];
     }
     arr[0] = 64;
     arr[1] = 34;
     arr[2] = 25;
     arr[3] = 12;
     arr[4] = 22;
     arr[5] = 11;
     arr[6] = 90;
     printarray(arr, 7);
     bubbleSort(arr, 7);
     printarray(arr, 7);
   }
```
This compiler will generate the following AST:
```$xslt
Prog -> nonTerminal
├── ClassList -> nonTerminal
├── FuncDefList -> nonTerminal
│   ├── FuncDef -> nonTerminal
│   │   ├── scope -> bubbleSort
│   │   ├── FParamList -> nonTerminal
│   │   │   ├── FParam -> nonTerminal
│   │   │   │   ├── integer -> integer
│   │   │   │   ├── id -> arr
│   │   │   │   └── DimList -> nonTerminal
│   │   │   └── FParam -> nonTerminal
│   │   │       ├── integer -> integer
│   │   │       ├── id -> size
│   │   │       └── DimList -> nonTerminal
│   │   ├── ReturnType -> nonTerminal
│   │   │   └── void -> void
│   │   └── StatList -> nonTerminal
│   │       ├── VarDecl -> nonTerminal
│   │       │   ├── integer -> integer
│   │       │   ├── id -> n
│   │       │   └── DimList -> nonTerminal
│   │       ├── VarDecl -> nonTerminal
│   │       │   ├── integer -> integer
│   │       │   ├── id -> i
│   │       │   └── DimList -> nonTerminal
│   │       ├── VarDecl -> nonTerminal
│   │       │   ├── integer -> integer
│   │       │   ├── id -> j
│   │       │   └── DimList -> nonTerminal
│   │       ├── VarDecl -> nonTerminal
│   │       │   ├── integer -> integer
│   │       │   ├── id -> temp
│   │       │   └── DimList -> nonTerminal
│   │       ├── Assign -> nonTerminal
│   │       │   ├── id -> n
│   │       │   │   └── IndiceList -> nonTerminal
│   │       │   └── Expr -> nonTerminal
│   │       │       └── id -> size
│   │       ├── Assign -> nonTerminal
│   │       │   ├── id -> i
│   │       │   │   └── IndiceList -> nonTerminal
│   │       │   └── Expr -> nonTerminal
│   │       │       └── intnum -> 0
│   │       ├── Assign -> nonTerminal
│   │       │   ├── id -> j
│   │       │   │   └── IndiceList -> nonTerminal
│   │       │   └── Expr -> nonTerminal
│   │       │       └── intnum -> 0
│   │       ├── Assign -> nonTerminal
│   │       │   ├── id -> temp
│   │       │   │   └── IndiceList -> nonTerminal
│   │       │   └── Expr -> nonTerminal
│   │       │       └── intnum -> 0
│   │       └── WhileStat -> nonTerminal
│   │           ├── Expr -> nonTerminal
│   │           │   └── lt -> <
│   │           │       ├── id -> i
│   │           │       └── - -> -
│   │           │           ├── id -> n
│   │           │           └── intnum -> 1
│   │           └── StatBlock -> nonTerminal
│   │               ├── WhileStat -> nonTerminal
│   │               │   ├── Expr -> nonTerminal
│   │               │   │   └── lt -> <
│   │               │   │       ├── id -> j
│   │               │   │       └── - -> -
│   │               │   │           ├── - -> -
│   │               │   │           │   ├── id -> n
│   │               │   │           │   └── id -> i
│   │               │   │           └── intnum -> 1
│   │               │   └── StatBlock -> nonTerminal
│   │               │       ├── IfStat -> nonTerminal
│   │               │       │   ├── Expr -> nonTerminal
│   │               │       │   │   └── gt -> >
│   │               │       │   │       ├── id -> arr
│   │               │       │   │       │   └── IndiceList -> nonTerminal
│   │               │       │   │       │       └── Expr -> nonTerminal
│   │               │       │   │       │           └── id -> j
│   │               │       │   │       └── id -> arr
│   │               │       │   │           └── IndiceList -> nonTerminal
│   │               │       │   │               └── Expr -> nonTerminal
│   │               │       │   │                   └── + -> +
│   │               │       │   │                       ├── id -> j
│   │               │       │   │                       └── intnum -> 1
│   │               │       │   ├── ThenBLock -> nonTerminal
│   │               │       │   │   └── StatBlock -> nonTerminal
│   │               │       │   │       ├── Assign -> nonTerminal
│   │               │       │   │       │   ├── id -> temp
│   │               │       │   │       │   │   └── IndiceList -> nonTerminal
│   │               │       │   │       │   └── Expr -> nonTerminal
│   │               │       │   │       │       └── id -> arr
│   │               │       │   │       │           └── IndiceList -> nonTerminal
│   │               │       │   │       │               └── Expr -> nonTerminal
│   │               │       │   │       │                   └── id -> j
│   │               │       │   │       ├── Assign -> nonTerminal
│   │               │       │   │       │   ├── id -> arr
│   │               │       │   │       │   │   └── IndiceList -> nonTerminal
│   │               │       │   │       │   │       └── Expr -> nonTerminal
│   │               │       │   │       │   │           └── id -> j
│   │               │       │   │       │   └── Expr -> nonTerminal
│   │               │       │   │       │       └── id -> arr
│   │               │       │   │       │           └── IndiceList -> nonTerminal
│   │               │       │   │       │               └── Expr -> nonTerminal
│   │               │       │   │       │                   └── + -> +
│   │               │       │   │       │                       ├── id -> j
│   │               │       │   │       │                       └── intnum -> 1
│   │               │       │   │       └── Assign -> nonTerminal
│   │               │       │   │           ├── id -> arr
│   │               │       │   │           │   └── IndiceList -> nonTerminal
│   │               │       │   │           │       └── Expr -> nonTerminal
│   │               │       │   │           │           └── + -> +
│   │               │       │   │           │               ├── id -> j
│   │               │       │   │           │               └── intnum -> 1
│   │               │       │   │           └── Expr -> nonTerminal
│   │               │       │   │               └── id -> temp
│   │               │       │   └── ElseBlock -> nonTerminal
│   │               │       └── Assign -> nonTerminal
│   │               │           ├── id -> j
│   │               │           │   └── IndiceList -> nonTerminal
│   │               │           └── Expr -> nonTerminal
│   │               │               └── + -> +
│   │               │                   ├── id -> j
│   │               │                   └── intnum -> 1
│   │               └── Assign -> nonTerminal
│   │                   ├── id -> i
│   │                   │   └── IndiceList -> nonTerminal
│   │                   └── Expr -> nonTerminal
│   │                       └── + -> +
│   │                           ├── id -> i
│   │                           └── intnum -> 1
│   └── FuncDef -> nonTerminal
│       ├── scope -> printArray
│       ├── FParamList -> nonTerminal
│       │   ├── FParam -> nonTerminal
│       │   │   ├── integer -> integer
│       │   │   ├── id -> arr
│       │   │   └── DimList -> nonTerminal
│       │   └── FParam -> nonTerminal
│       │       ├── integer -> integer
│       │       ├── id -> size
│       │       └── DimList -> nonTerminal
│       ├── ReturnType -> nonTerminal
│       │   └── void -> void
│       └── StatList -> nonTerminal
│           ├── VarDecl -> nonTerminal
│           │   ├── integer -> integer
│           │   ├── id -> n
│           │   └── DimList -> nonTerminal
│           ├── VarDecl -> nonTerminal
│           │   ├── integer -> integer
│           │   ├── id -> i
│           │   └── DimList -> nonTerminal
│           ├── Assign -> nonTerminal
│           │   ├── id -> n
│           │   │   └── IndiceList -> nonTerminal
│           │   └── Expr -> nonTerminal
│           │       └── id -> size
│           ├── Assign -> nonTerminal
│           │   ├── id -> i
│           │   │   └── IndiceList -> nonTerminal
│           │   └── Expr -> nonTerminal
│           │       └── intnum -> 0
│           └── WhileStat -> nonTerminal
│               ├── Expr -> nonTerminal
│               │   └── lt -> <
│               │       ├── id -> i
│               │       └── id -> n
│               └── StatBlock -> nonTerminal
│                   ├── WriteStat -> nonTerminal
│                   │   └── Expr -> nonTerminal
│                   │       └── id -> arr
│                   │           └── IndiceList -> nonTerminal
│                   │               └── Expr -> nonTerminal
│                   │                   └── id -> i
│                   └── Assign -> nonTerminal
│                       ├── id -> i
│                       │   └── IndiceList -> nonTerminal
│                       └── Expr -> nonTerminal
│                           └── + -> +
│                               ├── id -> i
│                               └── intnum -> 1
└── FuncMain -> nonTerminal
    └── StatList -> nonTerminal
        ├── VarDecl -> nonTerminal
        │   ├── integer -> integer
        │   ├── id -> arr
        │   └── DimList -> nonTerminal
        │       └── intnum -> 7
        ├── Assign -> nonTerminal
        │   ├── id -> arr
        │   │   └── IndiceList -> nonTerminal
        │   │       └── Expr -> nonTerminal
        │   │           └── intnum -> 0
        │   └── Expr -> nonTerminal
        │       └── intnum -> 64
        ├── Assign -> nonTerminal
        │   ├── id -> arr
        │   │   └── IndiceList -> nonTerminal
        │   │       └── Expr -> nonTerminal
        │   │           └── intnum -> 1
        │   └── Expr -> nonTerminal
        │       └── intnum -> 34
        ├── Assign -> nonTerminal
        │   ├── id -> arr
        │   │   └── IndiceList -> nonTerminal
        │   │       └── Expr -> nonTerminal
        │   │           └── intnum -> 2
        │   └── Expr -> nonTerminal
        │       └── intnum -> 25
        ├── Assign -> nonTerminal
        │   ├── id -> arr
        │   │   └── IndiceList -> nonTerminal
        │   │       └── Expr -> nonTerminal
        │   │           └── intnum -> 3
        │   └── Expr -> nonTerminal
        │       └── intnum -> 12
        ├── Assign -> nonTerminal
        │   ├── id -> arr
        │   │   └── IndiceList -> nonTerminal
        │   │       └── Expr -> nonTerminal
        │   │           └── intnum -> 4
        │   └── Expr -> nonTerminal
        │       └── intnum -> 22
        ├── Assign -> nonTerminal
        │   ├── id -> arr
        │   │   └── IndiceList -> nonTerminal
        │   │       └── Expr -> nonTerminal
        │   │           └── intnum -> 5
        │   └── Expr -> nonTerminal
        │       └── intnum -> 11
        ├── Assign -> nonTerminal
        │   ├── id -> arr
        │   │   └── IndiceList -> nonTerminal
        │   │       └── Expr -> nonTerminal
        │   │           └── intnum -> 6
        │   └── Expr -> nonTerminal
        │       └── intnum -> 90
        ├── Func -> nonTerminal
        │   └── id -> printarray
        │       └── FParamList -> nonTerminal
        │           ├── Expr -> nonTerminal
        │           │   └── id -> arr
        │           └── Expr -> nonTerminal
        │               └── intnum -> 7
        ├── Func -> nonTerminal
        │   └── id -> bubbleSort
        │       └── FParamList -> nonTerminal
        │           ├── Expr -> nonTerminal
        │           │   └── id -> arr
        │           └── Expr -> nonTerminal
        │               └── intnum -> 7
        └── Func -> nonTerminal
            └── id -> printarray
                └── FParamList -> nonTerminal
                    ├── Expr -> nonTerminal
                    │   └── id -> arr
                    └── Expr -> nonTerminal
                        └── intnum -> 7
```

