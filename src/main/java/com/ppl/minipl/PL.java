package com.ppl.minipl;

import java.io.IOException;

public class PL {
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("USAGE: PL [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }
}
