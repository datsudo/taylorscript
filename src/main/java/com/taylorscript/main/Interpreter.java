package com.taylorscript.main;

class Interpreter implements Expr.Visitor<Object> {
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        // Returns the literal value of literal expression
        return expr.value;
    }
}