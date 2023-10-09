package com.ppl.minipl;

import java.util.ArrayList;
import java.util.List;

import static com.ppl.minipl.TokenType.*;

public class Parser {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    Parser(String source) {
        this.source = source;
    }
}
