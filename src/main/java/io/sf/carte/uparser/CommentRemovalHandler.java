/*

 Copyright (c) 2017-2025, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

/**
 * A handler that removes comments.
 * <p>
 * Example:
 * </p>
 * 
 * <pre><code>
 * String removeComments(String text) {
 *     String[] opening = { "/{@literal *}", "&lt;!--" };
 *     String[] closing = { "{@literal *}/", "--&gt;" };
 *     CommentRemovalHandler handler = new CommentRemovalHandler(text.length());
 *     TokenProducer tp = new TokenProducer(handler);
 *     try {
 *         tp.parseMultiComment(new StringReader(text), opening, closing);
 *     } catch (IOException e) {
 *     }
 *     return handler.getBuffer().toString();
 * }
 * </code></pre>
 */
public class CommentRemovalHandler implements TokenHandler2 {

	private final StringBuilder buffer;

	/**
	 * Construct the handler with the given initial buffer size.
	 * 
	 * @param bufSize the initial buffer size.
	 */
	public CommentRemovalHandler(int bufSize) {
		this(new StringBuilder(bufSize));
	}

	/**
	 * Construct the handler with the given buffer.
	 * 
	 * @param buffer the buffer.
	 */
	public CommentRemovalHandler(StringBuilder buffer) {
		super();
		this.buffer = buffer;
	}

	/**
	 * Get the buffer.
	 * 
	 * @return the buffer.
	 */
	public StringBuilder getBuffer() {
		return buffer;
	}

	@Override
	public void tokenStart(TokenControl control) {
	}

	@Override
	public void word(int index, CharSequence word) {
		buffer.append(word);
	}

	@Override
	public void separator(int index, int codePoint) {
		buffer.appendCodePoint(codePoint);
	}

	@Override
	public void quoted(int index, CharSequence quoted, int quoteCp) {
		char[] quote = Character.toChars(quoteCp);
		buffer.append(quote);
		buffer.append(quoted);
		buffer.append(quote);
	}

	@Override
	public void quotedWithControl(int index, CharSequence quoted, int quoteCp) {
		quoted(index, quoted, quoteCp);
	}

	@Override
	public void quotedNewlineChar(int index, int codePoint) {
		buffer.appendCodePoint(codePoint);
	}

	@Override
	public void leftParenthesis(int index) {
		buffer.append('(');
	}

	@Override
	public void leftSquareBracket(int index) {
		buffer.append('[');
	}

	@Override
	public void leftCurlyBracket(int index) {
		buffer.append('{');
	}

	@Override
	public void rightParenthesis(int index) {
		buffer.append(')');
	}

	@Override
	public void rightSquareBracket(int index) {
		buffer.append(']');
	}

	@Override
	public void rightCurlyBracket(int index) {
		buffer.append('}');
	}

	@Override
	public void character(int index, int codePoint) {
		buffer.appendCodePoint(codePoint);
	}

	@Override
	public void escaped(int index, int codePoint) {
		buffer.append('\\').appendCodePoint(codePoint);
	}

	@Override
	public void control(int index, int codePoint) {
		buffer.appendCodePoint(codePoint);
	}

	@Override
	public void commented(int index, int commentType, String comment) {
	}

	@Override
	public void endOfStream(int len) {
	}

	@Override
	public void error(int index, byte errCode, CharSequence context) {
	}

}
