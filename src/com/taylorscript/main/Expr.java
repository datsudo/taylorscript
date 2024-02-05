package com.taylorscript.main;

import java.util.List;

abstract class Expr {
    static class Assign extends Expr {
        Assign(Token name, Expr value, Token equals) {
            this.name = name;
            this.value = value;
            this.equals = equals;
        }

        final Token name;
        final Expr value;
        final Token equals;
    }

    static class Binary extends Expr {
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    static class Call extends Expr {
        Call(Expr callee, Token bracket, List<Expr> args) {
            this.callee = callee;
            this.bracket = bracket;
            this.args = args;
        }

        final Expr callee;
        final Token bracket;
        final List<Expr> args;
    }

    static class Grouping extends Expr {
        Grouping(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;
    }

    static class Literal extends Expr {
        Literal(Object value) {
            this.value = value;
        }

        final Object value;
    }

    static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    static class Unary extends Expr {
        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        final Token operator;
        final Expr right;
    }

    static class Variable extends Expr {
        Variable(Token name) {
            this.name = name;
        }

        final Token name;
    }
}
