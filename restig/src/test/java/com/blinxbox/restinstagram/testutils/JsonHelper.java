/*
 * Copyright (c) 2010-2012 BlinxBox.
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

package com.blinxbox.restinstagram.testutils;

import static com.restfb.util.StringUtils.fromInputStream;

import java.io.IOException;

/**
 * Various helper methods used for json mapping. Extend this class to access
 * these methods.
 * 
 * @author Efi MK
 */
public class JsonHelper {

	/**
	 * Load premade json string from local path.
	 * 
	 * @param pathToJson
	 *            - Local path, e.g. /json/... of the json file.
	 * @return The content of the file.
	 */
	public static String jsonFromClasspath(final String pathToJson) {
		try {
			return fromInputStream(JsonHelper.class
					.getResourceAsStream("/json/" + pathToJson + ".json"));
		} catch (final IOException e) {
			throw new IllegalStateException(
					"Unable to load JSON from the classpath", e);
		}
	}
}