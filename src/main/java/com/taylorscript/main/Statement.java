package com.taylorscript.main;

import java.util.List;

abstract class Statement {
    interface Visitor<R> {
        R visitExpressionStatement(Expression statement);
        R visitPrintStatement(Print statement);
        R visitLetStatement(Let statement);
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
