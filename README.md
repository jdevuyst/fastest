# Fastest

A Clojure library for non-deterministic and parallel branching.

This library contains several macros that take a variadic number of expressions for arguments. The expressions are evaluated in parallel and (at most) one of their resulting values is returned.

Fastest uses `future` to eagerly schedule tasks. When one of the expressions returns a satisfactory result, the (other) futures are canceled.

## Setup

To start, create a [Leiningen](http://leiningen.org) project and add the following dependency to `project.clj`:

![Clojars Project](http://clojars.org/fastest/latest-version.svg)

Next, load Fastest as follows:

```clojure
(require '[fastest :as f])
```

## Macros

```clojure
(f/any expr1 expr2 expr3 ...)
```
Returns the value of the expression that returns the first result. `(f/any)` expands to `nil`.

```clojure
(f/any* expr1 expr2 expr3 ...)
```
Waits for all the expressions to be evaluated and returns the value that was last returned. `(f/any*)` expands to `nil`.

```clojure
(f/or expr1 expr2 expr3 ...)
```
Returns the first truthy value returned by any of the expressions. Or, if that fails, the last falsy value. `(f/or)` expands to `nil`.

```clojure
(f/and expr1 expr2 expr3 ...)
```
Yields the first falsy value returned by any of the expressions. Or, if that fails, the first truthy value. `(f/and)` expands to `true`.

```clojure
(f/some pred expr1 expr2 expr3 ...)
```
Yields the first value `x` returned by any of the expressions such that `(pred x)` is truthy. If that fails, `f/some` returns the last value such that `(pred x)` is falsy. `(f/some pred)` expands to `nil`.

## Further information

Notice that expessions are not usually terminated forcibly when the futures are canceled. Every expression is responsible for checking `(Thread/interrupted)` if it wants to abort when its return value is no longer needed. The exception to this rule is when an expression calls certain blocking methods of Java, such as `Thread/sleep`, which raise an `InterruptedException` when `(Thread/interrupted)` holds.

## License

Copyright © 2014 Jonas De Vuyst

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
