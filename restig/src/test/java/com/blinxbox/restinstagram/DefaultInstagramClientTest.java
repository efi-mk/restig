/**
 * 
 */
package com.blinxbox.restinstagram;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.blinxbox.restinstagram.DefaultInstagramClient.Requestor;
import com.blinxbox.restinstagram.exception.InstagramException;
import com.blinxbox.restinstagram.exception.InstagramNetworkException;
import com.blinxbox.restinstagram.types.MediaPost;
import com.restfb.WebRequestor;
import com.restfb.WebRequestor.Response;

/**
 * @author Efi MK
 * 
 */
public class DefaultInstagramClientTest {

	/**
	 * Response from tag request.
	 */
	private static final String TAG_RESPONSE = "{\"meta\":  {\"code\": 200},\"data\": {\"media_count\": 472,\"name\": \"nofilter\",}}";

	/**
	 * A single object response.
	 */
	private static final String OBJECT_FETCH = "{\"meta\":{\"code\":200},\"data\":{\"attribution\":null,\"tags\":[\"snow\"],\"location\":null,\"comments\":{\"count\":0,\"data\":[]},\"filter\":\"Normal\",\"created_time\":\"1333643413\",\"link\":\"http://instagr.am/p/JC3LhCRmYY/\",\"likes\":{\"count\":0,\"data\":[]},\"images\":{\"low_resolution\":{\"url\":\"http://distilleryimage8.s3.amazonaws.com/9ebeefb47f3c11e1abb01231381b65e3_6.jpg\",\"width\":306,\"height\":306},\"thumbnail\":{\"url\":\"http://distilleryimage8.s3.amazonaws.com/9ebeefb47f3c11e1abb01231381b65e3_5.jpg\",\"width\":150,\"height\":150},\"standard_resolution\":{\"url\":\"http://distilleryimage8.s3.amazonaws.com/9ebeefb47f3c11e1abb01231381b65e3_7.jpg\",\"width\":612,\"height\":612}},\"caption\":{\"created_time\":\"1337940746\",\"text\":\"#snow\",\"from\":{\"username\":\"_antonio\",\"profile_picture\":\"http://images.instagram.com/profiles/profile_33299504_75sq_1333639198.jpg\",\"id\":\"33299504\",\"full_name\":\"?????\"},\"id\":\"198983862188402309\"},\"type\":\"image\",\"id\":\"162935220482762264_33299504\",\"user\":{\"username\":\"_antonio\",\"website\":\"\",\"bio\":\"\",\"profile_picture\":\"http://images.instagram.com/profiles/profile_33299504_75sq_1333639198.jpg\",\"full_name\":\"?????\",\"id\":\"33299504\"}}}";
	/**
	 * A mock web requester.
	 */
	private WebRequestor mWebRequester;
	/**
	 * A mock json mapper.
	 */
	private JsonMapper mJsonMapper;
	/**
	 * A default parameter.
	 */
	private Parameter mParam;

	/**
	 * @throws java.lang.Exception
	 *             - Ignore.
	 */
	@Before
	public void setUp() throws Exception {
		mWebRequester = mock(WebRequestor.class);
		mJsonMapper = mock(JsonMapper.class);
		mParam = new Parameter("name", "value");
	}

	/**
	 * Test method for
	 * {@link com.blinxbox.restinstagram.DefaultInstagramClient#createEndpointForApiCall(java.lang.String)}
	 * .
	 */
	@Test
	public void createEndpointForApiCall_StartWithSlash_SlashRemoved() {
		final DefaultInstagramClient client = new DefaultInstagramClient(
				"Token");
		final String endPoint = client
				.createEndpointForApiCall("/tag/mytag/media");
		assertEquals(String.format("%s/%s",
				DefaultInstagramClient.INSTAGRAM_GRAPH_ENDPOINT_URL,
				"tag/mytag/media"), endPoint);
	}

	/**
	 * Test method for
	 * {@link com.blinxbox.restinstagram.DefaultInstagramClient#createEndpointForApiCall(java.lang.String)}
	 * .
	 */
	@Test
	public void createEndpointForApiCall_NoStartWithSlash_SameStringReturned() {
		final DefaultInstagramClient client = new DefaultInstagramClient(
				"Token");
		final String endPoint = client
				.createEndpointForApiCall("tag/mytag/media");
		assertEquals(String.format("%s/%s",
				DefaultInstagramClient.INSTAGRAM_GRAPH_ENDPOINT_URL,
				"tag/mytag/media"), endPoint);
	}

