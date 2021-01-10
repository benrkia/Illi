parser grammar IlliParser;

options {
  tokenVocab = IlliLexer;
}

source
    :   declaration* EOF
    ;

//
// Statements
//
declaration
    :   varDeclaration
    |   statement
    ;
varDeclaration
    :   VAR Identifier (ASSIGN expression)? ';'
    ;
statement
    :   block
    |   printStmt
    |   exprStmt
    ;
exprStmt
    :   expression ';'
    ;
printStmt
    :   PRINT expression ';'
    ;
block
    :   LBRACE declaration RBRACE
    ;

//
// Expressions
//

expression
    :   comma
    ;
comma
    :   assignment ( COMMA assignment )*
    ;
assignment
    :   Identifier ASSIGN assignment
    |   conditional
    ;
conditional
    :   equality (QUESTION expression COLON conditional)?
    ;
equality
    :   comparison ( ( NOTEQUAL | EQUAL ) comparison )*
    ;
comparison
    :   term ( ( GE | GT | LE | LT ) term )*
    ;
term
    :   factor ( ( ADD | SUB ) factor )*
    ;
factor
    :   unary ( ( MUL | DIV ) unary )*
    ;
unary
    :   ( BANG | SUB ) unary
    |   primary
    ;
primary
    :   grouping
    |   StringLiteral
    |   NumberLiteral
    |   BooleanLiteral
    |   NilLiteral
    |   Identifier
    |   erroneousBinary
    ;
grouping
    :   LPAREN expression RPAREN
    ;
erroneousBinary // Helps in providing a better error reporting for binary expressions
    :   ( NOTEQUAL | EQUAL ) equality
    |   ( GE | GT | LE | LT ) comparison
    |   ( ADD ) term
    |   ( MUL | DIV ) factor
    ;
