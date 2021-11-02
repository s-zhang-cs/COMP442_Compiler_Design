# COMPILER FRONTEND

Part of the course work for COMP442 (Compiler Design) taught by professor Joey Paquet at Concordia University for Winter2021.
Includes implementation of the compiler frontend (lexer, parser and AST generation) from scratch for a fictional programming language called 'Moon'. The following diagram shows the program architecture. Only the conceptually important attributes and methods are shown.

<img src="./resources/doc/Untitled Diagram.drawio.png">

Since it is my first time implementing a compiler. The program results from a lot of trial and error and bears its
cost. I only uploaded the frontend part because I believe it is now rather 'clean' after continuous improvement. 
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
```$xslt
// ====== Class declarations ====== //
class POLYNOMIAL {
	public func evaluate(float x) : float;
};

class LINEAR inherits POLYNOMIAL {
	private float a;
	private float b;

	public func build(float A, float B) : LINEAR;
	public func evaluate(float x) : float;
};

class QUADRATIC inherits POLYNOMIAL {
	private float a;
	private float b;
	private float c;

	public func build(float A, float B, float C) : QUADRATIC;
	public func evaluate(float x) : float;
};

// ====== Function Definitions ====== //
func POLYNOMIAL::evaluate(float x) : float
{
  return (0);
}

func LINEAR::evaluate(float x) : float
{
  var
  {
    float result;
  }
  result = 0.0;
  result = a * x + b;
  return (result);
}

func QUADRATIC::evaluate(float x) : float
{
  var
  {
    float result;
  }
  //Using Horner's method
  result = a;
  result = result * x + b;
  result = result * x + c;
  return (result);
}

func LINEAR::build(float A, float B) : LINEAR
{
  var
  {
    LINEAR new_function;
  }
  new_function.a = A;
  new_function.b = B;
  return (new_function);
}

func QUADRATIC::build(float A, float B, float C) : QUADRATIC
{
  var
  {
    QUADRATIC new_function;
  }
  new_function.a = A;
  new_function.b = B;
  new_function.c = C;
  return (new_function);
}


// ====== main ====== //
main
{
  var
  {
    linear f1;
    quadratic f2;
    integer counter;
  }
  f1 = f1.build(2, 3.5);
  f2 = f2.build(-2.0, 1.0, 0.0);
  counter = 1;

  while(counter <= 10)
  {
    write(counter);
    write(f1.evaluate(counter));
    write(f2.evaluate(counter));
  };
}
```
This compiler will generate the following AST:
```$xslt
Prog -> nonTerminal
├── ClassList -> nonTerminal
│   ├── ClassDecl -> nonTerminal
│   │   ├── id -> POLYNOMIAL
│   │   ├── InherList -> nonTerminal
│   │   └── MembDecl -> nonTerminal
│   │       ├── public -> public
│   │       └── FuncDecl -> nonTerminal
│   │           ├── id -> evaluate
│   │           ├── FParamList -> nonTerminal
│   │           │   └── FParam -> nonTerminal
│   │           │       ├── float -> float
│   │           │       ├── id -> x
│   │           │       └── DimList -> nonTerminal
│   │           └── ReturnType -> nonTerminal
│   │               └── float -> float
│   ├── ClassDecl -> nonTerminal
│   │   ├── id -> LINEAR
│   │   ├── InherList -> nonTerminal
│   │   │   └── id -> POLYNOMIAL
│   │   ├── MembDecl -> nonTerminal
│   │   │   ├── private -> private
│   │   │   └── VarDecl -> nonTerminal
│   │   │       ├── float -> float
│   │   │       ├── id -> a
│   │   │       └── DimList -> nonTerminal
│   │   ├── MembDecl -> nonTerminal
│   │   │   ├── private -> private
│   │   │   └── VarDecl -> nonTerminal
│   │   │       ├── float -> float
│   │   │       ├── id -> b
│   │   │       └── DimList -> nonTerminal
│   │   ├── MembDecl -> nonTerminal
│   │   │   ├── public -> public
│   │   │   └── FuncDecl -> nonTerminal
│   │   │       ├── id -> build
│   │   │       ├── FParamList -> nonTerminal
│   │   │       │   ├── FParam -> nonTerminal
│   │   │       │   │   ├── float -> float
│   │   │       │   │   ├── id -> A
│   │   │       │   │   └── DimList -> nonTerminal
│   │   │       │   └── FParam -> nonTerminal
│   │   │       │       ├── float -> float
│   │   │       │       ├── id -> B
│   │   │       │       └── DimList -> nonTerminal
│   │   │       └── ReturnType -> nonTerminal
│   │   │           └── id -> LINEAR
│   │   └── MembDecl -> nonTerminal
│   │       ├── public -> public
│   │       └── FuncDecl -> nonTerminal
│   │           ├── id -> evaluate
│   │           ├── FParamList -> nonTerminal
│   │           │   └── FParam -> nonTerminal
│   │           │       ├── float -> float
│   │           │       ├── id -> x
│   │           │       └── DimList -> nonTerminal
│   │           └── ReturnType -> nonTerminal
│   │               └── float -> float
│   └── ClassDecl -> nonTerminal
│       ├── id -> QUADRATIC
│       ├── InherList -> nonTerminal
│       │   └── id -> POLYNOMIAL
│       ├── MembDecl -> nonTerminal
│       │   ├── private -> private
│       │   └── VarDecl -> nonTerminal
│       │       ├── float -> float
│       │       ├── id -> a
│       │       └── DimList -> nonTerminal
│       ├── MembDecl -> nonTerminal
│       │   ├── private -> private
│       │   └── VarDecl -> nonTerminal
│       │       ├── float -> float
│       │       ├── id -> b
│       │       └── DimList -> nonTerminal
│       ├── MembDecl -> nonTerminal
│       │   ├── private -> private
│       │   └── VarDecl -> nonTerminal
│       │       ├── float -> float
│       │       ├── id -> c
│       │       └── DimList -> nonTerminal
│       ├── MembDecl -> nonTerminal
│       │   ├── public -> public
│       │   └── FuncDecl -> nonTerminal
│       │       ├── id -> build
│       │       ├── FParamList -> nonTerminal
│       │       │   ├── FParam -> nonTerminal
│       │       │   │   ├── float -> float
│       │       │   │   ├── id -> A
│       │       │   │   └── DimList -> nonTerminal
│       │       │   ├── FParam -> nonTerminal
│       │       │   │   ├── float -> float
│       │       │   │   ├── id -> B
│       │       │   │   └── DimList -> nonTerminal
│       │       │   └── FParam -> nonTerminal
│       │       │       ├── float -> float
│       │       │       ├── id -> C
│       │       │       └── DimList -> nonTerminal
│       │       └── ReturnType -> nonTerminal
│       │           └── id -> QUADRATIC
│       └── MembDecl -> nonTerminal
│           ├── public -> public
│           └── FuncDecl -> nonTerminal
│               ├── id -> evaluate
│               ├── FParamList -> nonTerminal
│               │   └── FParam -> nonTerminal
│               │       ├── float -> float
│               │       ├── id -> x
│               │       └── DimList -> nonTerminal
│               └── ReturnType -> nonTerminal
│                   └── float -> float
├── FuncDefList -> nonTerminal
│   ├── FuncDef -> nonTerminal
│   │   ├── scope -> POLYNOMIAL
│   │   ├── id -> evaluate
│   │   ├── FParamList -> nonTerminal
│   │   │   └── FParam -> nonTerminal
│   │   │       ├── float -> float
│   │   │       ├── id -> x
│   │   │       └── DimList -> nonTerminal
│   │   ├── ReturnType -> nonTerminal
│   │   │   └── float -> float
│   │   └── StatList -> nonTerminal
│   │       └── ReturnStat -> nonTerminal
│   │           └── ReturnStat -> nonTerminal
│   │               └── Expr -> nonTerminal
│   │                   └── intnum -> 0
│   ├── FuncDef -> nonTerminal
│   │   ├── scope -> LINEAR
│   │   ├── id -> evaluate
│   │   ├── FParamList -> nonTerminal
│   │   │   └── FParam -> nonTerminal
│   │   │       ├── float -> float
│   │   │       ├── id -> x
│   │   │       └── DimList -> nonTerminal
│   │   ├── ReturnType -> nonTerminal
│   │   │   └── float -> float
│   │   └── StatList -> nonTerminal
│   │       ├── VarDecl -> nonTerminal
│   │       │   ├── float -> float
│   │       │   ├── id -> result
│   │       │   └── DimList -> nonTerminal
│   │       ├── Assign -> nonTerminal
│   │       │   ├── id -> result
│   │       │   │   └── IndiceList -> nonTerminal
│   │       │   └── Expr -> nonTerminal
│   │       │       └── floatnum -> 0.0
│   │       ├── Assign -> nonTerminal
│   │       │   ├── id -> result
│   │       │   │   └── IndiceList -> nonTerminal
│   │       │   └── Expr -> nonTerminal
│   │       │       └── + -> +
│   │       │           ├── * -> *
│   │       │           │   ├── id -> a
│   │       │           │   └── id -> x
│   │       │           └── id -> b
│   │       └── ReturnStat -> nonTerminal
│   │           └── ReturnStat -> nonTerminal
│   │               └── Expr -> nonTerminal
│   │                   └── id -> result
│   ├── FuncDef -> nonTerminal
│   │   ├── scope -> QUADRATIC
│   │   ├── id -> evaluate
│   │   ├── FParamList -> nonTerminal
│   │   │   └── FParam -> nonTerminal
│   │   │       ├── float -> float
│   │   │       ├── id -> x
│   │   │       └── DimList -> nonTerminal
│   │   ├── ReturnType -> nonTerminal
│   │   │   └── float -> float
│   │   └── StatList -> nonTerminal
│   │       ├── VarDecl -> nonTerminal
│   │       │   ├── float -> float
│   │       │   ├── id -> result
│   │       │   └── DimList -> nonTerminal
│   │       ├── Assign -> nonTerminal
│   │       │   ├── id -> result
│   │       │   │   └── IndiceList -> nonTerminal
│   │       │   └── Expr -> nonTerminal
│   │       │       └── id -> a
│   │       ├── Assign -> nonTerminal
│   │       │   ├── id -> result
│   │       │   │   └── IndiceList -> nonTerminal
│   │       │   └── Expr -> nonTerminal
│   │       │       └── + -> +
│   │       │           ├── * -> *
│   │       │           │   ├── id -> result
│   │       │           │   └── id -> x
│   │       │           └── id -> b
│   │       ├── Assign -> nonTerminal
│   │       │   ├── id -> result
│   │       │   │   └── IndiceList -> nonTerminal
│   │       │   └── Expr -> nonTerminal
│   │       │       └── + -> +
│   │       │           ├── * -> *
│   │       │           │   ├── id -> result
│   │       │           │   └── id -> x
│   │       │           └── id -> c
│   │       └── ReturnStat -> nonTerminal
│   │           └── ReturnStat -> nonTerminal
│   │               └── Expr -> nonTerminal
│   │                   └── id -> result
│   ├── FuncDef -> nonTerminal
│   │   ├── scope -> LINEAR
│   │   ├── id -> build
│   │   ├── FParamList -> nonTerminal
│   │   │   ├── FParam -> nonTerminal
│   │   │   │   ├── float -> float
│   │   │   │   ├── id -> A
│   │   │   │   └── DimList -> nonTerminal
│   │   │   └── FParam -> nonTerminal
│   │   │       ├── float -> float
│   │   │       ├── id -> B
│   │   │       └── DimList -> nonTerminal
│   │   ├── ReturnType -> nonTerminal
│   │   │   └── id -> LINEAR
│   │   └── StatList -> nonTerminal
│   │       ├── VarDecl -> nonTerminal
│   │       │   ├── id -> LINEAR
│   │       │   ├── id -> new_function
│   │       │   └── DimList -> nonTerminal
│   │       ├── Assign -> nonTerminal
│   │       │   ├── Dot -> nonTerminal
│   │       │   │   ├── id -> new_function
│   │       │   │   │   └── IndiceList -> nonTerminal
│   │       │   │   └── id -> a
│   │       │   │       └── IndiceList -> nonTerminal
│   │       │   └── Expr -> nonTerminal
│   │       │       └── id -> A
│   │       ├── Assign -> nonTerminal
│   │       │   ├── Dot -> nonTerminal
│   │       │   │   ├── id -> new_function
│   │       │   │   │   └── IndiceList -> nonTerminal
│   │       │   │   └── id -> b
│   │       │   │       └── IndiceList -> nonTerminal
│   │       │   └── Expr -> nonTerminal
│   │       │       └── id -> B
│   │       └── ReturnStat -> nonTerminal
│   │           └── ReturnStat -> nonTerminal
│   │               └── Expr -> nonTerminal
│   │                   └── id -> new_function
│   └── FuncDef -> nonTerminal
│       ├── scope -> QUADRATIC
│       ├── id -> build
│       ├── FParamList -> nonTerminal
│       │   ├── FParam -> nonTerminal
│       │   │   ├── float -> float
│       │   │   ├── id -> A
│       │   │   └── DimList -> nonTerminal
│       │   ├── FParam -> nonTerminal
│       │   │   ├── float -> float
│       │   │   ├── id -> B
│       │   │   └── DimList -> nonTerminal
│       │   └── FParam -> nonTerminal
│       │       ├── float -> float
│       │       ├── id -> C
│       │       └── DimList -> nonTerminal
│       ├── ReturnType -> nonTerminal
│       │   └── id -> QUADRATIC
│       └── StatList -> nonTerminal
│           ├── VarDecl -> nonTerminal
│           │   ├── id -> QUADRATIC
│           │   ├── id -> new_function
│           │   └── DimList -> nonTerminal
│           ├── Assign -> nonTerminal
│           │   ├── Dot -> nonTerminal
│           │   │   ├── id -> new_function
│           │   │   │   └── IndiceList -> nonTerminal
│           │   │   └── id -> a
│           │   │       └── IndiceList -> nonTerminal
│           │   └── Expr -> nonTerminal
│           │       └── id -> A
│           ├── Assign -> nonTerminal
│           │   ├── Dot -> nonTerminal
│           │   │   ├── id -> new_function
│           │   │   │   └── IndiceList -> nonTerminal
│           │   │   └── id -> b
│           │   │       └── IndiceList -> nonTerminal
│           │   └── Expr -> nonTerminal
│           │       └── id -> B
│           ├── Assign -> nonTerminal
│           │   ├── Dot -> nonTerminal
│           │   │   ├── id -> new_function
│           │   │   │   └── IndiceList -> nonTerminal
│           │   │   └── id -> c
│           │   │       └── IndiceList -> nonTerminal
│           │   └── Expr -> nonTerminal
│           │       └── id -> C
│           └── ReturnStat -> nonTerminal
│               └── ReturnStat -> nonTerminal
│                   └── Expr -> nonTerminal
│                       └── id -> new_function
└── FuncMain -> nonTerminal
    └── StatList -> nonTerminal
        ├── VarDecl -> nonTerminal
        │   ├── id -> linear
        │   ├── id -> f1
        │   └── DimList -> nonTerminal
        ├── VarDecl -> nonTerminal
        │   ├── id -> quadratic
        │   ├── id -> f2
        │   └── DimList -> nonTerminal
        ├── VarDecl -> nonTerminal
        │   ├── integer -> integer
        │   ├── id -> counter
        │   └── DimList -> nonTerminal
        ├── Assign -> nonTerminal
        │   ├── id -> f1
        │   │   └── IndiceList -> nonTerminal
        │   └── Expr -> nonTerminal
        │       └── Dot -> nonTerminal
        │           ├── id -> f1
        │           │   └── IndiceList -> nonTerminal
        │           └── id -> build
        │               └── FParamList -> nonTerminal
        │                   ├── Expr -> nonTerminal
        │                   │   └── intnum -> 2
        │                   └── Expr -> nonTerminal
        │                       └── floatnum -> 3.5
        ├── Assign -> nonTerminal
        │   ├── id -> f2
        │   │   └── IndiceList -> nonTerminal
        │   └── Expr -> nonTerminal
        │       └── Dot -> nonTerminal
        │           ├── id -> f2
        │           │   └── IndiceList -> nonTerminal
        │           └── id -> build
        │               └── FParamList -> nonTerminal
        │                   ├── Expr -> nonTerminal
        │                   │   └── - -> -
        │                   │       └── floatnum -> 2.0
        │                   ├── Expr -> nonTerminal
        │                   │   └── floatnum -> 1.0
        │                   └── Expr -> nonTerminal
        │                       └── floatnum -> 0.0
        ├── Assign -> nonTerminal
        │   ├── id -> counter
        │   │   └── IndiceList -> nonTerminal
        │   └── Expr -> nonTerminal
        │       └── intnum -> 1
        └── WhileStat -> nonTerminal
            ├── Expr -> nonTerminal
            │   └── leq -> <=
            │       ├── id -> counter
            │       └── intnum -> 10
            └── StatBlock -> nonTerminal
                ├── WriteStat -> nonTerminal
                │   └── Expr -> nonTerminal
                │       └── id -> counter
                ├── WriteStat -> nonTerminal
                │   └── Expr -> nonTerminal
                │       └── Dot -> nonTerminal
                │           ├── id -> f1
                │           │   └── IndiceList -> nonTerminal
                │           └── id -> evaluate
                │               └── FParamList -> nonTerminal
                │                   └── Expr -> nonTerminal
                │                       └── id -> counter
                └── WriteStat -> nonTerminal
                    └── Expr -> nonTerminal
                        └── Dot -> nonTerminal
                            ├── id -> f2
                            │   └── IndiceList -> nonTerminal
                            └── id -> evaluate
                                └── FParamList -> nonTerminal
                                    └── Expr -> nonTerminal
                                        └── id -> counter
```

