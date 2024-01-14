package com.taylorscript.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.taylorscript.main.TokenType.*;
import static com.taylorscript.main.TokenType.VAR;

public class Lexer {
    private String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int lineNumber = 1;
    private int prevStart;
    private int prevCurrent;
    private int prevLineNumber;
    private String prevSource;
    private static final Map<String, TokenType> keywords;
    private boolean isInTailorCall = false;

    static {
        keywords = new HashMap<>();
        keywords.put("Define", FUNC);
        keywords.put("Mine", INPUT);
        keywords.put("When", IF);
        keywords.put("Thats", ELSE);
        keywords.put("Then", ELIF);
        keywords.put("AllTooWhile", LOOP);
        keywords.put("BlankSpace", NIL);
        keywords.put("The1", TRUE);
        keywords.put("The0", FALSE);
        keywords.put("SpeakNow", PRINT);
        keywords.put("BackTo", RETURN);
        keywords.put("Evermore", CONTINUE);
        keywords.put("Clean", CLEAR);
        keywords.put("Dear", SWITCH);
        keywords.put("John", CASE);
        keywords.put("Closure", DEFAULT);
        keywords.put("Let", VAR);
        keywords.put("Fear", TRY);
        keywords.put("Less", CATCH);
    }

    Lexer(String source) {
        this.source = source;
    }

    List<Token> scanTokens() throws IOException {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, lineNumber));
        return tokens;
    }

    private void scanToken() throws IOException {
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
            case '/': addToken(SLASH    ); break;
            case ',': addToken(COMMA    ); break;
            case '.': addToken(DOT      ); break;
            case ';': addToken(SEMICOLON); break;

            case '!': addToken(matchNextChar('=') ? BEQ : BANG ); break;
            case '=': addToken(matchNextChar('=') ? EEQ : EQUAL); break;
            case '<': addToken(matchNextChar('=') ? LEQ : LTHAN); break;
            case '>': addToken(matchNextChar('=') ? GEQ : GTHAN); break;
            case '&':
                if (matchNextChar('&')) addToken(AND);
                break;
            case '|':
                if (matchNextChar('|')) addToken(OR);
                break;

            case '-':
                if (matchNextChar('-'))
                    // check if next char is '-'; '--' indicates start of comment
                    while (peek() != '\n' && !isAtEnd()) advance();
                else if (matchNextChar('*')) {
                    // '-*' starts a multiline comment and ends with '*-'
                    boolean isInComment = true;
                    while (isInComment) {
                        if (peek() == '\n') this.lineNumber++;
                        if (peek() == '*' && peekNext() == '-') {
                            advance(); isInComment = false;
                        }
                        if (peek() == '\0') {
                            TaylorScript.error(lineNumber, "Unterminated multiline comment");
                            break;
                        }
                        advance();
                    }
                } else addToken(MINUS);
                break;

            case '*': addToken(STAR); break;

            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                lineNumber++;
                break;

            case '"': string(); break;

            default:
                if (isDigit(c)) number();
                else if (isTailorCall()) {
                    isInTailorCall = true;
                    advance();
                }
                else if (isAlpha(c)) identifier();
                else TaylorScript.error(lineNumber, "Unexpected character.");
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

    private boolean isTailorCall() {
        if (source.charAt(current - 1) != 'T') return false;
        int t = 1;
        while (t < 6) {
            if (peek() != "Tailor".charAt(t)) return false;
            advance();
            t++;
        }
        if (isAtEnd() || (peekNext() != '"' && source.charAt(current) != '[')) {
            TaylorScript.error(lineNumber, "Tailor keyword cannot be used as identifier.");
        }
        return true;
    }

    private void parseTailorFile(String tailorFilePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(tailorFilePath));
        prevSource = source;
        prevStart = start;
        prevCurrent = current;
        prevLineNumber = lineNumber;
        source = new String(bytes, Charset.defaultCharset());
        start = current = 0;
        lineNumber = 1;
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
    }

    private void string() throws IOException {
        consumeString();
        if (isAtEnd()) {
            TaylorScript.error(lineNumber, "Unterminated string.");
            return;
        }
        advance();

        // get the string except the surrounding quotes
        String value = source.substring(start + 1, current - 1);
        if (isInTailorCall) {
            isInTailorCall = false;
            advance();

            if (!new File(value + ".lor").isFile()) {
                TaylorScript.error(lineNumber, "Fragment code doesn't exist.");
                return;
            }

            parseTailorFile(value + ".lor");
            revertState();
        } else {
            addToken(STRING, value);
        }
    }

    private void revertState() {
        source = prevSource;
        start = prevStart;
        current = prevCurrent;
        lineNumber = prevLineNumber;
    }

    private void consumeString() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                lineNumber++;
            }
            advance();
        }
    }

    private boolean matchNextChar(char expected) {
        // this method peeks and advance at the same time
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        // like advance() but doesn't consume character
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