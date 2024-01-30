package com.taylorscript.main;

import java.util.List;

public class TSFunction implements TSCallable {
    private final Statement.Function declaration;

    TSFunction(Statement.Function declaration) {
        this.declaration = declaration;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        Environment environment = new Environment(interpreter.globals);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, args.get(i));
        }

        interpreter.executeBlock(declaration.body, environment);
        return null;
    }
}
