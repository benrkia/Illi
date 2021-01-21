package com.benrkia.illi;

import java.util.List;
import java.util.Objects;

interface Interpreter<T> {
  void interpret(T t);
}

class InterpreterImpl implements Interpreter<List<Stmt>>, Expr.Visitor<Object>, Stmt.Visitor<Void> {
  private Environment environment = new Environment();
  private static final Object uninitialized = new Object();

  @Override
  public void interpret (List<Stmt> statements) {
    try {
      statements.forEach(this::execute);
    } catch (RuntimeError error) {
      Illi.runtimeError(error);
    }
  }

  @Override
  public Void visit (Stmt.Expression stmt) {
    evaluate(stmt.expr);
    return null;
  }

  @Override
  public Void visit (Stmt.Print stmt) {
    var value = evaluate(stmt.expr);
    System.out.println(stringify(value));
    return null;
  }

  @Override
  public Void visit (Stmt.Var stmt) {
    environment.define(stmt.name, uninitialized);

    if (stmt.initializer != null) {
      environment.assign(stmt.name, evaluate(stmt.initializer));
    }

    return null;
  }

  @Override
  public Void visit (Stmt.Vars stmt) {
    stmt.variables.forEach(this::execute);
    return null;
  }

  @Override
  public Void visit (Stmt.Block stmt) {
    var enclosing = environment;
    try {
      this.environment = new Environment(enclosing);
      stmt.statements.forEach(this::execute);
    } finally {
      this.environment = enclosing;
    }
    return null;
  }

  private void execute(Stmt stmt) {
    stmt.accept(this);
  }

  @Override
  public Object visit (Expr.Assign expr) {
    Object value = evaluate(expr.value);
    environment.assign(expr.name, value);

    return value;
  }

  @Override
  public Object visit (Expr.Comma expr) {
    evaluate(expr.left); // C-like evaluate and discard
    return evaluate(expr.right);
  }

  @Override
  public Object visit (Expr.Conditional expr) {
    var condition = evaluate(expr.expr);
    return isTruthy(condition) ?
      evaluate(expr.thenBranch) : evaluate(expr.elseBranch);
  }

  @Override
  public Object visit (Expr.Binary expr) {
    var left = evaluate(expr.left);
    var right = evaluate(expr.right);

    switch (expr.operator.type) {
      case MINUS:
        requireNumberOperands(expr.operator, left, right);
        return (double) left - (double) right;
      case STAR:
        requireNumberOperands(expr.operator, left, right);
        return (double) left * (double) right;
      case SLASH:
        requireNumberOperands(expr.operator, left, right);
        if ((double) right == 0.0) {
          throw new RuntimeError(expr.operator, "Arithmetic division by 0");
        }
        return (double) left / (double) right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double) left + (double) right;
        }
        if (left instanceof String || right instanceof String) {
          return stringify(left).concat(stringify(right));
        }
        throw new RuntimeError(expr.operator,
                "Operands must be two numbers or at least one of them is string.");
      case GREATER:
        if (left instanceof Double && right instanceof Double) {
          return (double) left > (double) right;
        }
        if (left instanceof String && right instanceof String) {
          return ((String) left).compareTo((String) right) > 0;
        }
        throw new RuntimeError(expr.operator,
                "Operands must be two numbers or two strings.");
      case GREATER_EQUAL:
        if (left instanceof Double && right instanceof Double) {
          return (double) left >= (double) right;
        }
        if (left instanceof String && right instanceof String) {
          return ((String) left).compareTo((String) right) >= 0;
        }
        throw new RuntimeError(expr.operator,
                "Operands must be two numbers or two strings.");
      case LESS:
        if (left instanceof Double && right instanceof Double) {
          return (double) left < (double) right;
        }
        if (left instanceof String && right instanceof String) {
          return ((String) left).compareTo((String) right) < 0;
        }
        throw new RuntimeError(expr.operator,
                "Operands must be two numbers or two strings.");
      case LESS_EQUAL:
        if (left instanceof Double && right instanceof Double) {
          return (double) left <= (double) right;
        }
        if (left instanceof String && right instanceof String) {
          return ((String) left).compareTo((String) right) <= 0;
        }
        throw new RuntimeError(expr.operator,
                "Operands must be two numbers or two strings.");
      case BANG_EQUAL:
        return !isEqual(left, right);
      case EQUAL_EQUAL:
        return isEqual(left, right);
    }
    return null;
  }

  @Override
  public Object visit (Expr.Unary expr) {
    var right = evaluate(expr.right);
    switch (expr.operator.type) {
      case MINUS:
        requireNumberOperand(expr.operator, right);
        return -(double) right;
      case BANG:
        return !isTruthy(right);
    }

    return null;
  }

  @Override
  public Object visit (Expr.Literal expr) {
    return expr.value;
  }

  @Override
  public Object visit (Expr.Grouping expr) {
    return evaluate(expr.expression);
  }

  @Override
  public Object visit (Expr.Variable expr) {
    var value = environment.get(expr.name);

    if (value == uninitialized) {
      throw new RuntimeError(expr.name, "Variable '" + expr.name.lexeme + "' might not have been initialized");
    }

    return value;
  }

  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  /**
   * Check Truthiness using Ruby's style: everything is truthy except for false & nil
   */
  private boolean isTruthy (Object value) {
    if (value == null) return false;
    if (value instanceof Boolean) return (boolean) value;
    return true;
  }

  private boolean isEqual (Object o1, Object o2) {
    return Objects.equals(o1, o2);
  }

  private void requireNumberOperand (Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new RuntimeError(operator, "Operand must be a number");
  }

  private void requireNumberOperands (Token operator, Object o1, Object o2) {
    if (o1 instanceof Double && o2 instanceof Double) return;
    throw new RuntimeError(operator, "Operands must be numbers.");
  }

  private String stringify (Object o) {
    if (o == null) return "nil";
    var value = o.toString();
    if (o instanceof Double) {
      if (value.endsWith(".0")) {
        return value.substring(0, value.length() - 2);
      }
    }

    return value;
  }
}
