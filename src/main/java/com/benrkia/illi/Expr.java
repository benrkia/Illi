package com.benrkia.illi;

abstract class Expr {
  abstract <R> R accept(Visitor<R> visitor);

  interface Visitor<R> {
    R visit(Assign expr);
    R visit(Comma expr);
    R visit(Conditional expr);
    R visit(Binary expr);
    R visit(Unary expr);
    R visit(Literal expr);
    R visit(Grouping expr);
    R visit(Variable expr);
  }

  static class Assign extends Expr {
    Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

    final Token name;
    final Expr value;
  }

  static class Comma extends Expr {
    Comma(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

    final Expr left;
    final Expr right;
  }

  static class Conditional extends Expr {
    Conditional(Expr expr, Expr thenBranch, Expr elseBranch) {
      this.expr = expr;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

    final Expr expr;
    final Expr thenBranch;
    final Expr elseBranch;
  }

  static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

    final Expr left;
    final Token operator;
    final Expr right;
  }

  static class Unary extends Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

    final Token operator;
    final Expr right;
  }

  static class Literal extends Expr {
    Literal(Object value) {
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

    final Object value;
  }

  static class Grouping extends Expr {
    Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

    final Expr expression;
  }

  static class Variable extends Expr {
    Variable(Token name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

    final Token name;
  }

}
