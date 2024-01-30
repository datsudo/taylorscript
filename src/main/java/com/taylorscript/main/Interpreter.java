package com.taylorscript.main;

import java.util.ArrayList;
import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Statement.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;

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
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.LOGICAL_OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        // evaluate first the operand before the unary operator
        Object rightSubExpr = evaluate(expr.right);

        switch (expr.operator.type) {
            case LOGICAL_NOT:
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

    @Override
    public Void visitWhileStatement(Statement.While statement) {
        while (isTruthy(evaluate(statement.condition))) {
            execute(statement.body);
        }
        return null;
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
        switch (expr.equals.type) {
            case ASSIGN_EQUAL:
                break;
            case PLUS_EQ: {
                Object current = environment.get(expr.name);
                checkNumberOperands(expr.equals, current, value);
                value = (double) current + (double) value;
                break;
            }
            case MINUS_EQ: {
                Object current = environment.get(expr.name);
                checkNumberOperands(expr.equals, current, value);
                value = (double) current - (double) value;
                break;
            }
            case STAR_EQ: {
                Object current = environment.get(expr.name);
                checkNumberOperands(expr.equals, current, value);
                value = (double) current * (double) value;
                break;
            }
            case SLASH_EQ: {
                Object current = environment.get(expr.name);
                checkNumberOperands(expr.equals, current, value);
                value = (double) current / (double) value;
                break;
            }
        }

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

    void executeBlock(List<Statement> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Statement statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStatement(Statement.Block statement) {
        executeBlock(statement.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStatement(Statement.Expression statement) {
        evaluate(statement.expression);
        return null;
    }

    @Override
    public Void visitFunctionStatement(Statement.Function statement) {
        TSFunction function = new TSFunction(statement);
        environment.define(statement.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitIfStatement(Statement.If statement) {
        if (isTruthy(evaluate(statement.condition))) {
            execute(statement.thenBranch);
        } else if (statement.elseBranch != null) {
            execute(statement.elseBranch);
        }

        return null;
    }

    @Override
    public Void visitPrintStatement(Statement.Print statement) {
        Object value = evaluate(statement.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStatement(Statement.Return statement) {
        Object value = null;
        if (statement.value != null) value = evaluate(statement.value);

        throw new Return(value);
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
            case GREATER_THAN:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_THAN_EQ:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS_THAN:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_THAN_EQ:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case NOT_EQUAL:
                return !isEqual(left, right);
            case COMP_EQUAL:
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
            case CARET:
                checkNumberOperands(expr.operator, left, right);
                return Math.pow((double) left, (double) right);
        }

        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> args = new ArrayList<>();
        for (Expr arg : expr.args) {
            args.add(evaluate(arg));
        }

        if (!(callee instanceof TSCallable)) {
            throw new RuntimeError(expr.bracket, "Can only call functions.");
        }

        TSCallable function = (TSCallable)callee;

        // arity: number of args the functions/operators expects
        if (args.size() != function.arity()) { 
            throw new RuntimeError(expr.bracket, "Expected " +
                                   function.arity() + " arguments but got " +
                                   args.size() + ".");
        }
        return function.call(this, args);
    }
}
