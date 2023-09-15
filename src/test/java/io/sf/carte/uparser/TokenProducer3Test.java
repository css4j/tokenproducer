/*

 Copyright (c) 2017-2023, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

public class TokenProducer3Test {

	@Test
	public void testParse() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("(display: table-cell) and (display: list-item)", "/*", "*/");
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("display", handler.words.get(3));
		assertEquals("list-item", handler.words.get(4));
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertNotNull(handler.control);
	}

	@Test
	public void testParse2() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("(display: table-cell) and (display: list-item)-", "/*", "*/");
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("display", handler.words.get(3));
		assertEquals("list-item", handler.words.get(4));
		assertEquals("(:)(:)-", handler.punctbuffer.toString());
		assertEquals(46, handler.lastCharacterIndex);
	}

	@Test
	public void testParse2Reader() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("(display: table-cell) and (display: list-item)-"), "/*", "*/");
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("display", handler.words.get(3));
		assertEquals("list-item", handler.words.get(4));
		assertEquals("(:)(:)-", handler.punctbuffer.toString());
		assertEquals(46, handler.lastCharacterIndex);
	}

	@Test
	public void testParse2ReaderMultiComment() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("(display: table-cell) and (display: list-item)-");
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("display", handler.words.get(3));
		assertEquals("list-item", handler.words.get(4));
		assertEquals("(:)(:)-", handler.punctbuffer.toString());
		assertEquals(46, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderSizeLimit() throws TimeoutException, IOException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords, 20);
		try {
			tp.parse(new StringReader("(display: table-cell) and (display: list-item)"), "<!--", "-->");
			fail("Must throw exception.");
		} catch (SecurityException e) {
		}
	}

	@Test
	public void testParseReaderSizeLimitSingleWord() throws TimeoutException, IOException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords, 20);
		try {
			tp.parse(new StringReader("aaaaaaaaaaaaaaaaaaaaaaaaaaa"), "<!--", "-->");
			fail("Must throw exception.");
		} catch (SecurityException e) {
		}
	}

	@Test
	public void testParseReaderSizeLimitComment() throws TimeoutException, IOException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords, 20);
		try {
			tp.parse(new StringReader("<!ELEMENT> <!-- comment -->"), "<!--", "-->");
			fail("Must throw exception.");
		} catch (SecurityException e) {
		}
	}

	@Test
	public void testParseReaderSizeLimitQuoted() throws TimeoutException, IOException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords, 20);
		try {
			tp.parse(new StringReader("content: 'foo                  bar'"), "<!--", "-->");
			fail("Must throw exception.");
		} catch (SecurityException e) {
		}
	}

	@Test
	public void testParseNL() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("only\nscreen", "/*", "*/");
		assertEquals("only", handler.words.get(0));
		assertEquals("screen", handler.words.get(1));
		assertEquals(2, handler.words.size());
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(1, handler.control10);
	}

	@Test
	public void testParseSimple() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("f", "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(0, handler.lastWordIndex);
	}

	@Test
	public void testParseReaderSimple() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("f"), "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("", handler.punctbuffer.toString());
	}

	@Test
	public void testParseReaderMultiCommentSimple() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(new StringReader("f"), opening, closing);
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(0, handler.lastWordIndex);
	}

	@Test
	public void testParseSimpleNoComment() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("f");
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(0, handler.lastWordIndex);
	}

	@Test
	public void testParseReaderSimpleNoComment() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("f"));
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(0, handler.lastWordIndex);
	}

	@Test
	public void testParseSimple2() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("@f", "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("@", handler.punctbuffer.toString());
	}

	@Test
	public void testParseReaderSimple2() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("@f"), "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("@", handler.punctbuffer.toString());
	}

	@Test
	public void testParseReaderMultiCommentSimple2() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(new StringReader("@f"), opening, closing);
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("@", handler.punctbuffer.toString());
	}

	@Test
	public void testParseSimple2NoComment() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("@f");
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("@", handler.punctbuffer.toString());
	}

	@Test
	public void testParseReaderNoCommentSimple2() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("@f"));
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("@", handler.punctbuffer.toString());
	}

	@Test
	public void testParseSimple3() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("f@", "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("@", handler.punctbuffer.toString());
	}

	@Test
	public void testParseReaderSimple3() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("f@"), "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("@", handler.punctbuffer.toString());
	}

	@Test
	public void testParseNoCommentSimple3() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("f@");
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("@", handler.punctbuffer.toString());
	}

	@Test
	public void testParseReaderNoCommentSimple3() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("f@"));
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("@", handler.punctbuffer.toString());
	}

	@Test
	public void testParseSimple4() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("f g", "/*", "*/");
		assertEquals(2, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("g", handler.words.get(1));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(" 1", handler.sepbuffer.toString());
	}

	@Test
	public void testParseNoCommentSimple4() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("f g");
		assertEquals(2, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("g", handler.words.get(1));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(" 1", handler.sepbuffer.toString());
	}

	@Test
	public void testParseReaderNoCommentSimple4() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("f g"));
		assertEquals(2, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("g", handler.words.get(1));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(" 1", handler.sepbuffer.toString());
	}

	@Test
	public void testParseSimple5() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("f:g", "/*", "*/");
		assertEquals(2, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("g", handler.words.get(1));
		assertEquals(":", handler.punctbuffer.toString());
		assertEquals(2, handler.lastWordIndex);
	}

	@Test
	public void testParseNoCommentSimple5() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("f:g");
		assertEquals(2, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("g", handler.words.get(1));
		assertEquals(":", handler.punctbuffer.toString());
		assertEquals(2, handler.lastWordIndex);
	}

	@Test
	public void testParseReaderNoCommentSimple5() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("f:g"));
		assertEquals(2, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("g", handler.words.get(1));
		assertEquals(":", handler.punctbuffer.toString());
		assertEquals(2, handler.lastWordIndex);
	}

	@Test
	public void testParseSimple6() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("f-g:", "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("f-g", handler.words.get(0));
		assertEquals(":", handler.punctbuffer.toString());
		assertEquals(0, handler.lastWordIndex);
	}

	@Test
	public void testParseSimple7() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("f/", "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("/", handler.punctbuffer.toString());
		assertEquals(0, handler.lastWordIndex);
		assertEquals(1, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderSimple7() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("f/"), "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("/", handler.punctbuffer.toString());
		assertEquals(0, handler.lastWordIndex);
		assertEquals(1, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderMultiCommentSimple7() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("f/");
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals(1, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("/", handler.punctbuffer.toString());
		assertEquals(0, handler.lastWordIndex);
		assertEquals(1, handler.lastCharacterIndex);
	}

	@Test
	public void testParseSimple8() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("-/", "/*", "*/");
		assertEquals(0, handler.words.size());
		assertEquals("-/", handler.punctbuffer.toString());
		assertEquals(1, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderSimple8() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("-/"), "/*", "*/");
		assertEquals(0, handler.words.size());
		assertEquals("-/", handler.punctbuffer.toString());
		assertEquals(1, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderMultiCommentSimple8() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("-/");
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals(0, handler.words.size());
		assertEquals("-/", handler.punctbuffer.toString());
		assertEquals(1, handler.lastCharacterIndex);
	}

	@Test
	public void testParseNoCommentSimple8() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("-/");
		assertEquals(0, handler.words.size());
		assertEquals("-/", handler.punctbuffer.toString());
		assertEquals(1, handler.lastCharacterIndex);
	}

	@Test
	public void testParseSimple9() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("-/**/", "/*", "*/");
		assertEquals(0, handler.words.size());
		assertEquals(1, handler.comments.size());
		assertEquals("-", handler.punctbuffer.toString());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderSimple9() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("-/**/"), "/*", "*/");
		assertEquals(0, handler.words.size());
		assertEquals(1, handler.comments.size());
		assertEquals("-", handler.punctbuffer.toString());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderMultiCommentSimple9() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("-/**/");
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals(0, handler.words.size());
		assertEquals(1, handler.comments.size());
		assertEquals("-", handler.punctbuffer.toString());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseSimple10() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("/", "/*", "*/");
		assertEquals(0, handler.words.size());
		assertEquals("/", handler.punctbuffer.toString());
		assertEquals(0, handler.comments.size());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderSimple10() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("/"), "/*", "*/");
		assertEquals(0, handler.words.size());
		assertEquals(0, handler.comments.size());
		assertEquals("/", handler.punctbuffer.toString());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderMultiCommentSimple10() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("/");
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals(0, handler.words.size());
		assertEquals(0, handler.comments.size());
		assertEquals("/", handler.punctbuffer.toString());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseNoCommentSimple10() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("/");
		assertEquals(0, handler.words.size());
		assertEquals("/", handler.punctbuffer.toString());
		assertEquals(0, handler.comments.size());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderNoCommentSimple10() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("/"));
		assertEquals(0, handler.words.size());
		assertEquals("/", handler.punctbuffer.toString());
		assertEquals(0, handler.comments.size());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseSimple11() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("/*", "/*", "*/");
		assertEquals(0, handler.words.size());
		assertEquals(1, handler.comments.size());
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(0, handler.lastCommentIndex);
	}

	@Test
	public void testParseReaderSimple11() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("/*"), "/*", "*/");
		assertEquals(0, handler.words.size());
		assertEquals(1, handler.comments.size());
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(0, handler.lastCommentIndex);
	}

	@Test
	public void testParseReaderMultiCommentSimple11() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("/*");
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals(0, handler.words.size());
		assertEquals(1, handler.comments.size());
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(0, handler.lastCommentIndex);
	}

	@Test
	public void testParseSimple12() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("<!-", "<!--", "-->");
		assertEquals(0, handler.words.size());
		assertEquals("<!-", handler.punctbuffer.toString());
		assertEquals(0, handler.comments.size());
		assertEquals(2, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderSimple12() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("<!-"), "<!--", "-->");
		assertEquals(0, handler.words.size());
		assertEquals(0, handler.comments.size());
		assertEquals("<!-", handler.punctbuffer.toString());
		assertEquals(2, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderMultiCommentSimple12() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("<!-");
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals(0, handler.words.size());
		assertEquals(0, handler.comments.size());
		assertEquals("<!-", handler.punctbuffer.toString());
		assertEquals(2, handler.lastCharacterIndex);
	}

	@Test
	public void testParseSimple13() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("f-", "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("f-", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(0, handler.lastWordIndex);
	}

	@Test
	public void testParseReaderSimple13() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("f-"), "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("f-", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(0, handler.lastWordIndex);
	}

	@Test
	public void testParseReaderMultiCommentSimple13() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(new StringReader("f-"), opening, closing);
		assertEquals(1, handler.words.size());
		assertEquals("f-", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(0, handler.lastWordIndex);
	}

	@Test
	public void testParseNoCommentSimple13() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("f-");
		assertEquals(1, handler.words.size());
		assertEquals("f-", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(0, handler.lastWordIndex);
	}

	@Test
	public void testParseReaderNoCommentSimple13() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("f-"));
		assertEquals(1, handler.words.size());
		assertEquals("f-", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(0, handler.lastWordIndex);
	}

	@Test
	public void testParseSimple14() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("-", "/*", "*/");
		assertEquals(0, handler.words.size());
		assertEquals(0, handler.comments.size());
		assertEquals("-", handler.punctbuffer.toString());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderSimple14() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("-"), "/*", "*/");
		assertEquals(0, handler.words.size());
		assertEquals(0, handler.comments.size());
		assertEquals("-", handler.punctbuffer.toString());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderMultiCommentSimple14() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(new StringReader("-"), opening, closing);
		assertEquals(0, handler.words.size());
		assertEquals(0, handler.comments.size());
		assertEquals("-", handler.punctbuffer.toString());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseNoCommentSimple14() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("-");
		assertEquals(0, handler.words.size());
		assertEquals(0, handler.comments.size());
		assertEquals("-", handler.punctbuffer.toString());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderNoCommentSimple14() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("-"));
		assertEquals(0, handler.words.size());
		assertEquals(0, handler.comments.size());
		assertEquals("-", handler.punctbuffer.toString());
		assertEquals(0, handler.lastCharacterIndex);
	}

	@Test
	public void testParseSimple15() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("\\64  ", "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("4", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(1, handler.escaped.size());
		assertEquals("6", handler.escaped.get(0));
		assertEquals(" 3 4", handler.sepbuffer.toString());
		assertEquals(2, handler.lastWordIndex);
	}

	@Test
	public void testParseReaderSimple15() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("\\64  "), "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("4", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(1, handler.escaped.size());
		assertEquals("6", handler.escaped.get(0));
		assertEquals(" 3 4", handler.sepbuffer.toString());
		assertEquals(2, handler.lastWordIndex);
	}

	@Test
	public void testParseReaderMultiCommentSimple15() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(new StringReader("\\64  "), opening, closing);
		assertEquals(1, handler.words.size());
		assertEquals("4", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(1, handler.escaped.size());
		assertEquals("6", handler.escaped.get(0));
		assertEquals(" 3 4", handler.sepbuffer.toString());
		assertEquals(2, handler.lastWordIndex);
	}

	@Test
	public void testParseNoCommentSimple15() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("\\64  ");
		assertEquals(1, handler.words.size());
		assertEquals("4", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(1, handler.escaped.size());
		assertEquals("6", handler.escaped.get(0));
		assertEquals(" 3 4", handler.sepbuffer.toString());
		assertEquals(2, handler.lastWordIndex);
	}

	@Test
	public void testParseReaderNoCommentSimple15() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("\\64  "));
		assertEquals(1, handler.words.size());
		assertEquals("4", handler.words.get(0));
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(1, handler.escaped.size());
		assertEquals("6", handler.escaped.get(0));
		assertEquals(" 3 4", handler.sepbuffer.toString());
		assertEquals(2, handler.lastWordIndex);
	}

	@Test
	public void testParseComment() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("/*!rtl:ignore*/-o-foo:-o-bar;/*!rtl:ignore*/foo:bar/*!rtl:ignore*/", "/*", "*/");
		assertEquals(4, handler.words.size());
		assertEquals("-o-foo", handler.words.get(0));
		assertEquals("-o-bar", handler.words.get(1));
		assertEquals("foo", handler.words.get(2));
		assertEquals("bar", handler.words.get(3));
		assertEquals(":;:", handler.punctbuffer.toString());
		assertEquals(3, handler.comments.size());
		assertEquals("!rtl:ignore", handler.comments.get(0));
		assertEquals("!rtl:ignore", handler.comments.get(1));
		assertEquals("!rtl:ignore", handler.comments.get(2));
		assertEquals(47, handler.lastCharacterIndex);
		assertEquals(48, handler.lastWordIndex);
		assertEquals(51, handler.lastCommentIndex);
		assertEquals(-1, handler.lastControlIndex);
	}

	@Test
	public void testParseCommentNL() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("/*!rtl:ignore\n*/-o-foo:-o-bar;/*!rtl:ignore\n*/foo:bar/*!rtl:ignore\n*/", "/*", "*/");
		assertEquals(4, handler.words.size());
		assertEquals("-o-foo", handler.words.get(0));
		assertEquals("-o-bar", handler.words.get(1));
		assertEquals("foo", handler.words.get(2));
		assertEquals("bar", handler.words.get(3));
		assertEquals(":;:", handler.punctbuffer.toString());
		assertEquals(3, handler.comments.size());
		assertEquals("!rtl:ignore\n", handler.comments.get(0));
		assertEquals("!rtl:ignore\n", handler.comments.get(1));
		assertEquals("!rtl:ignore\n", handler.comments.get(2));
		assertEquals(49, handler.lastCharacterIndex);
		assertEquals(50, handler.lastWordIndex);
		assertEquals(53, handler.lastCommentIndex);
		assertEquals(66, handler.lastControlIndex);
		assertEquals(3, handler.control10);
	}

	@Test
	public void testParseCommentCDO() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("<!--!rtl:ignore-->-o-foo:-o-bar;<!--!rtl:ignore-->foo:bar<!--!rtl:ignore-->", "<!--", "-->");
		assertEquals(4, handler.words.size());
		assertEquals("-o-foo", handler.words.get(0));
		assertEquals("-o-bar", handler.words.get(1));
		assertEquals("foo", handler.words.get(2));
		assertEquals("bar", handler.words.get(3));
		assertEquals(":;:", handler.punctbuffer.toString());
		assertEquals(3, handler.comments.size());
		assertEquals("!rtl:ignore", handler.comments.get(0));
		assertEquals("!rtl:ignore", handler.comments.get(1));
		assertEquals("!rtl:ignore", handler.comments.get(2));
	}

	@Test
	public void testParseReaderComment() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("/*!rtl:ignore*/-o-foo:-o-bar;/*!rtl:ignore*/foo:bar/*!rtl:ignore*/"), "/*", "*/");
		assertEquals(4, handler.words.size());
		assertEquals("-o-foo", handler.words.get(0));
		assertEquals("-o-bar", handler.words.get(1));
		assertEquals("foo", handler.words.get(2));
		assertEquals("bar", handler.words.get(3));
		assertEquals(":;:", handler.punctbuffer.toString());
		assertEquals(3, handler.comments.size());
		assertEquals("!rtl:ignore", handler.comments.get(0));
		assertEquals("!rtl:ignore", handler.comments.get(1));
		assertEquals("!rtl:ignore", handler.comments.get(2));
		assertEquals(47, handler.lastCharacterIndex);
		assertEquals(48, handler.lastWordIndex);
		assertEquals(51, handler.lastCommentIndex);
		assertEquals(-1, handler.lastControlIndex);
	}

	@Test
	public void testParseReaderCommentNL() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("/*!rtl:ignore\n*/-o-foo:-o-bar;/*!rtl:ignore\n*/foo:bar/*!rtl:ignore\n*/"), "/*", "*/");
		assertEquals(4, handler.words.size());
		assertEquals("-o-foo", handler.words.get(0));
		assertEquals("-o-bar", handler.words.get(1));
		assertEquals("foo", handler.words.get(2));
		assertEquals("bar", handler.words.get(3));
		assertEquals(":;:", handler.punctbuffer.toString());
		assertEquals(3, handler.comments.size());
		assertEquals("!rtl:ignore\n", handler.comments.get(0));
		assertEquals("!rtl:ignore\n", handler.comments.get(1));
		assertEquals("!rtl:ignore\n", handler.comments.get(2));
		assertEquals(49, handler.lastCharacterIndex);
		assertEquals(50, handler.lastWordIndex);
		assertEquals(53, handler.lastCommentIndex);
		assertEquals(66, handler.lastControlIndex);
		assertEquals(3, handler.control10);
		assertEquals(0, handler.control13);
	}

	@Test
	public void testParseReaderCommentNL2() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("/*!rtl:ignore\n*/\n-o-foo:-o-bar;/*!rtl:ignore\n*/\nfoo:bar/*!rtl:ignore\n*/\n"), "/*", "*/");
		assertEquals(4, handler.words.size());
		assertEquals("-o-foo", handler.words.get(0));
		assertEquals("-o-bar", handler.words.get(1));
		assertEquals("foo", handler.words.get(2));
		assertEquals("bar", handler.words.get(3));
		assertEquals(":;:", handler.punctbuffer.toString());
		assertEquals(3, handler.comments.size());
		assertEquals("!rtl:ignore\n", handler.comments.get(0));
		assertEquals("!rtl:ignore\n", handler.comments.get(1));
		assertEquals("!rtl:ignore\n", handler.comments.get(2));
		assertEquals(51, handler.lastCharacterIndex);
		assertEquals(52, handler.lastWordIndex);
		assertEquals(55, handler.lastCommentIndex);
		assertEquals(71, handler.lastControlIndex);
		assertEquals(6, handler.control10);
	}

	@Test
	public void testParseReaderFileComment() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		Reader re = loadTestReader("comments.txt");
		tp.parse(re, "/*", "*/");
		assertEquals(12, handler.words.size());
		assertEquals("-webkit-keyframes", handler.words.get(0));
		assertEquals("important1", handler.words.get(1));
		assertEquals("from", handler.words.get(2));
		assertEquals("margin-top", handler.words.get(3));
		assertEquals("50px", handler.words.get(4));
		assertEquals("50", handler.words.get(5));
		assertEquals("margin-top", handler.words.get(6));
		assertEquals("150px", handler.words.get(7));
		assertEquals("important", handler.words.get(8));
		assertEquals("to", handler.words.get(9));
		assertEquals("margin-top", handler.words.get(10));
		assertEquals("100px", handler.words.get(11));
		assertEquals("@{{:;}%{:!;}{:;}}", handler.punctbuffer.toString());
		assertEquals(13, handler.comments.size());
		assertEquals(" pre-webkit-kfs ", handler.comments.get(0));
		assertEquals(" pre-webkit-kf-list ", handler.comments.get(1));
		assertEquals(" post-webkit-kfsel-from ", handler.comments.get(2));
		assertEquals(" post-webkit-kf-list ", handler.comments.get(12));
		assertEquals(0, handler.errorCounter);
		assertEquals(8, handler.control10);
		if (handler.control13 == 0) {
			assertEquals(420, handler.lastCharacterIndex);
			assertEquals(415, handler.lastWordIndex);
			assertEquals(449, handler.lastCommentIndex);
			assertEquals(476, handler.lastControlIndex);
		} else {
			// Windows
			assertEquals(8, handler.control13);
			assertEquals(425, handler.lastCharacterIndex);
			assertEquals(420, handler.lastWordIndex);
			assertEquals(455, handler.lastCommentIndex);
			assertEquals(484, handler.lastControlIndex);
		}
	}

	@Test
	public void testParseReaderSimple5() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("f:g"), "/*", "*/");
		assertEquals(2, handler.words.size());
		assertEquals("f", handler.words.get(0));
		assertEquals("g", handler.words.get(1));
		assertEquals(":", handler.punctbuffer.toString());
	}

	@Test
	public void testParseReaderSimple6() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("f-g:"), "/*", "*/");
		assertEquals(1, handler.words.size());
		assertEquals("f-g", handler.words.get(0));
		assertEquals(":", handler.punctbuffer.toString());
	}

	@Test
	public void testParseHyphen() throws TimeoutException {
		int[] allowInWords = { 37, 45, 46 }; // %-.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("(display: table-cell) and (width: calc(100% - 2em))", "/*", "*/");
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("width", handler.words.get(3));
		assertEquals("calc", handler.words.get(4));
		assertEquals("100%", handler.words.get(5));
		assertEquals("2em", handler.words.get(6));
		assertEquals("(:)(:(-))", handler.punctbuffer.toString());
		assertEquals("(((", handler.openbuffer.toString());
		assertEquals(")))", handler.closebuffer.toString());
		assertEquals(" 9 21 25 33 43 45", handler.sepbuffer.toString());
	}

	@Test
	public void testParseAllowedInComment() throws TimeoutException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("<!ELEMENT> <!-- comment -->", "<!--", "-->");
		assertEquals(1, handler.words.size());
		assertEquals("<!ELEMENT", handler.words.get(0));
		assertEquals(" comment ", handler.comments.get(0));
		assertEquals(">", handler.punctbuffer.toString());
		assertEquals(0, handler.openbuffer.length());
		assertEquals(0, handler.closebuffer.length());
		assertEquals(" 10", handler.sepbuffer.toString());
	}

	@Test
	public void testParseReaderAllowedInComment() throws TimeoutException, IOException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("<!ELEMENT> <!-- comment -->"), "<!--", "-->");
		assertEquals(1, handler.words.size());
		assertEquals("<!ELEMENT", handler.words.get(0));
		assertEquals(" comment ", handler.comments.get(0));
		assertEquals(">", handler.punctbuffer.toString());
		assertEquals(0, handler.openbuffer.length());
		assertEquals(0, handler.closebuffer.length());
		assertEquals(" 10", handler.sepbuffer.toString());
	}

	@Test
	public void testParseReaderMultiAllowedInComment() throws TimeoutException, IOException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(new StringReader("<!ELEMENT> <!-- comment -->"), opening, closing);
		assertEquals(1, handler.words.size());
		assertEquals("<!ELEMENT", handler.words.get(0));
		assertEquals(" comment ", handler.comments.get(0));
		assertEquals(">", handler.punctbuffer.toString());
		assertEquals(0, handler.openbuffer.length());
		assertEquals(0, handler.closebuffer.length());
		assertEquals(" 10", handler.sepbuffer.toString());
	}

	@Test
	public void testParseAllowedBeforeComment() throws TimeoutException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("<<!-- comment --><", "<!--", "-->");
		assertEquals(0, handler.words.size());
		assertEquals(" comment ", handler.comments.get(0));
		assertEquals("<<", handler.punctbuffer.toString());
		assertEquals(0, handler.openbuffer.length());
		assertEquals(0, handler.closebuffer.length());
		assertEquals(0, handler.sepbuffer.length());
	}

	@Test
	public void testParseAllowedBeforeComment2() throws TimeoutException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("!<!-- comment -->!", "<!--", "-->");
		assertEquals(0, handler.words.size());
		assertEquals(" comment ", handler.comments.get(0));
		assertEquals("!!", handler.punctbuffer.toString());
		assertEquals(0, handler.openbuffer.length());
		assertEquals(0, handler.closebuffer.length());
		assertEquals(0, handler.sepbuffer.length());
	}

	@Test
	public void testParseReaderAllowedBeforeComment() throws TimeoutException, IOException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("<<!-- comment --><"), "<!--", "-->");
		assertEquals(0, handler.words.size());
		assertEquals(" comment ", handler.comments.get(0));
		assertEquals("<<", handler.punctbuffer.toString());
		assertEquals(0, handler.openbuffer.length());
		assertEquals(0, handler.closebuffer.length());
		assertEquals(0, handler.sepbuffer.length());
	}

	@Test
	public void testParseReaderAllowedBeforeComment2() throws TimeoutException, IOException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("!<!-- comment -->!"), "<!--", "-->");
		assertEquals(0, handler.words.size());
		assertEquals(" comment ", handler.comments.get(0));
		assertEquals("!!", handler.punctbuffer.toString());
		assertEquals(0, handler.openbuffer.length());
		assertEquals(0, handler.closebuffer.length());
		assertEquals(0, handler.sepbuffer.length());
	}

	@Test
	public void testParseReaderMultiCommentAllowedBeforeComment() throws TimeoutException, IOException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(new StringReader("<<!-- comment --><"), opening, closing);
		assertEquals(0, handler.words.size());
		assertEquals(" comment ", handler.comments.get(0));
		assertEquals("<<", handler.punctbuffer.toString());
		assertEquals(0, handler.openbuffer.length());
		assertEquals(0, handler.closebuffer.length());
		assertEquals(0, handler.sepbuffer.length());
	}

	@Test
	public void testParseReaderMultiCommentAllowedBeforeComment2() throws TimeoutException, IOException {
		int[] allowInWords = { 33, 60 }; // <!
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(new StringReader("/<!-- comment -->/"), opening, closing);
		assertEquals(0, handler.words.size());
		assertEquals(" comment ", handler.comments.get(0));
		assertEquals("//", handler.punctbuffer.toString());
		assertEquals(0, handler.openbuffer.length());
		assertEquals(0, handler.closebuffer.length());
		assertEquals(0, handler.sepbuffer.length());
	}

	@Test
	public void testParseQuotes() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("(display: table-cell) and (content: 'foo bar')", "/*", "*/");
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(" 9 21 25 35", handler.sepbuffer.toString());
		assertEquals(27, handler.lastWordIndex);
		assertEquals(36, handler.lastQuotedIndex);
	}

	@Test
	public void testParseQuotesAndComment() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("(display: table-cell) /* This is a comment */ and (content: 'foo bar')",
				"/*", "*/");
		assertEquals(" This is a comment ", handler.comments.get(0));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals(" 9 21 45 49 59", handler.sepbuffer.toString());
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(22, handler.lastCommentIndex);
		assertEquals(51, handler.lastWordIndex);
		assertEquals(58, handler.lastCharacterIndex);
	}

	@Test
	public void testParseQuotesAndCommentCDO() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("(display: table-cell) <!-- This is a comment --> and (content: 'foo bar')",
				"<!--", "-->");
		assertEquals(" This is a comment ", handler.comments.get(0));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals(" 9 21 48 52 62", handler.sepbuffer.toString());
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(22, handler.lastCommentIndex);
		assertEquals(54, handler.lastWordIndex);
		assertEquals(61, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderQuotesAndComment() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("(display: table-cell) /* This is a comment */ and (content: 'foo bar')"), "/*",
				"*/");
		assertEquals(" This is a comment ", handler.comments.get(0));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(" 9 21 45 49 59", handler.sepbuffer.toString());
	}

	@Test
	public void testParseReaderQuotesAndCommentCDO() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(new StringReader("(display: table-cell) <!-- This is a comment --> and (content: 'foo bar')  "), "<!--",
				"-->");
		assertEquals(" This is a comment ", handler.comments.get(0));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(" 9 21 48 52 62 73 74", handler.sepbuffer.toString());
		assertEquals(61, handler.lastCharacterIndex);
	}

	@Test
	public void testParseQuotesAndMultiComment() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader(
				"(display: table-cell) /* This is a comment */ and\n//This too\n (content: 'foo bar')");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "//" };
		String[] closing = { "*/", "\n" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals(" This is a comment ", handler.comments.get(0));
		assertEquals("This too", handler.comments.get(1));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(0, handler.errorCounter);
		assertEquals(2, handler.control10);
		assertEquals(60, handler.lastControlIndex);
		assertEquals(63, handler.lastWordIndex);
		assertEquals(70, handler.lastCharacterIndex);
	}

	@Test
	public void testParseQuotesAndMultiComment2() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader(
				"(display: table-cell) /* This is a comment */ and (content: 'foo bar')\n//This too\n");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "//" };
		String[] closing = { "*/", "\n" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals(" This is a comment ", handler.comments.get(0));
		assertEquals("This too", handler.comments.get(1));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(0, handler.errorCounter);
		assertEquals(51, handler.lastWordIndex);
		assertEquals(58, handler.lastCharacterIndex);
		assertEquals(81, handler.lastControlIndex);
		assertEquals(2, handler.control10);
	}

	@Test
	public void testParseQuotesAndMultiCommentEOL() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader(
				"(display: table-cell) /* This is a comment */ and (content: 'foo bar')\n//This too");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "//" };
		String[] closing = { "*/", "\n" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals(" This is a comment ", handler.comments.get(0));
		assertEquals("This too", handler.comments.get(1));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(0, handler.errorCounter);
		assertEquals(51, handler.lastWordIndex);
		assertEquals(58, handler.lastCharacterIndex);
		assertEquals(70, handler.lastControlIndex);
		assertEquals(1, handler.control10);
	}

	@Test
	public void testParseMultiCommentEOLBad() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader("(display: table-cell) /* This begins like a comment");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "//" };
		String[] closing = { "*/", "\n" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals(" This begins like a comment", handler.comments.get(0));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("(", handler.openbuffer.toString());
		assertEquals(")", handler.closebuffer.toString());
		assertEquals("(:)", handler.punctbuffer.toString());
		assertEquals(1, handler.errorCounter);
	}

	@Test
	public void testParseCommentEOLBad() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader("(display: table-cell) /* This begins like a comment");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(re, "/*", "*/");
		assertEquals(" This begins like a comment", handler.comments.get(0));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("(", handler.openbuffer.toString());
		assertEquals(")", handler.closebuffer.toString());
		assertEquals("(:)", handler.punctbuffer.toString());
		assertEquals(1, handler.errorCounter);
	}

	// A dash before the '-->' may confuse the parser
	@Test
	public void testParseReaderCommentCDOCDC() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader(
				"(display: table-cell) <!--- This is a --comment ---> and (content: 'foo bar')\n<!--This too-->");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String opening = "<!--";
		String closing = "-->";
		tp.parse(re, opening, closing);
		assertEquals("- This is a --comment -", handler.comments.get(0));
		assertEquals("This too", handler.comments.get(1));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(0, handler.errorCounter);
		assertEquals(58, handler.lastWordIndex);
		assertEquals(65, handler.lastCharacterIndex);
		assertEquals(77, handler.lastControlIndex);
		assertEquals(78, handler.lastCommentIndex);
		assertEquals(1, handler.control10);
	}

	@Test
	public void testParseReaderCommentCDOCDC2() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader(
				"(display: table-cell) <!--- This is a --comment ----> and (content: 'foo bar')\n<!--This too-->");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String opening = "<!--";
		String closing = "-->";
		tp.parse(re, opening, closing);
		assertEquals("- This is a --comment --", handler.comments.get(0));
		assertEquals("This too", handler.comments.get(1));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(0, handler.errorCounter);
		assertEquals(59, handler.lastWordIndex);
		assertEquals(66, handler.lastCharacterIndex);
		assertEquals(78, handler.lastControlIndex);
		assertEquals(79, handler.lastCommentIndex);
		assertEquals(1, handler.control10);
	}

	@Test
	public void testParseReaderMultiCommentCDOCDC() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader(
				"(display: table-cell) <!--- This is a --comment ---> and (content: 'foo bar')\n/*This too*/");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals("- This is a --comment -", handler.comments.get(0));
		assertEquals("This too", handler.comments.get(1));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(0, handler.errorCounter);
		assertEquals(58, handler.lastWordIndex);
		assertEquals(65, handler.lastCharacterIndex);
		assertEquals(77, handler.lastControlIndex);
		assertEquals(78, handler.lastCommentIndex);
		assertEquals(1, handler.control10);
	}

	@Test
	public void testParseReaderMultiCommentCDOCDCNL() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader(
				"(display: table-cell) <!--- This is a \n--comment ---> and (content: 'foo bar')/*\nThis\n too*/");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals("- This is a \n--comment -", handler.comments.get(0));
		assertEquals("\nThis\n too", handler.comments.get(1));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(0, handler.errorCounter);
		assertEquals(59, handler.lastWordIndex);
		assertEquals(66, handler.lastCharacterIndex);
		assertEquals(78, handler.lastCommentIndex);
		assertEquals(85, handler.lastControlIndex);
		assertEquals(3, handler.control10);
	}

	@Test
	public void testParseReaderMultiCommentCDOCDC2() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader(
				"(display: table-cell) <!--- This is a --comment ----> and (content: 'foo bar')\n/*This too*/");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "<!--" };
		String[] closing = { "*/", "-->" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals("- This is a --comment --", handler.comments.get(0));
		assertEquals("This too", handler.comments.get(1));
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(0, handler.errorCounter);
		assertEquals(59, handler.lastWordIndex);
		assertEquals(66, handler.lastCharacterIndex);
		assertEquals(78, handler.lastControlIndex);
		assertEquals(79, handler.lastCommentIndex);
		assertEquals(1, handler.control10);
	}

	@Test
	public void testParseQuotesEscaped() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("(display: table-cell) and (content: 'foo\\' bar')", "/*", "*/");
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo\\' bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
	}

	@Test
	public void testParseQuotesUnescapedNL() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("content: 'foo\nbar", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals("bar", handler.words.get(1));
		assertEquals(":", handler.punctbuffer.toString());
		assertEquals(1, handler.control10);
		assertEquals(0, handler.control13);
	}

	@Test
	public void testParseQuotesUnescapedNLAccept() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.setAcceptNewlineEndingQuote(true);
		tp.parse("content: 'foo\nbar", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals("'foo'", handler.words.get(1));
		assertEquals("bar", handler.words.get(2));
		assertEquals(":", handler.punctbuffer.toString());
		assertEquals(1, handler.control10);
		assertEquals(0, handler.control13);
		assertEquals(14, handler.lastWordIndex);
	}

	@Test
	public void testParseQuotesUnescapedNLEOF() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("content: 'foo\n", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals(1, handler.words.size());
		assertEquals(":", handler.punctbuffer.toString());
		assertEquals(1, handler.control10);
		assertEquals(0, handler.control13);
		assertEquals(1, handler.errorCounter);
	}

	@Test
	public void testParseQuotesUnescapedNLEOFAccept() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.setAcceptEofEndingQuoted(true);
		tp.parse("content: 'foo", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals("'foo'", handler.words.get(1));
		assertEquals(":", handler.punctbuffer.toString());
		assertEquals(0, handler.errorCounter);
	}

	@Test
	public void testParseQuotesEscapedControl() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("(display: table-cell) and (content: 'foo\\\n bar')", "/*", "*/");
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo\n bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(1, handler.control10);
	}

	@Test
	public void testParseQuotesEscapedControlCRLF() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("(display: table-cell) and (content: 'foo\\\r\n bar')", "/*", "*/");
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo\n bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(0, handler.control10);
	}

	@Test
	public void testParseReader() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("(display: table-cell) and (display: list-item)");
		tp.parse(re, "/*", "*/");
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("display", handler.words.get(3));
		assertEquals("list-item", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(36, handler.lastWordIndex);
		assertEquals(34, handler.lastCharacterIndex);
	}

	@Test
	public void testParseReaderNL() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("only\nscreen");
		tp.parse(re, "/*", "*/");
		assertEquals("only", handler.words.get(0));
		assertEquals("screen", handler.words.get(1));
		assertEquals(2, handler.words.size());
		assertEquals(0, handler.punctbuffer.length());
		assertEquals(1, handler.control10);
		assertEquals(5, handler.lastWordIndex);
	}

	@Test
	public void testParseEscaped() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("content: \\f435;", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals(2, handler.words.size());
		assertEquals("f", handler.escaped.get(0));
		assertEquals("435", handler.words.get(1));
		assertEquals(":;", handler.punctbuffer.toString());
		assertEquals(14, handler.lastCharacterIndex);
	}

	@Test
	public void testParseEscapedBackslash() throws TimeoutException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("content: \\\\f435;", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals(2, handler.words.size());
		assertEquals(1, handler.escaped.size());
		assertEquals("\\", handler.escaped.get(0));
		assertEquals("f435", handler.words.get(1));
		assertEquals(":;", handler.punctbuffer.toString());
	}

	@Test
	public void testParseReaderHyphen() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 37, 46 }; // -%.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("(display: table-cell) and (width: calc(100% - 2em))");
		tp.parse(re, "/*", "*/");
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("width", handler.words.get(3));
		assertEquals("calc", handler.words.get(4));
		assertEquals("100%", handler.words.get(5));
		assertEquals("2em", handler.words.get(6));
		assertEquals("(((", handler.openbuffer.toString());
		assertEquals(")))", handler.closebuffer.toString());
		assertEquals("(:)(:(-))", handler.punctbuffer.toString());
	}

	@Test
	public void testParseReaderQuotes() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("(display: table-cell) and (content: 'foo bar')");
		tp.parse(re, "/*", "*/");
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(" 9 21 25 35", handler.sepbuffer.toString());
		assertEquals(27, handler.lastWordIndex);
		assertEquals(36, handler.lastQuotedIndex);
	}

	@Test
	public void testParseReaderQuotesEscaped() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("(display: table-cell) and (content: 'foo\\' bar')");
		tp.parse(re, "/*", "*/");
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("and", handler.words.get(2));
		assertEquals("content", handler.words.get(3));
		assertEquals("'foo\\' bar'", handler.words.get(4));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)(:)", handler.punctbuffer.toString());
		assertEquals(36, handler.lastQuotedIndex);
	}

	@Test
	public void testParseReaderQuotesAndCommentDisabled() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader(
				"(display: table-cell) /* no-comment */ and (content: 'foo bar')\n//neither\n");
		TestTokenHandler3 handler = new DisableCommentsTokenHandler();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		String[] opening = { "/*", "//" };
		String[] closing = { "*/", "\n" };
		tp.parseMultiComment(re, opening, closing);
		assertEquals(0, handler.comments.size());
		assertEquals("display", handler.words.get(0));
		assertEquals("table-cell", handler.words.get(1));
		assertEquals("no-comment", handler.words.get(2));
		assertEquals("and", handler.words.get(3));
		assertEquals("content", handler.words.get(4));
		assertEquals("'foo bar'", handler.words.get(5));
		assertEquals("neither", handler.words.get(6));
		assertEquals("((", handler.openbuffer.toString());
		assertEquals("))", handler.closebuffer.toString());
		assertEquals("(:)/**/(:)//", handler.punctbuffer.toString());
		assertEquals(0, handler.errorCounter);
		assertEquals(-1, handler.lastCommentIndex);
		assertEquals(2, handler.control10);
		assertEquals(53, handler.lastQuotedIndex);
	}

	@Test
	public void testParseReaderQuotesUnescapedNL() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader("content: 'foo\nbar");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(re, "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals("bar", handler.words.get(1));
		assertEquals(":", handler.punctbuffer.toString());
		assertEquals(1, handler.control10);
		assertEquals(1, handler.errorCounter);
	}

	@Test
	public void testParseReaderQuotesUnescapedNLAccept() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader("content: 'foo\nbar");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.setAcceptNewlineEndingQuote(true);
		tp.parse(re, "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals("'foo'", handler.words.get(1));
		assertEquals("bar", handler.words.get(2));
		assertEquals(":", handler.punctbuffer.toString());
		assertEquals(1, handler.control10);
	}

	@Test
	public void testParseReaderQuotesUnescapedNLEOF() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader("content: 'foo\n");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse(re, "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals(1, handler.words.size());
		assertEquals(":", handler.punctbuffer.toString());
		assertEquals(1, handler.control10);
		assertEquals(1, handler.errorCounter);
	}

	@Test
	public void testParseReaderQuotesUnescapedNLEOFAccept() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		StringReader re = new StringReader("content: 'foo");
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.setAcceptEofEndingQuoted(true);
		tp.parse(re, "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals("'foo'", handler.words.get(1));
		assertEquals(":", handler.punctbuffer.toString());
	}

	@Test
	public void testParseBackslash() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("content: \\\\", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals("\\", handler.escaped.get(0));
		assertEquals(":", handler.punctbuffer.toString());
	}

	@Test
	public void testParseBackslash2() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("content: \\\\;", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals("\\", handler.escaped.get(0));
		assertEquals(":;", handler.punctbuffer.toString());
		assertEquals(11, handler.lastCharacterIndex);
	}

	@Test
	public void testParseQuotedBackslash() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("content: '\\'", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals(1, handler.words.size());
		assertEquals(":", handler.punctbuffer.toString());
	}

	@Test
	public void testParseQuotedBackslash2() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("content: '\\\\'", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals("'\\\\'", handler.words.get(1));
		assertEquals(":", handler.punctbuffer.toString());
	}

	@Test
	public void testParseReaderBackslash() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("content: \\\\");
		tp.parse(re, "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals("\\", handler.escaped.get(0));
		assertEquals(":", handler.punctbuffer.toString());
	}

	@Test
	public void testParseReaderQuotedBackslash() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("content: '\\'");
		tp.parse(re, "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals(1, handler.words.size());
		assertEquals(":", handler.punctbuffer.toString());
	}

	@Test
	public void testParseReaderQuotedBackslash2() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		StringReader re = new StringReader("content: '\\\\'");
		tp.parse(re, "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals("'\\\\'", handler.words.get(1));
		assertEquals(":", handler.punctbuffer.toString());
	}

	@Test
	public void testParseSurrogate() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("content: 🚧;", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals(":🚧;", handler.punctbuffer.toString());
		assertEquals(11, handler.lastCharacterIndex);
	}

	@Test
	public void testParseSurrogate2() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("content: \ud83d\udd0a;", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals(":🔊;", handler.punctbuffer.toString());
		assertEquals(11, handler.lastCharacterIndex);
	}

	@Test
	public void testParseSurrogate3() throws TimeoutException, IOException {
		int[] allowInWords = { 45, 46 }; // -.
		TestTokenHandler3 handler = new TestTokenHandler3();
		TokenProducer3<TimeoutException> tp = new TokenProducer3<>(handler, allowInWords);
		tp.parse("content: \ud950;", "/*", "*/");
		assertEquals("content", handler.words.get(0));
		assertEquals(":\ud950;", handler.punctbuffer.toString());
		assertEquals(10, handler.lastCharacterIndex);
	}

	private static Reader loadTestReader(String filename) {
		return loadfromClasspath("/io/sf/carte/uparser/" + filename);
	}

	private static Reader loadfromClasspath(final String filename) {
		InputStream is = TokenProducer3Test.class.getResourceAsStream(filename);
		return is != null ? new InputStreamReader(is, StandardCharsets.UTF_8) : null;
	}

	static class DisableCommentsTokenHandler extends TestTokenHandler3 {

		@Override
		public void tokenStart(TokenControl control) {
			control.disableAllComments();
		}

	}

}
