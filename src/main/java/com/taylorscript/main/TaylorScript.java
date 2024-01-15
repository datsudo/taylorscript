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
        if (!filePath.substring(filePath.lastIndexOf('.')).equals(".tay")) {
            System.err.println("[FileExtensionError] Source file must end with .tay extension.");
            System.exit(65);
        }
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
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        printTokenTable(tokens);
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
}