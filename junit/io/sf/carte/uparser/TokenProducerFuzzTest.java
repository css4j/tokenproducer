/*

 Copyright (c) 2017-2023, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Tag;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;

/**
 * Use <a href="https://github.com/CodeIntelligenceTesting/jazzer">Jazzer</a> to
 * perform a fuzz test.
 * <p>
 * To perform actual fuzzing, the environment variable {@code JAZZER_FUZZ}
 * should be set to a non-empty value, and the inputs directory should be
 * created.
 * </p>
 * <p>
 * See also <a href=
 * "https://github.com/CodeIntelligenceTesting/cifuzz"><code>cifuzz</code></a>.
 * </p>
 * <p>
 * Important note: in the JUnit Runner configuration(s) of your IDE (or build
 * system) you should exclude the {@code Fuzz} tag if you do not want to run
 * this test there.
 * </p>
 */
@Tag("Fuzz")
class TokenProducerFuzzTest {

	@FuzzTest
	void fuzzStringTest(FuzzedDataProvider data) {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler handler = new TestTokenHandler();
		TokenProducer tp = new TokenProducer(handler, allowInWords);

		String input = data.consumeRemainingAsString();
		tp.parse(input, "/*", "*/");
	}

	@FuzzTest
	void fuzzReaderTest(FuzzedDataProvider data) throws IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler handler = new TestTokenHandler();
		TokenProducer tp = new TokenProducer(handler, allowInWords);

		String input = data.consumeRemainingAsString();
		tp.parse(new StringReader(input), "/*", "*/");
	}

}
