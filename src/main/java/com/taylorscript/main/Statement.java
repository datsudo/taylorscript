package com.taylorscript.main;

import java.util.List;

abstract class Statement {
    interface Visitor<R> {
        R visitBlockStatement(Block statement);
        R visitExpressionStatement(Expression statement);
        R visitFunctionStatement(Function statement);
        R visitIfStatement(If statement);
        R visitPrintStatement(Print statement);
        R visitLetStatement(Let statement);
        R visitWhileStatement(While statement);
    }

    static class Block extends Statement {
        Block(List<Statement> statements) {
            this.statements = statements;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStatement(this);
        }

        final List<Statement> statements;
    }

    static class Expression extends Statement {
        Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStatement(this);
        }

        final Expr expression;
    }

    static class Function extends Statement {
        Function(Token name, List<Token> params, List<Statement> body) {
            this.name = name;
            this.params = params;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStatement(this);
        }

        final Token name;
        final List<Token> params;
        final List<Statement> body;
    }

    static class If extends Statement {
        If(Expr condition, Statement thenBranch, Statement elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStatement(this);
        }

        final Expr condition;
        final Statement thenBranch;
        final Statement elseBranch;
    }

    static class Print extends Statement {
        Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStatement(this);
        }

        final Expr expression;
    }

    static class Let extends Statement {
        Let(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLetStatement(this);
        }

        final Token name;
        final Expr initializer;
    }

    static class While extends Statement {
        While(Expr condition, Statement body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStatement(this);
        }

        final Expr condition;
        final Statement body;
    }


    abstract <R> R accept(Visitor<R> visitor);
}
