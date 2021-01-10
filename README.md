**Illi** ([Moroccan Tamazight](https://en.wikipedia.org/wiki/Standard_Moroccan_Berber): ⵉⵍⵍⵉ, _lit_. 'my daughter') is a small, class-based, dynamically typed toy language. This language uses an AST-based interpreter for educational purposes.


Ilyasse BENRKIA
benrkyailyass@gmail.com

## Syntax

**Illi** is a c-family language with a modern syntax. You can think of it as an enhanced Javascript! maybe? Check out its [Lexical grammar](IlliLexer.g4). Typically, **Illi** script is a plain text file with a `.ily` file extension.

### Operator Precedence

**Illi** follows C's operator precedence and associativity. Operators are listed top to bottom, in descending precedence.

<table>
  <tbody>
    <tr>
      <th>Precedence</th>
      <th>Operator</th>
      <th>Description</th>
      <th>Associativity</th>
    </tr>
    <tr>
      <td>1</td>
      <td><code>( ... )</code></td>
      <td>Grouping</td>
      <td>n/a</td>
    </tr>
    <tr>
      <td>2</td>
      <td><code>-</code> <code>!</code></td>
      <td>Negate, Logical NOT</td>
      <td>Right-to-left</td>
    </tr>
    <tr>
      <td>3</td>
      <td><code>*</code> <code>/</code></td>
      <td>Multiplication, Division</td>
      <td>Left-to-right</td>
    </tr>
    <tr>
      <td>4</td>
      <td><code>+</code> <code>-</code></td>
      <td>Addition, Subtraction</td>
      <td>Left-to-right</td>
    </tr>
    <tr>
      <td>5</td>
      <td><code>&lt;</code> <code>&lt;=</code> <code>&gt;</code> <code>&gt;=</code></td>
      <td>Comparison</td>
      <td>Left-to-right</td>
    </tr>
    <tr>
      <td>6</td>
      <td><code>==</code> <code>!=</code></td>
      <td>Equals, Not equal</td>
      <td>Left-to-right</td>
    </tr>
    <tr>
      <td>7</td>
      <td><code>?:</code></td>
      <td>Ternary conditional</td>
      <td>Right-to-left</td>
    </tr>
    <tr>
      <td>8</td>
      <td><code>=</code></td>
      <td>Assignment</td>
      <td>Right-to-left</td>
    </tr>
    <tr>
      <td>9</td>
      <td><code>,</code></td>
      <td>Comma</td>
      <td>Left-to-right</td>
    </tr>
  </tbody>
</table>
