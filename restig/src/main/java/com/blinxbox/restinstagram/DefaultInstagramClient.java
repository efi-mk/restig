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

import static com.restfb.util.StringUtils.isBlank;
import static com.restfb.util.StringUtils.trimToEmpty;
import static com.restfb.util.StringUtils.trimToNull;
import static com.restfb.util.StringUtils.urlEncode;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;

import com.blinxbox.restinstagram.exception.InstagramException;
import com.blinxbox.restinstagram.exception.InstagramJsonMappingException;
import com.blinxbox.restinstagram.exception.InstagramNetworkException;
import com.restfb.DefaultWebRequestor;
import com.restfb.WebRequestor;
import com.restfb.WebRequestor.Response;
import com.restfb.json.JsonException;
import com.restfb.json.JsonObject;

/**
 * Default implementation of a <a
 * href="http://instagr.am/developer/endpoints/">Instagram API</a> client.
 * 
 * @author Efi MK
 */
public class DefaultInstagramClient extends AbstractInstagramClient {
	/**
	 * User's API access token.
	 */
	protected String mAccessToken;

	/**
	 * API endpoint URL.
	 */
	public static final String INSTAGRAM_GRAPH_ENDPOINT_URL = "https://api.instagram.com/v1";

	/**
	 * An attribute if exists means the response has an error.
	 */
	private static final String ERROR_ATTRIBUTE_NAME = "error_type";

	/**
	 * A meta object which contains information about the response.
	 */
	private static final String META_OBJECT = "meta";

	/**
	 * The attribute that contains the error code.
	 */
	private static final String ERROR_CODE_ATTRIBUTE_NAME = "code";

	/**
	 * The attribute that contains the error type.
	 */
	private static final String ERROR_TYPE_ATTRIBUTE_NAME = "error_type";

	/**
	 * The attribute that contains the message.
	 */
	private static final String ERROR_MESSAGE_ATTRIBUTE_NAME = "error_message";

	/**
	 * A instagram client ID.
	 */
	private final String mClientId;

	/**
	 * Creates an Instagram API client with the given {@code clientID}.
	 * 
	 * @param clientId
	 *            An Instagram OAuth access token.
	 */
	public DefaultInstagramClient(final String clientId) {
		this(clientId, null, new DefaultWebRequestor(), new DefaultJsonMapper());
	}

	/**
	 * Creates a Facebook Graph API client with the given {@code accessToken},
	 * {@code webRequestor}, and {@code jsonMapper}.
	 * 
	 * @param clientId
	 *            - An instagram client ID. Cannot be null or empty.
	 * @param accessToken
	 *            An Instagram OAuth access token. In case it's null the client
	 *            ID will be used.
	 * @param webRequestor
	 *            The {@link WebRequestor} implementation to use for sending
	 *            requests to the API endpoint.
	 * @param jsonMapper
	 *            The {@link JsonMapper} implementation to use for mapping API
	 *            response JSON to Java objects.
	 * @throws IllegalArgumentException
	 *             If {@code jsonMapper} or {@code webRequestor} is {@code null}
	 *             .
	 */
	public DefaultInstagramClient(final String clientId,
			final String accessToken, final WebRequestor webRequestor,
			final JsonMapper jsonMapper) throws IllegalArgumentException {
		super();

		verifyParameterPresence("clientId", clientId);
		verifyParameterPresence("jsonMapper", jsonMapper);
		verifyParameterPresence("webRequestor", webRequestor);

		this.mAccessToken = trimToNull(accessToken);
		this.webRequestor = webRequestor;
		this.jsonMapper = jsonMapper;
		mClientId = clientId;

		illegalParamNames.addAll(Arrays
				.asList(new String[] { CLIENT_ID_PARAM_NAME }));
	}

	/**
	 * Creates an Instagram API client with the given {@code clientID} and
	 * {@code accessToken}.
	 * 
	 * @param appId
	 *            An Instagram OAuth access token.
	 * @param accessToken
	 *            - A user specific access token. Cannot be null or empty.
	 */
	public DefaultInstagramClient(final String appId, final String accessToken) {
		this(appId, accessToken, new DefaultWebRequestor(),
				new DefaultJsonMapper());
	}

