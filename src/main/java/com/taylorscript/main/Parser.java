package com.taylorscript.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.taylorscript.main.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Expr expression() {
        return assignment();
    }

    private Statement declaration() {
        try {
            if (match(FUNC)) return function();
            if (match(VAR)) return varDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Statement statement() {
        if (match(IF)) return ifStatement();
        if (match(PRINT)) return printStatement();
        if (match(RETURN)) return returnStatement();
        if (match(LOOP)) return loopStatement();
        if (match(LEFT_BRACKET)) return new Statement.Block(block());

        return expressionStatement();
    }

    private Statement ifStatement() {
        consume(LEFT_BRACKET, "Expect '[' after 'When'.");
        Expr condition = expression();
        consume(RIGHT_BRACKET, "Expect ']' after 'When' condition.");

        Statement thenBranch = statement();
        Statement elseBranch = null;

        if (match(ELIF)) {
            return elifStatement();
        } else if (match(ELSE)) {
            elseBranch = statement();
        }

        return new Statement.If(condition, thenBranch, elseBranch);
    }

    private Statement elifStatement() {
        consume(LEFT_BRACKET, "Expect '[' after 'Then'.");
        Expr condition = expression();
        consume(RIGHT_BRACKET, "Expect ']' after 'Then' condition.");

        Statement thenBranch = statement();
        Statement elseBranch = null;

        if (match(ELIF)) {
            return elifStatement();
        } else if (match(ELSE)) {
            elseBranch = statement();
        }

        return new Statement.If(condition, thenBranch, elseBranch);
    }

    private Statement printStatement() {
        consume(LEFT_BRACKET, "Expect '[' before expression.");
        Expr value = expression();
        consume(RIGHT_BRACKET, "Expect ']' after expression.");
        consume(SEMICOLON, "Expect ';' after value.");

        return new Statement.Print(value);
    }

    private Statement returnStatement() {
        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON)) {
            value = expression();
        }
        consume(SEMICOLON, "Expect ';' after return value.");
        return new Statement.Return(keyword, value);
    }

    private Statement varDeclaration() {
        Token name = consume(IDENT, "Expect variable name.");

        Expr initializer = null;
        if (match(ASSIGN_EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Statement.Let(name, initializer);
    }

    private Statement loopStatement() {
        consume(LEFT_BRACKET, "Expect '[' after 'AllTooWhile'.");

        if (check(SEMICOLON)) {
            advance();
            return forStatement(null);
        } else if (check(VAR)) {
            advance();
            return forStatement(varDeclaration());
        } else {
            return whileStatement();
        }

    }

    private Statement whileStatement() {
        Expr condition = expression();
        consume(RIGHT_BRACKET, "Expect ']' after condition.");
        Statement body = statement();
        return new Statement.While(condition, body);
    }

    private Statement forStatement(Statement initializer) {
        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");

        Expr increment = null;
        if (!check(RIGHT_BRACKET)) increment = expression();
        consume(RIGHT_BRACKET, "Expect ']' after loop header.");

        Statement body = statement();

        if (increment != null) {
            body = new Statement.Block(Arrays.asList(body, new Statement.Expression(increment)));
        }

        if (condition == null) condition = new Expr.Literal(true);
        body = new Statement.While(condition, body);

        if (initializer != null) {
            body = new Statement.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    private Statement expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Statement.Expression(expr);
    }

    private Statement.Function function() {
        Token name = consume(IDENT, "Expect function name.");

        consume(LEFT_BRACKET, "Expect '[' after function name.");
        List<Token> params = new ArrayList<>();
        if (!check(RIGHT_BRACKET)) {
            do {
                if (params.size() >= 255) {
                    error(peek(), "The number of arguments exceeded the maximum limit (255).");
                }

                params.add(consume(IDENT, "Expect parameter name."));
            } while (match(COMMA));
        }

        consume(RIGHT_BRACKET, "Expect ']' after paremeters.");
        
        consume(LEFT_BRACKET, "Expect '[' before function body.");
        List<Statement> body = block();
        return new Statement.Function(name, params, body);
    }

    private List<Statement> block() {
        List<Statement> statements = new ArrayList<>();

        while (!check(RIGHT_BRACKET) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACKET, "Expect ']' after block.");
        return statements;
    }

    private Expr assignment() {
        Expr expr = or();

        if (match(ASSIGN_EQUAL, PLUS_EQ, MINUS_EQ, STAR_EQ, SLASH_EQ)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value, equals);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or() {
        Expr expr = and();

        while (match(LOGICAL_OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr and() {
        Expr expr = equality();

        while (match(LOGICAL_AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        // equality -> comparison ( ( "!=" | "==" ) comparison )*
        Expr expr = comparison();

        while (match(NOT_EQUAL, COMP_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        // comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )*
        Expr expr = term();

        while (match(GREATER_THAN, GREATER_THAN_EQ, LESS_THAN, LESS_THAN_EQ)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = exponent();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = exponent();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr exponent() {
        Expr expr = unary();
        if (match(CARET)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        // unary -> ( "!" | "-" ) unary | primary
        if (match(LOGICAL_NOT, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return call();
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_BRACKET)) {  // square(a, b, c)
            do {
                if (arguments.size() >= 255) {
                    error(peek(), "The number of arguments exceeded the maximum limit (255).");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }
        Token bracket = consume(RIGHT_BRACKET, "Expect ']' after arguments.");
        return new Expr.Call(callee, bracket, arguments);
    }

    private Expr call() {
        Expr expr = primary();

        while (true) {
            if (match(LEFT_BRACKET)) expr = finishCall(expr);
            else break;
        }

        return expr;
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(IDENT)) {
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        // consumes current token if it has any of given types
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        // true if the current token is of given type
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        // returns the current token we have yet to consume
        return tokens.get(current);
    }

    private Token previous() {
        // returns the most recently consumed token
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        TaylorScript.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch (peek().type) {
                case FUNC:
                case LOOP:
                case VAR:
                case IF:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }
}
