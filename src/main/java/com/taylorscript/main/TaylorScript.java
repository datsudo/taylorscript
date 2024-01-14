package com.taylorscript.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TaylorScript {
    static boolean hadError = false;

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
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) {
            System.exit(65);
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        System.out.println("TaylorScript v0 (2024)");
        for (;;) {
            System.out.print("->> ");
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) throws IOException {
//        Lexer lexer = new Lexer(source);
//        List<Token> tokens = lexer.scanTokens();
//
//        for (Token token: tokens) {
//            System.out.println(token);
//        }
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        if (hadError) return;

        System.out.println(new AstPrinter().print(expression));

    }

    static void error(int lineNumber, String message) {
        report(lineNumber, "", message);
    }

    private static void report(int lineNumber, String where, String message) {
        System.err.println("[LINE " + lineNumber + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.lineNumber, "at end", message);
        } else {
            report(token.lineNumber, " at '" + token.lexeme + "'", message);
        }
    }
}