	/** {@inheritDoc} */
	@Override
	public <T> InstagramCollection<T> fetchCollection(final String endPoint,
			final Class<T> type, final Parameter... parameters) {
		verifyParameterPresence("endPoint", endPoint);
		verifyParameterPresence("type", type);
		return new InstagramCollection<T>(this, makeRequest(endPoint, false,
				parameters), type);
	}

	@Override
	public <T> T fetchObject(final String endPoint, final Class<T> objectType,
			final Parameter... parameters) throws InstagramException {
		verifyParameterPresence("endPoint", endPoint);
		verifyParameterPresence("objectType", objectType);
		// Fetch from IG.
		final String json = makeRequest(endPoint, false, parameters);
		JsonObject jsonObject = null;

		try {
			jsonObject = new JsonObject(json);
		} catch (final JsonException e) {
			throw new InstagramJsonMappingException(MessageFormat.format(
					"The connection JSON you provided was invalid: {0}", json),
					e);
		}
		// Pull the data
		final JsonObject jsonData = jsonObject.getJsonObject("data");
		// Now convert.
		return jsonMapper.toJavaObject(jsonData.toString(), objectType);
	}

	@Override
	public void publish(final String endPoint, final Parameter... parameters)
			throws InstagramException {
		verifyParameterPresence("endPoint", endPoint);
		makeRequest(endPoint, true, parameters);
	}

	/**
	 * Coordinates the process of executing the API request GET/POST and
	 * processing the response we receive from the endpoint.
	 * 
	 * @param endpoint
	 *            Instagram API endpoint.
	 * @param parameters
	 *            Arbitrary number of parameters to send along to Instagram as
	 *            part of the API call.
	 * @return The JSON returned by Facebook for the API call.
	 * @throws InstagramException
	 *             If an error occurs while making the Instagram API or
	 *             processing the response.
	 */
	protected String makeRequest(String endpoint, final boolean executeAsPost,
			final Parameter... parameters) throws InstagramException {
		verifyParameterLegality(parameters);

		trimToEmpty(endpoint).toLowerCase();
		// Remove any prefix that contains the instagram api.
		if (endpoint.startsWith(INSTAGRAM_GRAPH_ENDPOINT_URL)) {
			endpoint = endpoint
					.substring(INSTAGRAM_GRAPH_ENDPOINT_URL.length());
		}
		if (!endpoint.startsWith("/")) {
			endpoint = "/" + endpoint;
		}

		final String fullEndpoint = createEndpointForApiCall(endpoint);
		final String parameterString = toParameterString(parameters);

		return makeRequestAndProcessResponse(new Requestor() {
			/**
			 * @see com.restfb.DefaultFacebookClient.Requestor#makeRequest()
			 */
			@Override
			public Response makeRequest() throws IOException {
				if (executeAsPost) {
					return webRequestor.executePost(fullEndpoint,
							parameterString);
				}
				return webRequestor.executeGet(fullEndpoint + "?"
						+ parameterString);
			}
		});
	}

	/**
	 * Represents an HTTP request.
	 * 
	 * @author Efi MK
	 * 
	 */
	protected static interface Requestor {
		/**
		 * Execute the actual request logic.
		 * 
		 * @return A response that the server returned back.
		 * @throws IOException
		 *             - Error while executing the request.
		 */
		Response makeRequest() throws IOException;
	}

	/**
	 * Execute the request and process the resulting json.
	 * 
	 * @param requestor
	 *            - Holds the actual request.
	 * @return A Json string containing the result.
	 * @throws InstagramException
	 *             - Invalid request. @
	 */
	protected String makeRequestAndProcessResponse(final Requestor requestor)
			throws InstagramException {
		Response response = null;

		// Perform a GET or POST to the API endpoint
		try {
			response = requestor.makeRequest();
		} catch (final Throwable t) {
			throw new InstagramNetworkException("Instagram request failed", t);
		}

		// If we get any HTTP response code other than a 200 OK
		// throw an exception.
		if (HTTP_OK != response.getStatusCode()) {
			throw new InstagramNetworkException("Instagram request failed",
					response.getStatusCode());
		}

		final String json = response.getBody();

		// If the response contained an error code, throw an exception.
		throwFacebookResponseStatusExceptionIfNecessary(json);

		return json;
	}

