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

package com.blinxbox.restinstagram;

import com.blinxbox.restinstagram.exception.InstagramException;
import com.restfb.WebRequestor;

/**
 * Specifies how a <a href="http://instagr.am/developer/endpoints/">Instagram
 * API</a> client must operate.
 * <p>
 * If you'd like to...
 * 
 * <ul>
 * <li>Fetch an instagram collection: use
 * {@link #fetchCollection(String, Class, Parameter...)}</li>
 * </ul>
 * 
 * @author Efi MK
 */
public interface InstagramClient {

	/**
	 * Fetches an {@code InstagramCollection} type, mapping the result to an
	 * instance of {@code connectionType}.
	 * 
	 * @param <T>
	 *            Java type to map to.
	 * @param endPoint
	 *            The name of the end point, e.g.
	 *            {@code "tags/snow/media/recent"}.
	 * @param type
	 *            end point type token.
	 * @param parameters
	 *            - Various parameters that will be used during the final API
	 *            call.
	 * @return An instance of type {@code type} which contains the requested
	 *         Collection's data.
	 * @throws InstagramException
	 *             If an error occurs while performing the API call.
	 */
	<T> InstagramCollection<T> fetchCollection(String endPoint, Class<T> type,
			Parameter... parameters) throws InstagramException;

	/**
	 * Fetches a single Instagram object, mapping the result to an instance of
	 * {@code objectType}.
	 * 
	 * @param <T>
	 *            Java type to map to.
	 * @param endPoint
	 *            The name of the end point, e.g.
	 *            {@code "media/197471638763567976_7698549"}.
	 * @param objectType
	 *            end point type token.
	 * @param parameters
	 *            URL parameters to include in the API call (optional).
	 * @return An instance of type {@code objectType} which contains the
	 *         requested object's data.
	 * @throws InstagramException
	 *             If an error occurs while performing the API call.
	 */
	<T> T fetchObject(String endPoint, Class<T> objectType,
			Parameter... parameters) throws InstagramException;

	/**
	 * Post an action on one og the end points. Behind the scenes a Post call is
	 * being made. <b>Pay attention</b> Most of the publish actions will require
	 * an access token.
	 * 
	 * @param endPoint
	 *            - Publish on this end point, for example - media/12345/likes.
	 *            Cannot be null or empty.
	 * @param parameters
	 *            - Parameters Instagram might accept as part of the publish.
	 * @throws InstagramException
	 *             - Error while publishing to instagram.
	 */
	public void publish(String endPoint, Parameter... parameters)
			throws InstagramException;

	/**
	 * Gets the {@code JsonMapper} used to convert Instagram JSON to Java
	 * objects.
	 * 
	 * @return The {@code JsonMapper} used to convert Instagram JSON to Java
	 *         objects.
	 */
	JsonMapper getJsonMapper();

	/**
	 * Gets the {@code WebRequestor} used to talk to the Instagram API
	 * endpoints.
	 * 
	 * @return The {@code WebRequestor} used to talk to the Instagram API
	 *         endpoints.
	 */
	WebRequestor getWebRequestor();

	/**
	 * @return True if user's session is valid, e.g. does the user has an access
	 *         token.
	 */
	boolean isSessionValid();
}