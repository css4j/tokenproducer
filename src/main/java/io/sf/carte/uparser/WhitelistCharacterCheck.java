/*

 Copyright (c) 2017-2025, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

import java.util.Arrays;

import io.sf.carte.uparser.TokenProducer3.CharacterCheck;
import io.sf.carte.uparser.TokenProducer3.SequenceParser;

/**
 * A character check that whitelists an array of codepoints.
 */
class WhitelistCharacterCheck implements CharacterCheck {

	private final int[] allowInWords;

	/**
	 * Constructor.
	 * 
	 * @param allowInWords a sorted array containing the characters that are allowed
	 *                     in words.
	 */
	public WhitelistCharacterCheck(int[] allowInWords) {
		super();
		this.allowInWords = allowInWords;
	}

	@Override
	public boolean isAllowedCharacter(int codePoint,
			SequenceParser<? extends Exception> parser) {
		return Arrays.binarySearch(allowInWords, codePoint) >= 0;
	}

}
