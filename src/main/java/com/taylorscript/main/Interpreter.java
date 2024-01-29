package com.taylorscript.main;

class Interpreter implements Expr.Visitor<Object> {
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        // Returns the literal value of literal expression
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        // Evaluates grouping in expressions
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        // Evaluates the subexpressions recursively
        return expr.accept(this);
    }
}
