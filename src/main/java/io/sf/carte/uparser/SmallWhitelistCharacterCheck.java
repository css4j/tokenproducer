/*

 Copyright (c) 2017-2025, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

import io.sf.carte.uparser.TokenProducer3.CharacterCheck;
import io.sf.carte.uparser.TokenProducer3.SequenceParser;

/**
 * A character check that whitelists a very small array of codepoints.
 */
class SmallWhitelistCharacterCheck implements CharacterCheck {

	private final int[] allowInWords;

	/**
	 * Constructor.
	 * <p>
	 * The allowed characters shall be checked one by one in the array order. If the
	 * array is large please use {@link WhitelistCharacterCheck} instead.
	 * </p>
	 * 
	 * @param allowInWords an array containing the characters that are allowed in
	 *                     words. Characters expected to be more frequent should be
	 *                     located at a lower index.
	 */
	public SmallWhitelistCharacterCheck(int[] allowInWords) {
		super();
		this.allowInWords = allowInWords;
	}

	@Override
	public boolean isAllowedCharacter(int codePoint,
			SequenceParser<? extends Exception> parser) {
		/*
		 * In general, it would be more efficient to test for:
		 * 
		 *   Arrays.binarySearch(allowInWords, codePoint) >= 0
		 * 
		 * but the typical use cases for this class involve an array of length two.
		 */
		for (int allowInWord : allowInWords) {
			if (codePoint == allowInWord) {
				return true;
			}
		}
		return false;
	}

}
