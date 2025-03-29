/*

 Copyright (c) 2017-2025, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

/**
 * A simple parser that produces tokens from a String or Reader, and processes
 * them through a user-provided handler.
 * <p>
 * This parser is intended to deal with handlers that produce only runtime
 * (unchecked) exceptions. If your use case requires dealing with checked
 * exceptions, please use {@link TokenProducer3} instead.
 * </p>
 * <p>
 * <strong>Tokenization Overview</strong>
 * </p>
 * <p>
 * The tokens produced are:
 * <ul>
 * <li>Words. Contains letters, digits, format characters (Unicode Cf),
 * connector punctuations, certain symbols and any codepoint in
 * <code>allowInWords</code> or accepted by the supplied
 * <code>CharacterCheck</code> object.
 * <li>Quoted text (within single and double quotes).
 * <li>Grouping characters: {}[]().
 * <li>Separators.
 * <li>Escaped characters: anything after a backslash, unless it is within a
 * quoted text.
 * <li>Control characters.
 * <li>Other characters.
 * <li>Comments. (if a comment-supporting method is used)
 * </ul>
 * <p>
 * A moderate level of control of the parsing can be achieved with a
 * {@link TokenControl} object.
 * </p>
 * <p>
 * The constructors that do not take a {@code characterCountLimit} argument
 * process {@code Reader} streams of up to one Gigabyte (0x40000000) in size,
 * throwing a {@link SecurityException} if they exceed that limit. Use the other
 * constructors if you need to process larger files (or want smaller limits
 * being enforced).
 * </p>
 */
public class TokenProducer extends TokenProducer3<RuntimeException> {

	/**
	 * Instantiate a <code>TokenProducer</code> object with the given handler.
	 * 
	 * @param handler
	 *            the token handler.
	 */
	public TokenProducer(TokenHandler2 handler) {
		super(handler);
	}

	/**
	 * Instantiate a <code>TokenProducer</code> object with the given handler and
	 * processing limit.
	 * 
	 * @param handler
	 *            the token handler.
	 * @param characterCountLimit
	 *            the character count limit.
	 */
	public TokenProducer(TokenHandler2 handler, int characterCountLimit) {
		super(handler, characterCountLimit);
	}

	/**
	 * Instantiate a <code>TokenProducer</code> object with the given handler and
	 * <code>CharacterCheck</code>.
	 * 
	 * @param handler
	 *            the token handler.
	 * @param charCheck
	 *            the character checker object.
	 */
	public TokenProducer(TokenHandler2 handler, CharacterCheck charCheck) {
		super(handler, charCheck);
	}

	/**
	 * Instantiate a <code>TokenProducer</code> object with the given handler and
	 * <code>CharacterCheck</code>.
	 * 
	 * @param handler
	 *            the token handler.
	 * @param charCheck
	 *            the character checker object.
	 * @param characterCountLimit
	 *            the character count limit.
	 */
	public TokenProducer(TokenHandler2 handler, CharacterCheck charCheck, int characterCountLimit) {
		super(handler, charCheck, characterCountLimit);
	}

	/**
	 * Construct a <code>TokenProducer</code> object with the given handler and
	 * an array of codepoints allowed in words.
	 * 
	 * @param handler
	 *            the token handler.
	 * @param allowInWords
	 *            the array of codepoints allowed in words.
	 */
	public TokenProducer(TokenHandler2 handler, int[] allowInWords) {
		super(handler, allowInWords);
	}

	/**
	 * Construct a <code>TokenProducer</code> object with the given handler and
	 * an array of codepoints allowed in words.
	 * 
	 * @param handler
	 *            the token handler.
	 * @param allowInWords
	 *            the array of codepoints allowed in words.
	 * @param characterCountLimit
	 *            the character count limit.
	 */
	public TokenProducer(TokenHandler2 handler, int[] allowInWords, int characterCountLimit) {
		super(handler, allowInWords, characterCountLimit);
	}

	/**
	 * Instantiate a <code>TokenProducer</code> object with the given
	 * <code>CharacterCheck</code> and character count limit.
	 * 
	 * @param charCheck
	 *            the character checker object.
	 * @param characterCountLimit
	 *            the character count limit.
	 */
	public TokenProducer(CharacterCheck charCheck, int characterCountLimit) {
		super(charCheck, characterCountLimit);
	}

}
