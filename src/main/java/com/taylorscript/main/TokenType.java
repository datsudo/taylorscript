package com.taylorscript.main;

enum TokenType {
    // single-char tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    LEFT_BRACKET, RIGHT_BRACKET,
    MINUS, PLUS, SLASH, STAR, PCENT,
    COMMA, DOT, SEMICOLON, COLON,

    // one-two char tokens
    LOGICAL_NOT, NOT_EQUAL,
    ASSIGN_EQUAL, COMP_EQUAL,
    GREATER_THAN, GREATER_THAN_EQ,
    LESS_THAN, LESS_THAN_EQ,
    LOGICAL_AND, LOGICAL_OR,
    PLUS_EQ, MINUS_EQ, START_EQ, SLASH_EQ,

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