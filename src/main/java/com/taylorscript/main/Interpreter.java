package com.taylorscript.main;

class Interpreter implements Expr.Visitor<Object> {
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        // Returns the literal value of literal expression
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        // evaluate first the operand before the unary operator
        Object rightSubExpr = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(rightSubExpr);
            case MINUS:
                return -(double)rightSubExpr;
        }

        return null;
    }

    @Override
    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
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

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                return (double)left - (double)right;
            case PLUS:
                // If both left/right operands are number, evaluate their sum
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                // If both are strings, concatenate
                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                break;
            case SLASH:
                return (double)left / (double)right;
            case STAR:
                return (double)left * (double)right;
        }

        return null;
    }
}
