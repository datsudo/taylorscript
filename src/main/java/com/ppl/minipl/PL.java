package com.ppl.minipl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

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

    private static void runFile(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        run(new String(bytes, Charset.defaultCharset()));
    }
}