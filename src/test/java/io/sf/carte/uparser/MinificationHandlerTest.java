/*

 Copyright (c) 2017-2025, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class MinificationHandlerTest {

	@Test
	public void testParse() {
		String text = "<html><!-- XML Comment 1 --><head>\t<style>/* CSS comment 1 */"
				+ "\\1/* CSS comment 2 */</style>\n<!-- XML Comment 2 --></head><body>*"
				+ "<p>\"hi\" 'ho\t' ()[]{}</p></body></html>";

		String removed = removeComments(text);

		assertEquals("<html> <head> <style> \\1 </style> </head><body>*<p>\"hi\" 'ho\t' ()[]{}</p>"
				+ "</body></html>", removed);
	}

	private String removeComments(String text) {
		final String[] opening = { "/*", "<!--" };
		final String[] closing = { "*/", "-->" };
		MinificationHandler handler = new MinificationHandler(text.length());
		TokenProducer tp = new TokenProducer(handler);
		tp.setHandleAllSeparators(false);
		try {
			tp.parseMultiComment(new StringReader(text), opening, closing);
		} catch (IOException e) {
		}
		return handler.getBuffer().toString();
	}

}
