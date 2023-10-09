package com.ppl.minipl;

enum TokenType {
    // single-char tokens
    LPAREN, RPAREN, LBRACE, RBRACE,
    MINUS, PLUS, SLASH, STAR, PCENT,
    COMMA, DOT, SEMICOLON,

    // one-two char tokens
    BANG, BEQ,
    EQUAL, EEQ,
    GTHAN, GEQ,
    LTHAN, LEQ,

    // literals
    IDENT, STRING, NUMBER,

    // keywords
    PAKI, PO,                   // honorifics
    CLASS, FUNC, ASSIGN,        // abstraction
    IF, ELSE, FOR, WHILE,       // control
    AND, OR, NIL, TRUE, FALSE,  // logic
    PRINT, RETURN, SUPER, THIS,

    EOF
}