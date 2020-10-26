/*

 Copyright (c) 2017-2020, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

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