	/**
	 * Throws an exception if Facebook returned an error response. Using the
	 * Graph API, it's possible to see both the new Graph API-style errors as
	 * well as Legacy API-style errors, so we have to handle both here. This
	 * method extracts relevant information from the error JSON and throws an
	 * exception which encapsulates it for end-user consumption.
	 * <p>
	 * For Graph API errors:
	 * <p>
	 * If the {@code error} JSON field is present, we've got a response status
	 * error for this API call.
	 * <p>
	 * For Legacy errors (e.g. FQL):
	 * <p>
	 * If the {@code error_code} JSON field is present, we've got a response
	 * status error for this API call.
	 * 
	 * @param json
	 *            The JSON returned by Facebook in response to an API call.
	 */
	protected void throwFacebookResponseStatusExceptionIfNecessary(
			final String json) {

		// If the result is not an object, bail immediately.
		if (json.startsWith("{")) {

			final JsonObject errorObject = new JsonObject(json);

			final JsonObject innerErrorObject = errorObject
					.getJsonObject(META_OBJECT);

			// We have an error :-(
			if (innerErrorObject.has(ERROR_ATTRIBUTE_NAME)) {
				// If there's an Integer error code, pluck it out.
				final Integer errorCode = innerErrorObject
						.has(ERROR_CODE_ATTRIBUTE_NAME) ? toInteger(innerErrorObject
						.getString(ERROR_CODE_ATTRIBUTE_NAME)) : null;
				final String errorType = innerErrorObject
						.getString(ERROR_TYPE_ATTRIBUTE_NAME);
				final String errorMessage = innerErrorObject
						.getString(ERROR_MESSAGE_ATTRIBUTE_NAME);
				throw new InstagramException(String.format(
						"Code '%s' Type: '%s' Message '%s'", errorCode,
						errorType, errorMessage));
			}
		}

	}

	/**
	 * Generate the parameter string to be included in the Instagram API
	 * request.
	 * 
	 * @param parameters
	 *            Arbitrary number of extra parameters to include in the
	 *            request.
	 * @return The parameter string to include in the Instagram API request.
	 * @throws InstagramJsonMappingException
	 *             If an error occurs when building the parameter string.
	 */
	protected String toParameterString(Parameter... parameters)
			throws InstagramJsonMappingException {
		if (!isBlank(mAccessToken)) {
			parameters = parametersWithAdditionalParameter(new Parameter(
					ACCESS_TOKEN_PARAM_NAME, mAccessToken), parameters);
		} else {
			if (!isBlank(mClientId)) {
				parameters = parametersWithAdditionalParameter(new Parameter(
						CLIENT_ID_PARAM_NAME, mClientId), parameters);
			}
		}

		final StringBuilder parameterStringBuilder = new StringBuilder();
		boolean first = true;

		for (final Parameter parameter : parameters) {
			if (first) {
				first = false;
			} else {
				parameterStringBuilder.append("&");
			}

			parameterStringBuilder.append(urlEncode(parameter.name));
			parameterStringBuilder.append("=");
			parameterStringBuilder.append(urlEncodedValueForParameterName(
					parameter.name, parameter.value));
		}

		return parameterStringBuilder.toString();
	}

	@Override
	protected String createEndpointForApiCall(String apiCall) {
		trimToEmpty(apiCall).toLowerCase();
		while (apiCall.startsWith("/")) {
			apiCall = apiCall.substring(1);
		}

		final String baseUrl = INSTAGRAM_GRAPH_ENDPOINT_URL;

		return String.format("%s/%s", baseUrl, apiCall);
	}

	/**
	 * Returns an {@code Integer} representation of the given {@code string}, or
	 * {@code null} if it's not a valid {@code Integer}.
	 * 
	 * @param string
	 *            The string to process.
	 * @return The {@code Integer} representation of {@code string}, or
	 *         {@code null} if {@code string} is {@code null} or not a valid
	 *         {@code Integer}.
	 */
	private Integer toInteger(final String string) {
		Integer returnValue = null;
		if (string != null) {
			try {
				returnValue = Integer.parseInt(string);
			} catch (final Exception e) {
				returnValue = null;
			}
		}

		return returnValue;
	}

	@Override
	public boolean isSessionValid() {
		return mAccessToken != null;
	}

}