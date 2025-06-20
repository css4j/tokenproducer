/*

 Copyright (c) 2017-2025, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

/**
 * A handler that minifies by removing comments and consecutive whitespace.
 * <p>
 * Example:
 * </p>
 * 
 * <pre><code>
 * String minify(String text) {
 *     String[] opening = { "/{@literal *}", "&lt;!--" };
 *     String[] closing = { "{@literal *}/", "--&gt;" };
 *     MinificationHandler handler = new MinificationHandler(text.length());
 *     TokenProducer tp = new TokenProducer(handler);
 *     tp.setHandleAllSeparators(false);
 *     try {
 *         tp.parseMultiComment(new StringReader(text), opening, closing);
 *     } catch (IOException e) {
 *     }
 *     return handler.getBuffer().toString();
 * }
 * </code></pre>
 */
public class MinificationHandler extends CommentRemovalHandler {

	/**
	 * Construct the handler with the given initial buffer size.
	 * 
	 * @param bufSize the initial buffer size.
	 */
	public MinificationHandler(int bufSize) {
		super(bufSize);
	}

	/**
	 * Construct the handler with the given buffer.
	 * 
	 * @param buffer the buffer.
	 */
	public MinificationHandler(StringBuilder buffer) {
		super(buffer);
	}

	@Override
	public void separator(int index, int codePoint) {
		if (getPreviousCodepoint() != 32) {
			super.separator(index, codePoint);
		}
	}

	@Override
	public void control(int index, int codePoint) {
		separator(index, 32);
	}

}