	/**
	 * Test method for
	 * {@link com.blinxbox.restinstagram.DefaultInstagramClient#fetchCollection(java.lang.String, java.lang.Class, com.blinxbox.restinstagram.Parameter[])}
	 * .
	 * 
	 * @throws IOException
	 *             - Ignore.
	 */
	@Test
	public void fetchObject_ValidResponse_JsonIsStrippedFromMetaTag()
			throws IOException {
		final DefaultInstagramClient client = new DefaultInstagramClient(
				"Client", "Access", mWebRequester, mJsonMapper);
		final Response response = mock(Response.class);
		when(response.getBody()).thenReturn(OBJECT_FETCH);
		when(response.getStatusCode()).thenReturn(HTTP_OK);
		when(mWebRequester.executeGet(anyString())).thenReturn(response);

		client.fetchObject("endpoint", MediaPost.class);

		final Class<MediaPost> myClass = MediaPost.class;
		verify(mJsonMapper).toJavaObject(
				contains("link\":\"http://instagr.am/p/JC3LhCRmYY/"),
				eq(myClass));
	}

	/**
	 * Test method for
	 * {@link com.blinxbox.restinstagram.DefaultInstagramClient#makeRequestAndProcessResponse(com.blinxbox.restinstagram.DefaultInstagramClient.Requestor)}
	 * .
	 * 
	 * @throws IOException
	 *             - ignore
	 */
	@Test(expected = InstagramNetworkException.class)
	public void makeRequestAndProcessResponse_InvalidResponse_ExceptionThrown()
			throws IOException {
		final DefaultInstagramClient client = new DefaultInstagramClient(
				"ClientId", null, mWebRequester, mJsonMapper);
		final Requestor requestor = mock(Requestor.class);
		final Response response = mock(Response.class);
		when(response.getStatusCode()).thenReturn(401);
		when(requestor.makeRequest()).thenReturn(response);

		client.makeRequestAndProcessResponse(requestor);
	}

	/**
	 * Test method for
	 * {@link com.blinxbox.restinstagram.DefaultInstagramClient#makeRequestAndProcessResponse(com.blinxbox.restinstagram.DefaultInstagramClient.Requestor)}
	 * .
	 * 
	 * @throws IOException
	 *             - ignore
	 */
	public void makeRequestAndProcessResponse_ValidResponse_NoExceptionThrown()
			throws IOException {
		final DefaultInstagramClient client = new DefaultInstagramClient(
				"ClientId", null, mWebRequester, mJsonMapper);
		final Requestor requestor = mock(Requestor.class);
		final Response response = mock(Response.class);
		when(response.getStatusCode()).thenReturn(200);
		when(response.getBody()).thenReturn(TAG_RESPONSE);
		when(requestor.makeRequest()).thenReturn(response);

		final String makeRequest = client
				.makeRequestAndProcessResponse(requestor);
		assertEquals(TAG_RESPONSE, makeRequest);
	}

	/**
	 * Test method for
	 * {@link com.blinxbox.restinstagram.DefaultInstagramClient#throwFacebookResponseStatusExceptionIfNecessary(java.lang.String)}
	 * .
	 */
	@Test
	public void throwFacebookResponseStatusExceptionIfNecessary_NoErrorInJson_NoExceptionThrown() {
		final DefaultInstagramClient client = new DefaultInstagramClient(
				"Token");
		final String noError = TAG_RESPONSE;
		client.throwFacebookResponseStatusExceptionIfNecessary(noError);
	}

	/**
	 * Test method for
	 * {@link com.blinxbox.restinstagram.DefaultInstagramClient#throwFacebookResponseStatusExceptionIfNecessary(java.lang.String)}
	 * .
	 */
	@Test(expected = InstagramException.class)
	public void throwFacebookResponseStatusExceptionIfNecessary_ErrorInJson_ExceptionThrown() {
		final DefaultInstagramClient client = new DefaultInstagramClient(
				"Token");
		final String error = "{\"meta\": {\"error_type\": \"OAuthException\",\"code\": 400,\"error_message\": \"Error\"}}";

		client.throwFacebookResponseStatusExceptionIfNecessary(error);

	}

	/**
	 * Test method for
	 * {@link com.blinxbox.restinstagram.DefaultInstagramClient#toParameterString(com.blinxbox.restinstagram.Parameter[])}
	 * .
	 */
	@Test
	public void toParameterString_AccessTokenNotEmpty_ClientIdNotAppearing() {
		final DefaultInstagramClient client = new DefaultInstagramClient(
				"AppId", "Access");

		final String paramString = client.toParameterString(mParam, mParam);
		assertEquals("name=value&name=value&access_token=Access", paramString);
	}

	/**
	 * Test method for
	 * {@link com.blinxbox.restinstagram.DefaultInstagramClient#toParameterString(com.blinxbox.restinstagram.Parameter[])}
	 * .
	 */
	@Test
	public void toParameterString_AccessTokenEmpty_ClientIdAppearing() {
		final DefaultInstagramClient client = new DefaultInstagramClient(
				"AppId");

		final String paramString = client.toParameterString(mParam, mParam);
		assertEquals("name=value&name=value&client_id=AppId", paramString);
	}
}
