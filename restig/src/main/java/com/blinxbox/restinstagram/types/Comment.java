/**
 * 
 */
package com.blinxbox.restinstagram.types;

import java.io.Serializable;
import java.util.Date;

import com.blinxbox.restinstagram.Instagram;
import com.blinxbox.restinstagram.types.MediaPost.User;

/**
 * Represents a collection of comments.
 * 
 * @author Efi MK
 * 
 */
public class Comment implements Serializable {

	/**
	 * Mandatory for serialization.
	 */
	private static final long serialVersionUID = -7604860147284867603L;

	/**
	 * Text of the comment.
	 */
	@Instagram
	String text;

	/**
	 * Comment wrote by this user.
	 */
	@Instagram
	User from;

	/**
	 * Time since epoch this post was created.
	 */
	@Instagram
	long created_time;

	/**
	 * @return Text of the comment.
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return Comment wrote by this user.
	 */
	public User getFrom() {
		return from;
	}

	/**
	 * @return Time since epoch this post was created. In seconds.
	 */
	public Date getCreatedTime() {
		return new Date(created_time);
	}
}
