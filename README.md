
# Bullwinkle

A parser for [Backus-Naur Form](http://en.wikipedia.org/wiki/Backus-Naur_form).
[Original code](https://github.com/sylvainhalle/Bullwinkle) written by [Sylvain Hallé](http://leduotang.ca/sylvain).
The original readme can be found [here]().

My main [reason](https://github.com/sylvainhalle/Bullwinkle/issues/14) for rewriting this library is to get it to parse [Java BNF](https://cs.au.dk/~amoeller/RegAut/JavaBNF.html).
This code doesn't do that yet.
I'm still rewriting things in the hopes of discovering how it works.

It would be nice if I can fix a few of the [issues](https://github.com/sylvainhalle/Bullwinkle/issues) in the original while I'm working on it.

## License

Apache 2.0.

## Changes

1. Converted to Maven project
2. Massive refactor
3. Removed copyright / license notice at head of each file

This is a violation of the license. 
Sylvain if you mind I can put them back or delete the whole project.
But I would prefer if you [release the project](https://github.com/sylvainhalle/Bullwinkle/issues/13) under the MIT license.

4. Broke many tests

Didn't mean to do this, but it was just faster.
I plan to fix all the tests.

5. Added jcli as a dependency

The project is now a fatjar.
This means the code can't be used as a library anymore.
This is not the plan.
When I get there I'll separate the Cli tool from the main library.

6. BNF rules must be on single line

The original code kept reading the input until it found a semicolon.
The semicolon would be the end of the BNF rule.
Java uses semicolons, so that implementation wouldn't work for me.

The code now assumes that each BNF rule is on its own line.

## Usage

Don't know.
The code isn't passing tests and I'm not done refactoring.

I plan to put a library in Maven Central.
And maybe make some binary releases of a CLI.

Of course you can fork the code.
