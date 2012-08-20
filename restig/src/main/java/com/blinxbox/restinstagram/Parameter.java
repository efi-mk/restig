/*
 * Copyright (c) 2010-2012 Mark Allen.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.blinxbox.restinstagram;

import static com.restfb.util.StringUtils.isBlank;
import static com.restfb.util.StringUtils.trimToEmpty;
import static java.lang.String.format;

/**
 * Representation of a Instagram API request parameter.
 * 
 * @author Efi MK
 */
public final class Parameter {
	/**
	 * Parameter name.
	 */
	public final String name;

	/**
	 * Parameter value.
	 */
	public final String value;

	/**
	 * Creates a new parameter with the given {@code name} and {@code value}.
	 * 
	 * @param name
	 *            The parameter name.
	 * @param value
	 *            The parameter value.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code name} is {@code null} or a blank string or either
	 *             {@code value} or {@code jsonMapper} is {@code null}.
	 */
	public Parameter(final String name, final Object value)
			throws IllegalArgumentException {
		if (isBlank(name) || value == null) {
			throw new IllegalArgumentException(
					Parameter.class
							+ " instances must have a non-blank name and non-null value.");
		}

		this.name = trimToEmpty(name).toLowerCase();

		this.value = value.toString();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}

		final Parameter other = (Parameter) obj;

		if (this.name != other.name && (!this.name.equals(other.name))) {
			return false;
		}
		if (this.value != other.value && (!this.value.equals(other.value))) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + this.name.hashCode();
		hash = 41 * hash + this.value.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return format("Parameter[%s=%s]", name, value);
	}
}