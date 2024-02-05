package com.taylorscript.main;

import java.util.List;

abstract class Statement {
    static class Block extends Statement {
        Block(List<Statement> statements) {
            this.statements = statements;
        }

        final List<Statement> statements;
    }

    static class Expression extends Statement {
        Expression(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;
    }

    static class Function extends Statement {
        Function(Token name, List<Token> params, List<Statement> body) {
            this.name = name;
            this.params = params;
            this.body = body;
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

        final Expr condition;
        final Statement thenBranch;
        final Statement elseBranch;
    }

    static class Print extends Statement {
        Print(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;
    }

    static class Return extends Statement {
        Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }

        final Token keyword;
        final Expr value;
    }

    static class Let extends Statement {
        Let(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        final Token name;
        final Expr initializer;
    }

    static class While extends Statement {
        While(Expr condition, Statement body) {
            this.condition = condition;
            this.body = body;
        }

        final Expr condition;
        final Statement body;
    }
}
