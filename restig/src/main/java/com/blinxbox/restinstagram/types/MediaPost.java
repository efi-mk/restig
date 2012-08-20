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

package com.blinxbox.restinstagram.types;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.blinxbox.restinstagram.Instagram;

/**
 * Represents a post in Instagram.
 * 
 * @author Efi MK
 */
/**
 * @author Efi MK
 * 
 */
public class MediaPost implements Serializable {

	/**
	 * Represents an Instagram caption section.
	 * 
	 * @author Efi MK
	 * 
	 */
	public static class Likes implements Serializable {
		/**
		 * Text of the comment.
		 */
		@Instagram
		int count;

		/**
		 * @return Number of likes.
		 */
		public int getCount() {
			return count;
		}
	}

	/**
	 * Represents an Instagram caption section.
	 * 
	 * @author Efi MK
	 * 
	 */
	public static class Caption implements Serializable {
		/**
		 * Text of the comment.
		 */
		@Instagram
		String text;

		/**
		 * Caption id.
		 */
		@Instagram
		String id;

		/**
		 * @return Text of the comment.
		 */
		public String getText() {
			return text;
		}

		/**
		 * @return Caption id.
		 */
		public String getId() {
			return id;
		}

		public Caption() {
		}

		Caption(final String text, final String id) {
			this.text = text;
			this.id = id;

		}

	}

	/**
	 * Represents an Instagram user.
	 * 
	 * @author Efi MK
	 * 
	 */
	public static class User implements Serializable {

		/**
		 * For serialization.
		 */
		private static final long serialVersionUID = -9139733728844823420L;

		/**
		 * A link to the profile picture of the user.
		 */
		@Instagram
		String profile_picture;
		/**
		 * User ID.
		 */
		@Instagram
		String id;
		/**
		 * Full name of the user.
		 */
		@Instagram
		String full_name;

		/**
		 * User name.
		 */
		@Instagram
		String username;

		/**
		 * @return A link to the profile picture of the user.
		 */
		public String getProfilePicture() {
			return profile_picture;
		}

		/**
		 * @return User ID.
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return Full name of the user.
		 */
		public String getFullName() {
			return full_name;
		}

		/**
		 * @return User name.
		 */
		public String getUserName() {
			return username;
		}
	}

	/**
	 * Represents an image in Instagram. There are 3 types of images: <li>
	 * low_resolution - with size of 306 * 306. <li>thumbnail - with size of 150
	 * * 150. <li>standard_resolution - with size of 612 * 612.
	 * 
	 * Check the size by using {@link #getHeight()} and {@link #getWidth()}
	 * 
	 * @author Efi MK
	 * 
	 */
	public static class Image implements Serializable {
		/**
		 * Used for serialization.
		 */
		private static final long serialVersionUID = 5811101144604271107L;

		/**
		 * Width of the image.
		 */
		@Instagram
		private int width;

		/**
		 * Height of the image.
		 */
		@Instagram
		private int height;

		/**
		 * From where the image can be retrieved.
		 */
		@Instagram
		private String url;

		/**
		 * @return Width of the image.
		 */
		public int getWidth() {
			return width;
		}

		/**
		 * @return Height of the image.
		 */
		public int getHeight() {
			return height;
		}

		/**
		 * @return From where the image can be retrieved.
		 */
		public String getUrl() {
			return url;
		}

	}

	/**
	 * Holds the images object. There are 3 types of images: <li>
	 * low_resolution - with size of 306 * 306. <li>thumbnail - with size of 150
	 * * 150. <li>standard_resolution - with size of 612 * 612.
	 * 
	 * @author Efi MK
	 * 
	 */
	public static class Images implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7940466671705419507L;

		/**
		 * Low resolution image 306*305
		 */
		@Instagram
		private Image low_resolution;

		/**
		 * thumnail image. 150 *150
		 */
		@Instagram
		private Image thumbnail;

		/**
		 * standard image. 612 * 612
		 */
		@Instagram
		private Image standard_resolution;

		/**
		 * @return Low resolution image 306*305
		 */
		public Image getLowResolution() {
			return low_resolution;
		}

		/**
		 * @return thumnail image. 150 *150
		 */
		public Image getThumbnail() {
			return thumbnail;
		}

		/**
		 * @return standard image. 612 * 612
		 */
		public Image getStandardResolution() {
			return standard_resolution;
		}

	}

	/**
	 * Each post has one image with 3 resolutions. This variable holds pointers
	 * to the images.
	 */
	@Instagram
	private Images images;

	/**
	 * Time since epoch this post was created.
	 */
	@Instagram
	private long created_time;
	/**
	 * User who posted this post.
	 */
	@Instagram
	private User user;

	/**
	 * Link to the post.
	 */
	@Instagram
	private String link;

	/**
	 * Caption for the post.
	 */
	@Instagram
	private Caption caption;

	/**
	 * Likes for the current post.
	 */
	@Instagram
	private Likes likes;

	/**
	 * Post ID.
	 */
	@Instagram
	private String id;

	/**
	 * Holds post tags.
	 */
	@Instagram
	private List<String> tags;

	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = 2L;

	/**
	 * @return Each post has one image with 3 resolutions. Returns these 3
	 *         resolutions.
	 */
	public Images getImages() {
		return images;
	}

	/**
	 * @return Time since epoch this post was created. In seconds.
	 */
	public Date getCreatedTime() {
		return new Date(created_time);
	}

	/**
	 * @return User who posted this post.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @return Caption for the post. In case the post does not have a caption
	 *         return an empty (not null).
	 */
	public Caption getCaption() {
		Caption returnValue;
		if (caption == null) {
			returnValue = new Caption("", "99999");
		} else {
			returnValue = caption;
		}
		return returnValue;
	}

	/**
	 * @return Link to the post.
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @return Post ID.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return Likes for the current post.
	 */
	public Likes getLikes() {
		return likes;
	}

	/**
	 * @return Holds post tags.
	 */
	public List<String> getTags() {
		return tags;
	}

}