lexer grammar IlliLexer;

// Literals

StringLiteral
    :   '"' StringCharacters? '"'
    ;
NumberLiteral
    :   DIGIT+ ( '.' DIGIT+ )?
    ;
BooleanLiteral
    :   'true'
    |   'false'
    ;
NilLiteral
    :   'nil'
    ;
Identifier
    :   ALPHA (DIGIT|ALPHA)*
    ;
fragment DIGIT
    :   [0-9]
    ;
fragment ALPHA
    :   [a-zA-Z_]
    ;
fragment StringCharacters
    :   StringCharacter+
    ;
fragment StringCharacter
    :	  ~["\\]
    |	  EscapeSequence
    ;
fragment EscapeSequence
    :   '\\' ["bfnrt\\]
    ;

// Whitespace and comments

WS
		:   [ \t\r\n]+		->	skip
    ;
Comment
    :   '/*' .*? '*/' ->  skip
    ;
LineComment
    :   '//' ~[\r\n]* ->  skip
    ;

// Keywords

AND:    'and';
CLASS:  'class';
ELSE:   'else';
FUN:    'fun';
FOR:    'for';
IF:     'if';
OR:     'or';
PRINT:  'print';
RETURN: 'return';
SUPER:  'super';
THIS:   'this';
VAR:    'var';
WHILE:  'while';

// Separators

LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
SEMI : ';';
COMMA : ',';
DOT : '.';

// Operators

ASSIGN : '=';
GT : '>';
LT : '<';
BANG : '!';
QUESTION : '?';
COLON : ':';
EQUAL : '==';
LE : '<=';
GE : '>=';
NOTEQUAL : '!=';
ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
