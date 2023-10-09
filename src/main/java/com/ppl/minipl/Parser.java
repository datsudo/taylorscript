package com.ppl.minipl;

import java.util.ArrayList;
import java.util.List;

import static com.ppl.minipl.TokenType.*;

public class Parser {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int lineNumber = 1;

    Parser(String source) {
        this.source = source;
    }

    List<Token> parseTokens() {
        while (!isAtEnd()) {
            start = current;
            parseToken();
        }

        tokens.add(new Token(EOF, "", null, lineNumber));
        return tokens;
    }

    private void parseToken() {
        // parse individual token
        char c = advance();
        switch (c) {
            case '(': addToken(LPAREN   ); break;
            case ')': addToken(RPAREN   ); break;
            case '{': addToken(LBRACE   ); break;
            case '}': addToken(RBRACE   ); break;
            case '-': addToken(MINUS    ); break;
            case '+': addToken(PLUS     ); break;
            case '*': addToken(STAR     ); break;
            case ',': addToken(COMMA    ); break;
            case '.': addToken(DOT      ); break;
            case ';': addToken(SEMICOLON); break;

            default:
                PL.error(lineNumber, "Unexpected character.");
                break;
        }
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        // this overload method is for tokens with literal values
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, lineNumber));
    }
}
