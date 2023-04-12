# Upgrading from 1.x

Tokenproducer 2.0.1 uses a new `TokenHandler2` interface which replaces the methods
`openGroup()` and `closeGroup()` with `leftParenthesis()`/`rightParenthesis()`
and others.

Using the new event methods may allow you to write clearer code, although you can
keep implementing the old 1.x `TokenHandler` interface, which provides a simpler
upgrade path.
