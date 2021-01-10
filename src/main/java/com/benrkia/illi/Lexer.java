package com.benrkia.illi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.benrkia.illi.TokenType.*;

class Lexer {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("and",     AND);
    keywords.put("class",   CLASS);
    keywords.put("else",    ELSE);
    keywords.put("false",   FALSE);
    keywords.put("fun",     FUN);
    keywords.put("for",     FOR);
    keywords.put("if",      IF);
    keywords.put("nil",     NIL);
    keywords.put("or",      OR);
    keywords.put("print",   PRINT);
    keywords.put("return",  RETURN);
    keywords.put("super",   SUPER);
    keywords.put("this",    THIS);
    keywords.put("true",    TRUE);
    keywords.put("var",     VAR);
    keywords.put("while",   WHILE);
  }

  private int current = 0;
  private int start = 0;
  // TODO: update to support enhanced location for a better error reporting
  private int line = 1;

  public Lexer (String source) {
    this.source = source;
  }

  public List<Token> scanTokens () {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return List.copyOf(tokens);
  }

  private void scanToken () {
    var c = advance();
    switch (c) {
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case ';': addToken(SEMICOLON); break;
      case ':': addToken(COLON); break;
      case '?': addToken(QUESTION); break;
      case '*': addToken(STAR); break;
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '/':
        if (match('/')) {
          while (peek() != '\n' && peek() != '\r' && !isAtEnd()) advance();
        } else {
          addToken(SLASH);
        }
        break;
      case '"': string(); break;
      case ' ':
      case '\r':
      case '\t':
        break;
      case '\n':
        ++line;
        break;
      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          Illi.error(line, "Unexpected character.");
        }
        break;
    }
  }

  private void addToken (TokenType type) {
    addToken(type, null);
  }

  private void addToken (TokenType type, Object literal) {
    var lexeme = source.substring(start, current);
    tokens.add(new Token(type, lexeme, literal, line));
  }

  private void identifier () {
    while (isAlphaNumeric(peek())) advance();

    var value = source.substring(start, current);
    var type = keywords.getOrDefault(value, IDENTIFIER);
    addToken(type);
  }

  private void number () {
    while (isDigit(peek())) advance();

    if (peek() == '.' && isDigit(peekNext())) {
      advance();
      while (isDigit(peek())) advance();
    }

    var value = source.substring(start, current);
    addToken(NUMBER, Double.parseDouble(value));
  }

  private void string () {
    boolean needsEscape = false;
    while ((needsEscape || peek() != '"') && !isAtEnd()) {
      if (needsEscape) needsEscape = false;
      else if (peek() == '\\') needsEscape = true;
      if (peek() == '\n') ++line;
      advance();
    }

    if (isAtEnd()) {
      Illi.error(line, "Unterminated string.");
      return;
    }
    advance();

    var value = escape(source.substring(start + 1, current - 1));
    addToken(STRING, value);
  }

  private String escape(String value) {
    var _bytes = value.getBytes();
    var needsEscape = false;
    var escaped = new StringBuilder();

    for (var _byte: _bytes) {
      if (needsEscape) {
        switch (_byte) {
          case 0x22: // \"
            escaped.append((char) 0x22);
            break;
          case 0x62: // \b
            escaped.append((char) 0x8);
            break;
          case 0x66: // \f
            escaped.append((char) 0xc);
            break;
          case 0x6e: // \n
            escaped.append((char) 0xa);
            break;
          case 0x72: // \r
            escaped.append((char) 0xd);
            break;
          case 0x74: // \t
            escaped.append((char) 0x9);
            break;
          case 0x5c: // \\
            escaped.append((char) 0x5c);
            break;
          default:
            Illi.error(line, "Illegal escape character in string literal.");
            return null;
        }
        needsEscape = false;
      }
      else if (_byte == 0x5c) needsEscape = true;
      else escaped.append((char) _byte);
    }

    return escaped.toString();
  }

  private char peek () {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

  private char peekNext () {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private char advance () {
    ++current;
    return source.charAt(current - 1);
  }

  private boolean match (char expected) {
    if (isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;

    ++current;
    return true;
  }

  private boolean isDigit (char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAlpha (char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  private boolean isAlphaNumeric (char c) {
    return isAlpha(c) || isDigit(c);
  }

  private boolean isAtEnd () {
    return current >= source.length();
  }
}
