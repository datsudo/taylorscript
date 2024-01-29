package com.taylorscript.main;

import java.util.List;

abstract class Statement {
    interface Visitor<R> {
        R visitBlockStatement(Block statement);
        R visitExpressionStatement(Expression statement);
        R visitIfStatement(If statement);
        R visitPrintStatement(Print statement);
        R visitLetStatement(Let statement);
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

    static class If extends Statement {
        If(Expr condition, Statement thenBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStatement(this);
        }

        final Expr condition;
        final Statement thenBranch;
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


    abstract <R> R accept(Visitor<R> visitor);
}
