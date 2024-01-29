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
                checkNumberOperand(expr.operator, right);
                return -(double)rightSubExpr;
        }

        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");  // TODO: implement RuntimeError class
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        // Just checks if both objects are equal
        // Used for "==" operator
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
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
            case GTHAN:
                return (double)left > (double)right;
            case GEQ:
                return (double)left >= (double)right;
            case LTHAN:
                return (double)left < (double)right;
            case LEQ:
                return (double)left <= (double)right;
            case BEQ:
                return !isEqual(left, right);
            case EEQ:
                return isEqual(left, right);
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
