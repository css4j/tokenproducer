/**
 * A small event parser (microparser).
 * <p>
 * This package provides two low-level parsers that work with three different
 * token handlers.
 * </p>
 * <p>
 * {@link TokenProducer} is a low-level token producer (parser) that can send
 * events to a {@link TokenHandler} or {@link TokenHandler2} handler. It should
 * be used when the error handling only produces runtime (unchecked) exceptions.
 * </p>
 * <p>
 * On the other hand, {@link TokenProducer3} deals with checked exceptions and
 * can only work with a {@link TokenHandler3} handler.
 * </p>
 * <p>
 * Most token handlers will report problems through error handlers and throw no
 * checked exceptions, in which case you should use {@link TokenProducer}
 * together with a {@link TokenHandler} or {@link TokenHandler2}. In other use
 * cases your handler may want to throw checked exceptions, and then you must
 * use {@link TokenProducer3} with a {@link TokenHandler3} instead.
 * </p>
 * <p>
 * As an example, this package includes the {@link CommentRemovalHandler} which
 * is to be used with {@link TokenProducer}.
 * </p>
 */
package io.sf.carte.uparser;
