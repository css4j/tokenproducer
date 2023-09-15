/*

 Copyright (c) 2017-2023, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

/**
 * A {@link TokenHandler2} that is backwards-compatible with
 * {@code TokenProducer} 1.x.
 * <p>
 * You may consider using {@link TokenHandler2} or {@link TokenHandler3} instead.
 * </p>
 */
public interface TokenHandler extends TokenHandler2 {

	@Override
	default void leftParenthesis(int index) {
		openGroup(index, TokenProducer3.CHAR_LEFT_PAREN);
	}

	@Override
	default void leftSquareBracket(int index) {
		openGroup(index, TokenProducer3.CHAR_LEFT_SQ_BRACKET);
	}

	@Override
	default void leftCurlyBracket(int index) {
		openGroup(index, TokenProducer3.CHAR_LEFT_CURLY_BRACKET);
	}

	@Override
	default void rightParenthesis(int index) {
		closeGroup(index, TokenProducer3.CHAR_RIGHT_PAREN);
	}

	@Override
	default void rightSquareBracket(int index) {
		closeGroup(index, TokenProducer3.CHAR_RIGHT_SQ_BRACKET);
	}

	@Override
	default void rightCurlyBracket(int index) {
		closeGroup(index, TokenProducer3.CHAR_RIGHT_CURLY_BRACKET);
	}

	/**
	 * Called when one of these codepoints is found: (, [, {
	 *
	 * @param index
	 *            the index at which the codepoint was found.
	 * @param codePoint
	 *            the found codepoint.
	 */
	void openGroup(int index, int codePoint);

	/**
	 * Called when one of these codepoints is found: ), ], }
	 *
	 * @param index
	 *            the index at which the codepoint was found.
	 * @param codePoint
	 *            the found codepoint.
	 */
	void closeGroup(int index, int codePoint);

}
