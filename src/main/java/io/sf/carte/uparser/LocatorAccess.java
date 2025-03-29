/*

 Copyright (c) 2017-2025, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-3-Clause

package io.sf.carte.uparser;

/**
 * Set a line-column location.
 */
public interface LocatorAccess {

	/**
	 * Set a location's line and column.
	 * 
	 * @param line the line number.
	 * @param column the column number.
	 */
	void setLocation(int line, int column);

}
