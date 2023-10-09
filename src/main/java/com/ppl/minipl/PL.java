package com.ppl.minipl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class PL {
    static boolean hadError = false;

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

        if (hadError) {
            System.exit(65);
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.println("[ IN] << ");
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        Parser parser = new Parser(source);
        List<Token> tokens = parser.parseTokens();

        for (Token token: tokens) {
            System.out.println(token);
        }
    }

    static void error(int lineNumber, String message) {
        report(lineNumber, "", message);
    }

    private static void report(int lineNumber, String where, String message) {
        System.err.println("[LINE " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}