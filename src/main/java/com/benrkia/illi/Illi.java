package com.benrkia.illi;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class Illi {
  private static final Interpreter<List<Stmt>> interpreter = new InterpreterImpl();

  static boolean hadError = false;
  static boolean hadRuntimeError = false;

  public static void main (String[] args) throws IOException {

    if (args.length > 1) {
      System.out.println("Usage: Illi <source file>");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runFile (String filePath) throws IOException {
    var sourcePath = Paths.get(filePath);
    if (!Files.isReadable(sourcePath)) {
      System.out.println("File not found: " + filePath);
      System.exit(66);
    }

    var bytes = Files.readAllBytes(sourcePath);
    run(new String(bytes, Charset.defaultCharset()));

    if (hadError) System.exit(65);
    if (hadRuntimeError) System.exit(70);
  }

  private static void runPrompt () throws IOException {
    System.out.println("(To exit, press ^D or enter #exit)");
    var input = new InputStreamReader(System.in);
    var reader = new BufferedReader(input);
    for(;;) {
      System.out.print("> ");
      var line = reader.readLine();
      if (line == null || "#exit".equals(line.trim())) {
        break;
      }
      run(line);
      hadError = false;
    }
  }

  private static void run (String line) {
    var scanner = new Lexer(line);
    var tokens = scanner.scanTokens();
    var statements = new Parser(tokens).parse();

    if (hadError) return;

    interpreter.interpret(statements);
  }

  static void error (int line, String message) {
    report(line, "", message);
  }
  static void error (Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }
  static void runtimeError (RuntimeError error) {
    System.err.println("[line "+ error.token.line +"] Error at '"+ error.token.lexeme +"'");
    System.err.println(error.getMessage());
    hadRuntimeError = true;
  }

  private static void report (int line, String where, String message) {
    System.err.println("[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }
}
