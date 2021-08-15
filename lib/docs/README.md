Bullwinkle: a runtime parser for BNF grammars
=============================================

[![Travis](https://img.shields.io/travis/sylvainhalle/Bullwinkle.svg?style=flat-square)](https://travis-ci.org/sylvainhalle/Bullwinkle)
[![Coverity Scan Build Status](https://img.shields.io/coverity/scan/15155.svg?style=flat-square)](https://scan.coverity.com/projects/sylvainhalle-bullwinkle)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sylvainhalle_bullwinkle&metric=coverage)](https://sonarcloud.io/dashboard?id=sylvainhalle_bullwinkle)
<img src="http://leduotang.ca/Bullwinkle.svg" height="20" alt="Downloads"/>

Bullwinkle is a parser for LL(k) languages that operates through recursive descent
with backtracking.

[Parser generators](http://en.wikipedia.org/wiki/Parser_generator) such as
ANTLR, Yacc or Bison take a grammar as input and produce code for a parser
specific to that grammar, which must then be compiled to be used. On the
contrary, Bullwinkle reads the definition of the grammar (expressed in
[Backus-Naur Form](http://en.wikipedia.org/wiki/Backus-Naur_form) (BNF)) at
*runtime* and can parse strings on the spot.

Other unique features of Bullwinkle include:

- Instances of the Bullwinkle parser can be safely serialized with
  [Azrael](https://github.com/sylvainhalle/Azrael).
- [Partial parsing](#partial), a special mode where input strings can
  contain non-terminal symbols from the grammar. A string can hence be
  partially verified for syntactical correctness.
- [Object builders](#builder), a class of objects that makes it easy to
  traverse a parse tree and build an output object recursively.

Table of Contents                                                    {#toc}
-----------------

- [An example](#example)
- [Compiling and installing Bullwinkle](#install)
- [Defining a grammar](#grammar)
- [Using the parse tree](#tree)
- [Partial parsing](#partial)
- [Using object builders](#builder)
- [Command-line usage](#cli)
- [About the author](#about)

An example                                                       {#example}
----------

Consider for example the following simple grammar, taken from the file
`Examples/Simple-Math.bnf` in the Bullwinkle archive:

    <exp> := <add> | <sub> | <mul> | <div> | - <exp> | <num>;
    <add> := <num> + <num> | ( <exp> + <exp> );
    <sub> := <num> - <num> | ( <exp> - <exp> );
    <mul> := <num> × <num> | ( <exp> × <exp> );
    <div> := <num> ÷ <num> | ( <exp> ÷ <exp> );
    <num> := ^[0-9]+;

Here is a simple Java program that reads characters strings and tries to parse
them against this grammar (a complete working program can be found in the file
`SimpleExample.java`:

    try
    {
      BnfParser parser = new BnfParser("Examples/Simple-Math.bnf");
      ParseNode node1 = parser.parse("3+4");
      ParseNode node2 = parser.parse("(10 + (3 - 4))");
    }
    catch (IOException | InvalidGrammarExpression | ParseException)
    {
      System.err.println("Some error occurred");
    }

The first instruction loads the grammar definition and instantiates an
object `parser` for that grammar. Calls to method `parse()` give this parser
a character string, and return an object of class `ParseNode` which points
to the head of the corresponding parse tree (or null if the input string
does not follow the grammar). These instructions are enclosed in a try/catch
block to catch potential exceptions thrown during this process. The whole
process is done dynamically at runtime, without requiring any compiling.

Here is the parse tree returned for the second expression in the previous
example:

![Parse tree](Simple-Math.png?raw=true)

Compiling and Installing Bullwinkle                              {#install}
-----------------------------------

First make sure you have the following installed:

- The Java Development Kit (JDK) to compile. Bullwinkle was developed and
  tested on version 6 of the JDK, but it is probably safe to use any
  later version.
- [Ant](http://ant.apache.org) to automate the compilation and build process

Download the sources for Bullwinkle from
[GitHub](http://github.com/sylvainhalle/Bullwinkle) or clone the repository
using Git:

    git clone git@github.com:sylvainhalle/Bullwinkle.git

### Compiling

Compile the sources by simply typing:

    ant

This will produce a file called `bullwinkle.jar` in the folder. This
file is runnable and stand-alone, or can be used as a library, so it can be
moved around to the location of your choice.

In addition, the script generates in the `doc` folder the Javadoc
documentation for using Bullwinkle. This documentation is also embedded in
the JAR file. To show documentation in Eclipse, right-click on the jar,
click "Properties", then fill the Javadoc location (which is the JAR
itself).

### Testing

Bullwinkle can test itself by running:

    ant test

Unit tests are run with [jUnit](http://junit.org); a detailed report of
these tests in HTML format is availble in the folder `tests/junit`, which
is automatically created. Code coverage is also computed with
[JaCoCo](http://www.eclemma.org/jacoco/); a detailed report is available
in the folder `tests/coverage`.

### Coverity Scan

Bullwinkle uses [Coverity Scan](https://scan.coverity.com) for static analysis
of its source code and defect detection. Instructions for using Coverity Scan
locally are detailed [here](https://scan.coverity.com/download?tab=java). In
a nutshell, if Coverity Scan is installed, type the following:

    cov-build --dir cov-int ant compile

(Make sure to clean up the directory first by launching `ant clean`.)

Defining a grammar                                               {#grammar}
------------------

For Bullwinkle to work, the grammar must be
[LL(k)](http://en.wikipedia.org/wiki/LL_parser). Roughly, this means that
it must not contain a production rules of the form
`<S> := <S> something`. Trying to parse such a rule by recursive descent
causes an infinite recursion (which will throw a `ParseException` when the
maximum recursion depth is reached).

Defining a grammar can be done in two ways.

### Parsing a string

The first way is by parsing a character string (taken from a file or created
directly) that contains the grammar declaration. This format uses a fairly
intuitive syntax, as the example above has shown.

- Non-terminal symbols are enclosed in `<` and `>` and their names must not
  contain spaces.
- Rules are defined with `:=` and cases are separated by the pipe character.
- A rule can span multiple lines (any whitespace character after the first one
  is ignored, as in e.g. HTML) and must end by a semicolon.
- Terminal symbols are defined by typing them directly in a rule, or through
  regular expressions and begin with the `^` (hat) character. The example above
  shows both cases: the `+` symbol is typed directly into the rules, while the
  terminal symbol `<num>` is defined with a regex. **Look out:**
    - If a space needs to be used in the regular expression, it must be
      declared by using the regex sequence `\s`, and *not* by putting a space.
    - Beware not to put an extra space before the ending semicolon, or that
      space will count as part of the regex
    - Caveat emptor: a few corner cases are not covered at the moment, such as
      a regex that would contain a semicolon.
- The left-hand side symbol of the first rule found is assumed to be the start
  symbol. This can be overridden by calling method `setStartSymbol()` on an
  instance of the parser.
- Whitespace acts as a token separator, so there is no need to declare terminal
  tokens separately. This means that the rule `<num> + <num>` matches any string
  with a number, the symbol +, and another number, separated by any number of
  spaces, including none. This also means that writing `1+2` defines a *single*
  token that matches only the string "1+2". When declaring rules, tokens *must*
  be separated by a space. Writing `(<exp>)` is illegal and will throw an
  exception; one must write `( <exp> )` (note the spaces). However, since
  whitespace is ignored when parsing, this rule would still match the string
  "(1+1)".

Some symbols or sequences of symbols, such as `:=`, `|`, `<`, `>` and `;`,
have a special meaning and cannot be used directly inside terminal symbols
(note that this limitation applies only when parsing a grammar from a text
file). However, these symbols can be included by *escaping* them, i.e.
replacing them with their UTF-8 hex code.

- `|` can be replaced by `\u007c`
- `<` can be replaced by `\u003c`
- `<` can be replaced by `\u003e`
- `;` can be replaced by `\u003b`
- `:=` can be replaced by `\u003a\u003d`

The characters should appear as is (i.e. unescaped) in the string to parse.

### Building the rules manually

A second way of defining a grammar consists of assembling rules by creating
instances of objects programmatically. Roughly:

- A `BnfRule` contains a left-hand side that must be a `NonTerminalToken`, and
  a right-hand side containing multiple cases that are added through method
  `addAlternative()`.
- Each case is itself a `TokenString`, formed of multiple `TerminalToken`s and
  `NonTerminalToken`s which can be `add`ed. Terminal tokens include
  `NumberTerminalToken`, `StringTerminalToken` and `RegexTerminalToken`.
- `BnfRule`s are `add`ed to an instance of the `BnfParser`.

Using the parse tree                                                {#tree}
--------------------

Once a grammar has been loaded into an instance of `BnfParser`, the `parse()`
method is used to parse a given string and produce a parse tree (or null if the
string does not parse). This parse tree can then be explored in two ways:

1. In a manner similar to the DOM, by calling the `getChildren()` method of an
   instance of a `ParseNode` to get the list of its children (and so on,
   recursively);
2. Through the [Visitor design
   pattern](http://en.wikipedia.org/wiki/Visitor_pattern). In that case, one
   creates a class that implements the `ParseNodeVisitor` interface, and passes
   this visitor to the `ParseNode`'s `acceptPostfix()` or `acceptPrefix()`
   method, depending on the desired mode of traversal. The sample code shows an
   example of a visitor (class `GraphvizVisitor`), which produces a DOT file
   from the contents of the parse tree.

If your goal is to create some object out of the parse tree, consider using
the [object builder](#builder) class to simplify your work.

Partial parsing                                                  {#partial}
---------------

Partial parsing is a special mode where the input string is allowed
to contain non-terminal symbols. For example, consider the following grammar:

    <S> := <A> <B> c;
    <A> := foo;
    <B> := bar | <Z> d;
    <Z> := 0 | 1;

In partial parsing mode, the string `foo <B> c` is accepted by the
grammar. In this case, one of the leaf nodes of the resulting parse tree
is not a terminal symbol, but rather the non-terminal symbol &lt;B&gt;.

One particular use of partial parsing is the step-by-step verification of
partially formed strings. In the previous example, one might create
an input string by first writing

    <A> <B> c

This string can be checked to be valid by parsing it with partial parsing
enabled. Then non-terminal &lt;A&gt; can be expanded, yielding:

    foo <B> c

Again, one can check that this string is still syntactically valid. Non-terminal
&lt;B&gt; can be expanded to form `foo <Z> d c`, and then `foo 0 d c`.

To enable partial parsing, use the method `setPartialParsing()` of class
`BnfParser`.

Using object builders                                            {#builder}
---------------------

Many times, the goal of parsing an expression is to create some "object"
out of the resulting parse tree. The `ParseTreeObjectBuilder` class in
Bullwinkle simplifies the task of creating such objects.

Suppose for example that you created
objects to represent simple arithmetical expressions: there is one class
for `Add`, another for `Sub`(traction), another for plain `Num`bers, etc.
(See the `Examples` folder in the sources, where such classes are indeed
shown in `ArithExp.java`.) You can create and nest such objects
programmatically, for example to represent 10+(6-4):

    ArithExp a = new Add(new Num(10), new Sub(new Num(6), new Num(4));

Suppose you created a simple grammar to represent such expressions in
"forward" Polish notation, such as this:

    <exp> := <add> | <sub> | <num>;
    <add> := + <exp> <exp>;
    <sub> := - <exp> <exp>;
    <num> := ^[0-9]+;

Using such a grammar, the previous expression would be written as
`+ 10 - 6 4`. You would like to be able to instantiate `ArithExp` objects
from expressions following this syntax.

The `ParseTreeObjectBuilder` makes such a task simple. It performs a
*postfix* traversal of a parse tree and maintains a stack of arbitrary
objects. When visiting a parse node that corresponds to a non-terminal
token, such as &lt;foo&gt;, it looks for a method that handles this symbol.
This is done by adding an annotation `@Builds` to the method, as follows:

    @Builds(rule="<foo>")
    public void myMethod(Stack<Object> stack) { ...

The object builder calls this method, and passes it the current contents
of the object stack. It is up to this method to pop and push objects
from that stack, in order to recursively create the desired object at the
end. For example, in the grammar above, the code to handle token &lt;add&gt;
would look like:

    @Builds(rule="<add>")
    public void handleAdd(Stack<Object> stack) {
      ArithExp e2 = (ArithExp) stack.pop();
      ArithExp e1 = (ArithExp) stack.pop();
      stack.pop(); // To remove the "+" symbol
      stack.push(new Add(e1, e2));
    }

Since the builder traverses the tree in a postfix fashion, when a parse
node for &lt;add&gt; is visited, the object stack should already contain
the `ArithExp` objects created from its two operands. As a rule, each method
should pop from the stack as many objects as there are tokens in the corresponding case in the grammar. For example, the rule for &lt;add&gt;
has three tokens, and so the method handling &lt;add&gt; pops three objects.

The full example for this parser can be found in `BuildExampleStack` in the
`Examples` project.

As one can see, it is possible to create object builders that read
expressions in just a few lines of code. This can be even further simplified
using the `pop` and `clean` parameters. Instead of popping objects manually,
and pushing a new object back onto the stack, one can use the `pop` parameter
to ask for the object builder to already pop the appropriate number of
objects from the stack. The method for &lt;add&gt; would then become:

    @Builds(rule="<add>", pop=true)
    public ArithExp handleAdd(Object ... parts) {
      return new Add((ArithExp) parts[1], (ArithExp) parts[2]);
    }

Notice how this time, the method's arguments is an array of objects; in that
case, the array has three elements, corresponding to the three tokens of the
&lt;add&gt; rule. The first is the "+" symbol, and the other two are the
`ArithExp` objects created from the two sub-expressions. Similarly, instead of
pushing an object to the stack, the method simply returns it; the object builder
takes care of pushing it. By not accessing the contents of the stack directly,
it is harder to make mistakes.

As a further refinement, the `clean` option can remove from the arguments all
the objects that match terminal symbols in the corresponding rule. Consider a
grammar for infix arithmetical expressions, where parentheses are optional
around single numbers. This grammar would look like:

    <exp> := <add> ...
    <add> := <num> + <num> | ( <exp> ) + <num> | <num> + ( <exp> ) ...

This time, the rules for each operator must take into account whether any of
their operands is a number or a compound expression. The code handling
&lt;add&gt; would be more complex, as one would have to carefully pop an
element, check if it is a parenthesis, and if so, take care of popping the
matching parenthesis later on, etc. However, one can see that each case of
the rule has exactly two non-terminal tokens, and that both are `ArithExp`.
Using the `clean` option in conjunction with `pop`, the code for handling
&lt;add&gt; becomes identical as before:

    @Builds(rule="<add>", pop=true, clean=true)
    public ArithExp handleAdd(Object ... parts) {
      return new Add((ArithExp) parts[0], (ArithExp) parts[1]);
    }

The array indices become 0 and 1, since only the two `ArithExp` objects remain
as the arguments. Again, a full example can be found in the `Examples` folder,
inside `BuildExamplePop.java`.

Command-line usage                                                   {#cli}
------------------

The project comes with `bullwinkle.jar`, a file that can be used
either as a library inside a Java program (as described above), or as a
stand-alone command-line application. In that case, the application reads
the grammar definition from a file, a string to parse either from the
standard input or from another file, and writes to the standard output the
resulting parse tree. This tree can then be read by another application.

Command-line usage is as follows:

    java -jar bullwinkle.jar [options] grammar [file]

where `grammar` is the path to a file describing the grammar to use, and
`file` is an optional filename containing the string to be parsed. If no
file is given, the string will be read from the standard input.

Options are:

`-f x`, `--format x`
:  Output with format x. Supported values are `xml`, `txt` and `dot`. See
below for a description of these formats.

`-v x`
:  Set verbosity to level x (0 = no messages are printed).

Three output formats are supported directly.

### XML

In the XML format, non-terminal symbols are converted into tags, and
terminal tokens are surrounded by the `<token>` element. In the above
example, the expression `3 + 4` becomes the following XML structure:

    <parsetree>
      <exp>
        <add>
          <num>
            <token>
              3
            </token>
          </num>
          <token>
            +
          </token>
          <num>
            <token>
              4
            </token>
          </num>
        </add>
      </exp>
    </parsetree>

### Indented text

Indented text merely outputs terminal and non-terminal tokens, indenting
any subtree by one space, as follows:

    exp
     add
      num
       token
        3
      token
       +
      token
      num
       token
        4

### DOT

The DOT format produces a text file suitable for use with the
[Graphviz](http://www.graphviz.org) package. The picture shown earlier was
produced in this way.

Projects that use Bullwinkle                                       {#usage}
----------------------------

- [BeepBeep 3](https://liflab.github.io/beepbeep-3), an event stream query
  engine
- [Cornipickle](https://github.com/liflab/cornipickle), a web testing tool
- [SealTest](https://liflab.github.io/sealtest), a test sequence generator
- [SugarSMV](https://github.com/liflab/sugarsmv), a syntactical extension
  of the NuSMV model checker


About the author                                                   {#about}
----------------

Bullwinkle was written by [Sylvain Hallé](http://leduotang.ca/sylvain),
Associate Professor at [Université du Québec à Chicoutimi](http://www.uqac.ca),
Canada. It arose from the need to experiment with
various grammars without requiring compilation, as with classical parser
generators.