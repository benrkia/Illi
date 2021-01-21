package com.benrkia.illi;

import java.util.ArrayList;
import java.util.List;

import static com.benrkia.illi.TokenType.*;

class Parser {
  private static class ParseError extends RuntimeException {}

  @FunctionalInterface
  private interface Production {
    Expr apply();
  }

  private final List<Token> tokens;
  private int current = 0;

  Parser (List<Token> tokens) {
    this.tokens = tokens;
  }

  List<Stmt> parse() {
    var statements = new ArrayList<Stmt>();

    while (!isAtEnd()) {
      statements.add(declaration());
    }

    return statements;
  }

  private Stmt declaration() {
    try {
      if (match(VAR)) return varDeclarations();

      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Stmt varDeclarations () {
    List<Stmt> variables = new ArrayList<>();

    variables.add(varDeclaration());
    while (match(COMMA)) {
      variables.add(varDeclaration());
    }

    consume(SEMICOLON, "';' Expected");
    return new Stmt.Vars(variables);
  }

  private Stmt varDeclaration () {
    var name = consume(IDENTIFIER, "Expect variable name.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = assignment();
    }

    return new Stmt.Var(name, initializer);
  }

  private Stmt statement() {
    if (match(PRINT)) return printStatement();
    if (match(LEFT_BRACE)) return block();

    return expressionStatement();
  }

  private Stmt printStatement() {
    Expr value = expression();
    consume(SEMICOLON, "Expected ';' after value.");
    return new Stmt.Print(value);
  }

  private Stmt block() {
    List<Stmt> statements = new ArrayList<>();

    while (!check(RIGHT_BRACE) && !isAtEnd()) {
      statements.add(declaration());
    }

    consume(RIGHT_BRACE, "Expected '}' after block statement.");
    return new Stmt.Block(statements);
  }

  private Stmt expressionStatement() {
    Expr expr = expression();
    consume(SEMICOLON, "Expected ';' after expression.");
    return new Stmt.Expression(expr);
  }

  private Expr expression() {
    return comma();
  }

  private Expr comma () {
    var expr = assignment();

    while (match(COMMA)) {
      var right = assignment();
      expr = new Expr.Comma(expr, right);
    }

    return expr;
  }

  private Expr assignment() {
    var expr = conditional();

    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = assignment();

      if (expr instanceof Expr.Variable) {
        Token name = ((Expr.Variable)expr).name;
        return new Expr.Assign(name, value);
      }

      error(equals, "Invalid assignment target.");
    }

    return expr;
  }

  private Expr conditional () {
    var expr = equality();

    if (match(QUESTION)) {
      var thenBranch = expression();
      consume(COLON, "Expect ':' after then branch of expression.");
      var elseBranch = conditional();
      expr = new Expr.Conditional(expr, thenBranch, elseBranch);
    }

    return expr;
  }

  private Expr equality () {
    return parseLeftAssociative(this::comparison, BANG_EQUAL, EQUAL_EQUAL);
  }

  private Expr comparison () {
    return parseLeftAssociative(this::term, GREATER_EQUAL, GREATER, LESS_EQUAL, LESS);
  }

  private Expr term () {
    return parseLeftAssociative(this::factor, PLUS, MINUS);
  }

  private Expr factor () {
    return parseLeftAssociative(this::unary, STAR, SLASH);
  }

  private Expr unary () {
    if (match(BANG, MINUS)) {
      var operator = previous();
      var right = unary();
      return new Expr.Unary(operator, right);
    }

    return primary();
  }

  private Expr primary () {
    if (match(FALSE)) return new Expr.Literal(false);
    if (match(TRUE)) return new Expr.Literal(true);
    if (match(NIL)) return new Expr.Literal(null);
    if (match(IDENTIFIER)) return new Expr.Variable(previous());

    if (match(STRING, NUMBER)) return new Expr.Literal(previous().literal);

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }

    // Erroneous productions
    if (match(BANG_EQUAL, EQUAL_EQUAL)) {
      error(previous(), "Missing left-hand operand.");
      equality();
      return null;
    }

    if (match(GREATER_EQUAL, GREATER, LESS_EQUAL, LESS)) {
      error(previous(), "Missing left-hand operand.");
      comparison();
      return null;
    }

    if (match(PLUS)) {
      error(previous(), "Missing left-hand operand.");
      return null;
    }

    if (match(STAR, SLASH)) {
      error(previous(), "Missing left-hand operand.");
      factor();
      return null;
    }

    throw error(peek(), "Expect expression.");
  }

  private Expr parseLeftAssociative (Production operandMethod, TokenType... types) {
    var expr = operandMethod.apply();

    while (match(types)) {
      var operator = previous();
      var right = operandMethod.apply();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token advance () {
    if (!isAtEnd()) current++;
    return previous();
  }

  private Token previous () {
    return tokens.get(current - 1);
  }

  private Token consume (TokenType type, String message) {
    if (check(type)) return advance();
    throw error(peek(), message);
  }

  private boolean check (TokenType type) {
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  private boolean match (TokenType... types) {
    for (var type: types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private void synchronize () {
    advance();

    while (!isAtEnd()) {
      if (previous().type == SEMICOLON) return;

      switch (peek().type) {
        case CLASS:
        case FUN:
        case VAR:
        case FOR:
        case IF:
        case WHILE:
        case PRINT:
        case RETURN:
          return;
      }

      advance();
    }
  }

  private ParseError error (Token token, String message) {
    Illi.error(token, message);
    return new ParseError();
  }
}
