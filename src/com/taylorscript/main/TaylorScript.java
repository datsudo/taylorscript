package com.taylorscript.main;

import org.sk.PrettyTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TaylorScript {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

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

        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
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
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parse();

        if (hadError) return;

//        System.out.println(new AstPrinter().print(expression));
        interpreter.interpret(statements);
    }

    private static void printTokenTable(List<Token> tokens) {
        PrettyTable tokenTable = new PrettyTable("TOKEN", "LEXEME", "LITERAL");
        for (Token token: tokens) {
            String literal = "";
            if (token.literal != null) {
                literal = token.literal + "";
            }
            tokenTable.addRow(token.type + "", token.lexeme, literal);
        }
        System.out.println(tokenTable);
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

    static void runtimeError(RuntimeError error) {
        System.err.println("[LINE " + error.token.lineNumber + "] " + error.getMessage());
        hadRuntimeError = true;
    }
}