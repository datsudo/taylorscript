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

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
