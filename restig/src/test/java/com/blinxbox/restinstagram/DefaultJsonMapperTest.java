/**
 * 
 */
package com.blinxbox.restinstagram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.blinxbox.restinstagram.types.MediaPost;

/**
 * @author Efi MK
 * 
 */
public class DefaultJsonMapperTest {

	/**
	 * A single post response.
	 */
	public static final String POST_RESPONSE = "{\"attribution\":null,\"tags\":[\"snow\"],\"location\":null,\"comments\":{\"count\":0,\"data\":[]},\"filter\":\"Normal\",\"created_time\":\"1333643413\",\"link\":\"http://instagr.am/p/JC3LhCRmYY/\",\"likes\":{\"count\":0,\"data\":[]},\"images\":{\"low_resolution\":{\"url\":\"http://distilleryimage8.s3.amazonaws.com/9ebeefb47f3c11e1abb01231381b65e3_6.jpg\",\"width\":306,\"height\":306},\"thumbnail\":{\"url\":\"http://distilleryimage8.s3.amazonaws.com/9ebeefb47f3c11e1abb01231381b65e3_5.jpg\",\"width\":150,\"height\":150},\"standard_resolution\":{\"url\":\"http://distilleryimage8.s3.amazonaws.com/9ebeefb47f3c11e1abb01231381b65e3_7.jpg\",\"width\":612,\"height\":612}},\"caption\":{\"created_time\":\"1337940746\",\"text\":\"#snow\",\"from\":{\"username\":\"_antonio\",\"profile_picture\":\"http://images.instagram.com/profiles/profile_33299504_75sq_1333639198.jpg\",\"id\":\"33299504\",\"full_name\":\"?????\"},\"id\":\"198983862188402309\"},\"type\":\"image\",\"id\":\"162935220482762264_33299504\",\"user\":{\"username\":\"_antonio\",\"website\":\"\",\"bio\":\"\",\"profile_picture\":\"http://images.instagram.com/profiles/profile_33299504_75sq_1333639198.jpg\",\"full_name\":\"?????\",\"id\":\"33299504\"}}";

	/**
	 * Can we handle the empty list?
	 */
	@Test
	public void emptyList() {
		final DefaultJsonMapper jsonMapper = new DefaultJsonMapper();

		final List<Object> objects = jsonMapper.toJavaList("[]", Object.class);
		Assert.assertTrue(objects.size() == 0);
	}

	/**
	 * Can we handle the empty object?
	 */
	@Test
	public void emptyObject() {
		final DefaultJsonMapper jsonMapper = new DefaultJsonMapper();
		final Object object = jsonMapper.toJavaObject("{}", Object.class);
		Assert.assertTrue(object != null);
	}

	/**
	 * Can we handle a valid post objec.t .
	 */
	@Test
	public void toJavaObject_AValidJsonPost_PostReturned() {
		final DefaultJsonMapper jsonMapper = new DefaultJsonMapper();
		final MediaPost post = jsonMapper.toJavaObject(POST_RESPONSE,
				MediaPost.class);

		assertNotNull(post);
		assertEquals(0, post.getLikes().getCount());
	}

}
