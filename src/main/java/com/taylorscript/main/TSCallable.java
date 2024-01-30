package com.taylorscript.main;

import java.util.List;

interface TSCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> args);
}
