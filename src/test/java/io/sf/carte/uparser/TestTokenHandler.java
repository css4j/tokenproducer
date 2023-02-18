/*

 Copyright (c) 2017-2023, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

import java.util.LinkedList;

class TestTokenHandler implements TokenHandler {

	TokenControl control = null;
	LinkedList<String> words = new LinkedList<String>();
	LinkedList<String> escaped = new LinkedList<String>();
	LinkedList<String> comments = new LinkedList<String>();
	StringBuilder punctbuffer = new StringBuilder();
	StringBuilder sepbuffer = new StringBuilder();
	StringBuilder openbuffer = new StringBuilder();
	StringBuilder closebuffer = new StringBuilder();
	int control10 = 0;
	int control13 = 0;
	int errorCounter = 0;
	int lastWordIndex = -1;
	int lastQuotedIndex = -1;
	int lastCharacterIndex = -1;
	int lastCommentIndex = -1;
	int lastControlIndex = -1;

	@Override
	public void word(int index, CharSequence word) {
		words.add(word.toString());
		lastWordIndex = index;
	}

	@Override
	public void separator(int index, int cp) {
		sepbuffer.append(' ').append(index);
	}

	@Override
	public void openGroup(int index, int codepoint) {
		char[] chars = Character.toChars(codepoint);
		openbuffer.append(chars);
		punctbuffer.append(chars);
	}

	@Override
	public void closeGroup(int index, int codepoint) {
		char[] chars = Character.toChars(codepoint);
		closebuffer.append(chars);
		punctbuffer.append(chars);
	}

	@Override
	public void character(int index, int codepoint) {
		char[] chars = Character.toChars(codepoint);
		punctbuffer.append(chars);
		lastCharacterIndex = index;
	}

	@Override
	public void quoted(int index, CharSequence quoted, int quoteCp) {
		char c = (char) quoteCp;
		StringBuilder buf = new StringBuilder(quoted.length() + 2);
		buf.append(c).append(quoted).append(c);
		words.add(buf.toString());
		lastQuotedIndex = index;
	}

	@Override
	public void quotedWithControl(int index, CharSequence quoted, int quoteCp) {
		quoted(index, quoted, quoteCp);
		lastQuotedIndex = index;
	}

	@Override
	public void escaped(int index, int codepoint) {
		escaped.add(new String(Character.toChars(codepoint)));
	}

	@Override
	public void control(int index, int codepoint) {
		if (codepoint == 10) {
			control10++;
		} else if (codepoint == 13) {
			control13++;
		}
		lastControlIndex = index;
	}

	@Override
	public void quotedNewlineChar(int index, int codepoint) {
		if (codepoint == 10) {
			control10++;
		} else if (codepoint == 13) {
			control13++;
		}
	}

	@Override
	public void commented(int index, int commentType, String comment) {
		comments.add(comment);
		lastCommentIndex = index;
	}

	@Override
	public void endOfStream(int len) {
	}

	@Override
	public void error(int index, byte errCode, CharSequence context) {
		errorCounter++;
	}

	@Override
	public void tokenStart(TokenControl control) {
		this.control = control;
	}

}