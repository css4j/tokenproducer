/*

 Copyright (c) 2017-2025, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

/**
 * A {@link TokenHandler3} that has no checked exceptions, backwards-compatible
 * with {@code TokenProducer} 2.x.
 * <p>
 * Most token handlers will report problems through error handlers and produce
 * no checked exceptions, in which case you should use this handler together
 * with {@link TokenProducer}. In other use cases your handler may want to throw
 * checked exceptions, and then you must use {@link TokenProducer3} together
 * with {@link TokenHandler3} instead.
 * </p>
 */
public interface TokenHandler2 extends TokenHandler3<RuntimeException> {

	/**
	 * At the beginning of parsing, this method is called, passing the {@link TokenControl}
	 * object that can be used to fine-control the parsing.
	 *
	 * @param control
	 *            the <code>TokenControl</code> object in charge of parsing.
	 */
	@Override
	void tokenStart(TokenControl control);

	/**
	 * A word was found by the parser (includes connector punctuation).
	 *
	 * @param index
	 *            the index at which the word was found.
	 * @param word
	 *            the word.
	 */
	@Override
	void word(int index, CharSequence word);

	/**
	 * A separator (Zs, Zl and Zp unicode categories) was found.
	 *
	 * @param index
	 *            the index at which the separator was found.
	 * @param codePoint
	 *            the codepoint of the found separator.
	 */
	@Override
	void separator(int index, int codePoint);

	/**
	 * A quoted string was found by the parser.
	 *
	 * @param index
	 *            the index at which the quoted string was found.
	 * @param quoted
	 *            the quoted sequence of characters, without the quotes.
	 * @param quote
	 *            the quote character.
	 */
	@Override
	void quoted(int index, CharSequence quoted, int quote);

	/**
	 * A quoted string was found by the parser, and contains control characters.
	 *
	 * @param index
	 *            the index at which the quoted string was found.
	 * @param quoted
	 *            the quoted sequence of characters, without the quotes.
	 * @param quoteCp
	 *            the quote character codepoint.
	 */
	@Override
	void quotedWithControl(int index, CharSequence quoted, int quoteCp);

	/**
	 * An unescaped FF/LF/CR control was found while assembling a quoted string.
	 *
	 * @param index
	 *            the index at which the control was found.
	 * @param codePoint
	 *            the FF/LF/CR codepoint.
	 */
	@Override
	void quotedNewlineChar(int index, int codePoint);

	/**
	 * Called when the {@code (} codepoint is found.
	 *
	 * @param index
	 *            the index at which the codepoint was found.
	 */
	@Override
	void leftParenthesis(int index);

	/**
	 * Called when the {@code [} codepoint is found.
	 *
	 * @param index
	 *            the index at which the codepoint was found.
	 */
	@Override
	void leftSquareBracket(int index);

	/**
	 * Called when the <code>{</code> codepoint is found.
	 *
	 * @param index
	 *            the index at which the codepoint was found.
	 */
	@Override
	void leftCurlyBracket(int index);

	/**
	 * Called when the {@code )} codepoint is found.
	 *
	 * @param index
	 *            the index at which the codepoint was found.
	 */
	@Override
	void rightParenthesis(int index);

	/**
	 * Called when the {@code ]} codepoint is found.
	 *
	 * @param index
	 *            the index at which the codepoint was found.
	 */
	@Override
	void rightSquareBracket(int index);

	/**
	 * Called when the <code>}</code> codepoint is found.
	 *
	 * @param index
	 *            the index at which the codepoint was found.
	 */
	@Override
	void rightCurlyBracket(int index);

	/**
	 * Called when start punctuation (Ps) codepoints are found (except characters
	 * handled by {@link #leftCurlyBracket(int)}, {@link #leftParenthesis(int)} and
	 * {@link #leftSquareBracket(int)}).
	 *
	 * @param index
	 *            the index at which the codepoint was found.
	 * @param codePoint
	 *            the found codepoint.
	 */
	@Override
	default void startPunctuation(int index, int codePoint) {
		character(index, codePoint);
	}

	/**
	 * Called when end punctuation (Pe) codepoints are found (except characters
	 * handled by {@link #rightCurlyBracket(int)}, {@link #rightParenthesis(int)}
	 * and {@link #rightSquareBracket(int)}).
	 *
	 * @param index
	 *            the index at which the codepoint was found.
	 * @param codePoint
	 *            the found codepoint.
	 */
	@Override
	default void endPunctuation(int index, int codePoint) {
		character(index, codePoint);
	}

	/**
	 * Other characters including punctuation (excluding connector punctuation) and symbols
	 * (Sc, Sm and Sk unicode categories) was found, that was not one of the non-alphanumeric
	 * characters allowed in words.
	 * <p>
	 * Symbols in So category are considered part of words and won't be handled by this
	 * method.
	 *
	 * @param index
	 *            the index at which the punctuation was found.
	 * @param codePoint
	 *            the codepoint of the found punctuation.
	 */
	@Override
	void character(int index, int codePoint);

	/**
	 * A codepoint preceded with a backslash was found outside of quoted text.
	 *
	 * @param index
	 *            the index at which the escaped codepoint was found.
	 * @param codePoint
	 *            the escaped codepoint.
	 */
	@Override
	void escaped(int index, int codePoint);

	/**
	 * A control character codepoint was found.
	 *
	 * @param index
	 *            the index at which the control codepoint was found.
	 * @param codePoint
	 *            the control codepoint.
	 */
	@Override
	void control(int index, int codePoint);

	/**
	 * A commented string was found by the parser.
	 *
	 * @param index
	 *            the index at which the commented string was found.
	 * @param commentType
	 *            the type of comment.
	 * @param comment
	 *            the commented string.
	 */
	@Override
	void commented(int index, int commentType, String comment);

	/**
	 * The stream that was being parsed reached its end.
	 *
	 * @param len
	 *            the length of the processed stream.
	 */
	@Override
	void endOfStream(int len);

	/**
	 * An error was found while parsing.
	 * <p>
	 * Something was found that broke the assumptions made by the parser, like an escape
	 * character at the end of the stream or an unmatched quote.
	 *
	 * @param index
	 *            the index at which the error was found.
	 * @param errCode
	 *            the error code.
	 * @param context
	 *            a context sequence. If a string was parsed, it will contain up to 16
	 *            characters before and after the error.
	 */
	@Override
	void error(int index, byte errCode, CharSequence context);
}
