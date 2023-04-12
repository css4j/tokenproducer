# Upgrading from 1.x

Tokenproducer 2.0 replaces the methods `openGroup()` and `closeGroup()` with
`leftParenthesis()`/`rightParenthesis()` and others. Using the new event methods
may allow you to write clearer code, although you can also have your old
`TokenHandler` implementation inherit from the new `LegacyTokenHandler`, which
is compatible with the 1.x API and provides a simpler upgrade path.
