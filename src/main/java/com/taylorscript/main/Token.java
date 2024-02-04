package com.taylorscript.main;

class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int lineNumber;
    final int colNumber;

    Token(TokenType type, String lexeme, Object literal, int lineNumber, int colNumber) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
