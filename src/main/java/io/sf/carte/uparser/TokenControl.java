/*

 Copyright (c) 2017-2025, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

import java.io.IOException;

/**
 * Offers methods to control the parsing process by the handler.
 */
public interface TokenControl {

	/**
	 * Enable the handling of all types of comments.
	 */
	void enableAllComments();

	/**
	 * If set, quoted strings ending with an unescaped newline (instead of the closing quote)
	 * are processed through the relevant <code>quoted</code> method, albeit an error is
	 * reported in any case. Otherwise, only the error is reported.
	 * <p>
	 * It is set to <code>false</code> by default.
	 * 
	 * @param accept
	 *            true to process quoted strings that ends with an unescaped newline, false
	 *            otherwise.
	 */
	void setAcceptNewlineEndingQuote(boolean accept);

	/**
	 * If set, quoted strings ending with an EOF (End Of File). are processed through the
	 * relevant <code>quoted</code> method, albeit an error is reported in any case.
	 * Otherwise, only the error is reported.
	 * <p>
	 * It is set to <code>false</code> by default.
	 * 
	 * @param accept
	 *            true to process quoted strings that ends with an EOF, <code>false</code> otherwise.
	 */
	void setAcceptEofEndingQuoted(boolean accept);

	/**
	 * Gives the next code point in the stream, bypassing the general processing for
	 * that one.
	 * 
	 * @return the next code point, or -1 if the end of the stream was reached.
	 * 
	 * @throws IOException if an I/O problem occurs.
	 */
	int skipNextCodepoint() throws IOException;

	/**
	 * Tells whether the associated stream supports the {@code mark()} operation.
	 * 
	 * @return {@code true} if the associated stream supports the {@code mark()}
	 *         operation.
	 */
	default boolean markStreamSupported() {
		return false;
	}

	/**
	 * Calls {@link java.io.Reader#mark(int) mark(int)} on the stream reader.
	 * 
	 * @param readAheadLimit Limit on the number of characters that may be read
	 *                       while still preserving the mark. After reading this
	 *                       many characters, attempting to reset the stream may
	 *                       fail.
	 * @throws IOException if the stream does not support mark(), or if some other
	 *                     I/O error occurs.
	 */
	default void markStream(int readAheadLimit) throws IOException {
		throw new IOException("mark() not supported.");
	}

	/**
	 * Calls {@link java.io.Reader#reset() reset()} on the stream reader.
	 * 
	 * @throws IOException if the stream has not been marked, or if the mark has
	 *                     been invalidated, or if the stream does not support
	 *                     reset(), or if some other I/O error occurs.
	 */
	default void resetStream() throws IOException {
		throw new IOException("reset() not supported.");
	}

	/**
	 * Get the current content handler.
	 * 
	 * @return the {@code ContentHandler}.
	 */
	ContentHandler<?> getContentHandler();

	/**
	 * Set a new {@code ContentHandler}.
	 * 
	 * @param handler
	 *            the new {@code ContentHandler}.
	 */
	void setContentHandler(ContentHandler<?> handler);

	/**
	 * Get the current control handler.
	 * 
	 * @return the {@code ControlHandler}.
	 */
	ControlHandler<?> getControlHandler();

	/**
	 * Set a new {@code ControlHandler}.
	 * 
	 * @param handler
	 *            the new {@code ControlHandler}.
	 */
	void setControlHandler(ControlHandler<?> handler);

	/**
	 * Get the current error handler.
	 * 
	 * @return the {@code TokenErrorHandler}.
	 */
	TokenErrorHandler<?> getErrorHandler();

	/**
	 * Set a new {@code TokenErrorHandler}.
	 * 
	 * @param handler
	 *            the new {@code TokenErrorHandler}.
	 */
	void setErrorHandler(TokenErrorHandler<?> handler);

	/**
	 * Get the current content handler, assuming that it implements the
	 * {@code TokenHandler3} interface.
	 * <p>
	 * This method is present only for backwards compatibility. Please avoid it, as
	 * the content handler may or may not implement the legacy {@code TokenHandler3}
	 * interface.
	 * </p>
	 * 
	 * @return the {@code TokenHandler}.
	 * @deprecated See {@link #getContentHandler()}, {@link #getControlHandler()},
	 *             {@link #getErrorHandler()}
	 */
	@Deprecated
	default TokenHandler3<?> getTokenHandler() {
		return (TokenHandler3<?>) getContentHandler();
	}

	/**
	 * Set all the handlers with a new {@code TokenHandler3}.
	 * 
	 * @param handler
	 *            the new {@code TokenHandler3}.
	 */
	void setTokenHandler(TokenHandler3<?> handler);

	/**
	 * Disable the handling of all types of comments.
	 */
	void disableAllComments();

	/**
	 * Enable the handling of comments of the given type.
	 * 
	 * @param type
	 *            the type of comment to enable. If the type does not exist, implementations
	 *            may or may not throw an exception, depending on the intended coupling with
	 *            the <code>TokenHandler</code>.
	 */
	void enableComments(int type);

	/**
	 * Disable the handling of comments of the given type.
	 * 
	 * @param type
	 *            the type of comment to disable. If the type does not exist, implementations
	 *            may or may not throw an exception, depending on the intended coupling with
	 *            the <code>TokenHandler</code>.
	 */
	void disableComments(int type);

	/**
	 * Enable/disable the external handling of locations.
	 * <p>
	 * If a binary stream is being processed, it should be enabled. It is enabled by
	 * default.
	 * </p>
	 * 
	 * @param enable if {@code false}, this parser will take care of handling
	 *               CR/LF/FF control characters and location housekeeping.
	 *               Otherwise, all control characters are sent to the handler
	 *               without further processing.
	 */
	void setExternalLocationHandling(boolean enable);

	/**
	 * Set the current parsing location to the given locator.
	 * <p>
	 * Will only work correctly if {@code externalLocationHandling} is disabled.
	 * </p>
	 * 
	 * @param locator the locator.
	 * @see #setExternalLocationHandling(boolean)
	 */
	void setLocationTo(LocatorAccess locator);

	/**
	 * Set the parsing location given by {@code index} to the given locator.
	 * <p>
	 * Will only work correctly if {@code externalLocationHandling} is disabled and
	 * the {@code index} points at the same line that the current parsing location.
	 * </p>
	 * 
	 * @param locator the locator.
	 * @param index   the index at which the location is to be set.
	 * @see #setExternalLocationHandling(boolean)
	 */
	void setLocationTo(LocatorAccess locator, int index);

}
