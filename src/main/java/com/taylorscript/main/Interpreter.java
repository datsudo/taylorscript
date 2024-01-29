package com.taylorscript.main;

import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Statement.Visitor<Void> {
    private Environment environment = new Environment();

    void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            TaylorScript.runtimeError(error);
        }
    }

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
                checkNumberOperand(expr.operator, rightSubExpr);
                return -(double)rightSubExpr;
        }

        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        // Check left/right operands if they're numbers
        if (left instanceof Double && right instanceof Double) {
            if (operator.type == TokenType.SLASH && (Double)right == 0) {
                throw new RuntimeError(operator, "Zero division error.");
            }
            return;
        }
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
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

    private String stringify(Object object) {
        if (object == null) return "The0";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
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

    private void execute(Statement statement) {
        statement.accept(this);
    }

    @Override
    public Void visitExpressionStatement(Statement.Expression statement) {
        evaluate(statement.expression);
        return null;
    }

    @Override
    public Void visitPrintStatement(Statement.Print statement) {
        Object value = evaluate(statement.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitLetStatement(Statement.Let statement) {
        Object value = null;
        if (statement.initializer != null) {
            value = evaluate(statement.initializer);
        }

        environment.define(statement.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case GTHAN:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GEQ:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LTHAN:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LEQ:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case BEQ:
                return !isEqual(left, right);
            case EEQ:
                return isEqual(left, right);
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
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

                throw new RuntimeError(expr.operator, "Operands must be numbers or strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
        }

        return null;
    }
}
