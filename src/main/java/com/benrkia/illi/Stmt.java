package com.benrkia.illi;

import java.util.List;

abstract class Stmt {
  abstract <R> R accept(Visitor<R> visitor);

  interface Visitor<R> {
    R visit(Expression stmt);
    R visit(Print stmt);
    R visit(Var stmt);
    R visit(Block stmt);
  }

  static class Expression extends Stmt {
    Expression(Expr expr) {
      this.expr = expr;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

    final Expr expr;
  }

  static class Print extends Stmt {
    Print(Expr expr) {
      this.expr = expr;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

    final Expr expr;
  }

  static class Var extends Stmt {
    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

    final Token name;
    final Expr initializer;
  }

  static class Block extends Stmt {
    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

    final List<Stmt> statements;
  }

}
