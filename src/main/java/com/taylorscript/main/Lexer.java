package com.taylorscript.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.taylorscript.main.TokenType.*;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int lineNumber = 1;
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("Define", FUNC);
        keywords.put("Mine", INPUT);
        keywords.put("When", IF);
        keywords.put("Thats", ELSE);
        keywords.put("ThatsWhen", ELIF);
        keywords.put("AllTooWhile", LOOP);
        keywords.put("BlankSpace", NIL);
        keywords.put("The1", TRUE);
        keywords.put("The0", FALSE);
        keywords.put("SpeakNow", PRINT);
        keywords.put("BackToDecember", RETURN);
        keywords.put("Evermore", CONTINUE);
        keywords.put("Clear", CLEAR);
        keywords.put("Dear", SWITCH);
        keywords.put("John", CASE);
        keywords.put("Closure", DEFAULT);
        keywords.put("Getaway", TRY);
        keywords.put("Car", CATCH);
    }

    Lexer(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, lineNumber));
        return tokens;
    }

    private void scanToken() {
        // parse individual token
        char c = advance();
        switch (c) {
            case '(': addToken(LPAREN   ); break;
            case ')': addToken(RPAREN   ); break;
            case '{': addToken(LBRACE   ); break;
            case '}': addToken(RBRACE   ); break;
            case '[': addToken(LBRACKET ); break;
            case ']': addToken(RBRACKET ); break;
            case '+': addToken(PLUS     ); break;
            case '*': addToken(STAR     ); break;
            case ',': addToken(COMMA    ); break;
            case '.': addToken(DOT      ); break;
            case ';': addToken(SEMICOLON); break;

            case '!': addToken(matchNextChar('=') ? BEQ : BANG ); break;
            case '=': addToken(matchNextChar('=') ? EEQ : EQUAL); break;
            case '<': addToken(matchNextChar('=') ? LEQ : LTHAN); break;
            case '>': addToken(matchNextChar('=') ? GEQ : GTHAN); break;

            case '/':
                if (matchNextChar('/')) {
                    // check if next char is '/'; '//' indicates start of comment
                    // just continue advancing until end of line
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(SLASH);
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                lineNumber++;
                break;

            case '"': string(); break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    TaylorScript.error(lineNumber, "Unexpected character.");
                }
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) {
            type = IDENT;
        }
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) {
                advance();
            }
        }

        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                lineNumber++;
            }
            advance();
        }

        if (isAtEnd()) {
            TaylorScript.error(lineNumber, "Unterminated string.");
            return;
        }
        advance();

        // get the string except the surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean matchNextChar(char expected) {
        // this method peeks and advance at the same time
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        // this method peeks and advance at the same time
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
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