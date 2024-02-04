package com.taylorscript.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TaylorScript {
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("USAGE: taylorscript [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String filePath) throws IOException {
        if (!filePath.substring(filePath.lastIndexOf('.')).equals(".tay")) {
            System.err.println("[FileExtensionError] Source file must end with .tay extension.");
            System.exit(65);
        }
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        System.out.println("TaylorScript v0.1 (2024)");
        for (;;) {
            System.out.print("->> ");
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            run(line);
        }
    }

    private static void run(String source) throws IOException {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    static void error(int lineNumber, int colNumber, String message) {
        report(lineNumber, colNumber, "", message);
    }

    private static void report(int lineNumber, int colNumber, String where, String message) {
        System.err.println("[LINE " + lineNumber + "; COLUMN " + colNumber + "] Error" + where + ": " + message);
        System.exit(65);
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.lineNumber, token.colNumber, " at end", message);
        } else {
            report(token.lineNumber, token.colNumber, " at '" + token.lexeme + "'", message);
        }
    }
}