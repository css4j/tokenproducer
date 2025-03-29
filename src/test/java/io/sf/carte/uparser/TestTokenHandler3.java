/*

 Copyright (c) 2017-2025, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

class TestTokenHandler3 implements TokenHandler3<TimeoutException> {

	TokenControl control = null;
	LinkedList<String> words = new LinkedList<>();
	LinkedList<String> escaped = new LinkedList<>();
	LinkedList<String> comments = new LinkedList<>();
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
	public void word(int index, CharSequence word) throws TimeoutException {
		words.add(word.toString());
		lastWordIndex = index;
	}

	@Override
	public void separator(int index, int cp) throws TimeoutException {
		sepbuffer.append(' ').append(index);
	}

	@Override
	public void leftParenthesis(int index) throws TimeoutException {
		openbuffer.append('(');
		punctbuffer.append('(');
	}

	@Override
	public void leftSquareBracket(int index) throws TimeoutException {
		openbuffer.append('[');
		punctbuffer.append('[');
	}

	@Override
	public void leftCurlyBracket(int index) throws TimeoutException {
		openbuffer.append('{');
		punctbuffer.append('{');
	}

	@Override
	public void rightParenthesis(int index) throws TimeoutException {
		closebuffer.append(')');
		punctbuffer.append(')');
	}

	@Override
	public void rightSquareBracket(int index) throws TimeoutException {
		closebuffer.append(']');
		punctbuffer.append(']');
	}

	@Override
	public void rightCurlyBracket(int index) throws TimeoutException {
		closebuffer.append('}');
		punctbuffer.append('}');
	}

	@Override
	public void character(int index, int codepoint) throws TimeoutException {
		char[] chars = Character.toChars(codepoint);
		punctbuffer.append(chars);
		lastCharacterIndex = index;
	}

	@Override
	public void quoted(int index, CharSequence quoted, int quoteCp) throws TimeoutException {
		char c = (char) quoteCp;
		StringBuilder buf = new StringBuilder(quoted.length() + 2);
		buf.append(c).append(quoted).append(c);
		words.add(buf.toString());
		lastQuotedIndex = index;
	}

	@Override
	public void quotedWithControl(int index, CharSequence quoted, int quoteCp) throws TimeoutException {
		quoted(index, quoted, quoteCp);
		lastQuotedIndex = index;
	}

	@Override
	public void escaped(int index, int codepoint) throws TimeoutException {
		escaped.add(new String(Character.toChars(codepoint)));
	}

	@Override
	public void control(int index, int codepoint) throws TimeoutException {
		if (codepoint == 10) {
			control10++;
		} else if (codepoint == 13) {
			control13++;
		}
		lastControlIndex = index;
	}

	@Override
	public void quotedNewlineChar(int index, int codepoint) throws TimeoutException {
		if (codepoint == 10) {
			control10++;
		} else if (codepoint == 13) {
			control13++;
		}
	}

	@Override
	public void commented(int index, int commentType, String comment) throws TimeoutException {
		comments.add(comment);
		lastCommentIndex = index;
	}

	@Override
	public void endOfStream(int len) throws TimeoutException {
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