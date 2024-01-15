package com.taylorscript.main;

enum TokenType {
    // single-char tokens
    LPAREN, RPAREN, LBRACE, RBRACE,
    LBRACKET, RBRACKET,
    MINUS, PLUS, SLASH, STAR, PCENT,
    COMMA, DOT, SEMICOLON,

    // one-two char tokens
    BANG, BEQ,
    EQUAL, EEQ,
    GTHAN, GEQ,
    LTHAN, LEQ,
    AND, OR,

    // literals
    IDENT, STRING, NUMBER,

    // keywords
    FUNC,  // abstraction
    NIL, TRUE, FALSE,  // logic

    // noise
    NUMSTYLE, STRSTYLE, BOOLSTYLE,

    LOOP, CONTINUE,  // combined for-while loop
    IF, ELSE, ELIF,  // control
    TRY, CATCH,  // exception handling
    SWITCH, CASE, DEFAULT,  // switch-case
    PRINT, BREAK, RETURN, CLEAR,  // etc
    VAR,
    INPUT,

    EOF
}