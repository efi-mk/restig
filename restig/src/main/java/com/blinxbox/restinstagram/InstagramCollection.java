/**
 * 
 */
package com.blinxbox.restinstagram;

import static com.restfb.util.StringUtils.isBlank;
import static java.text.MessageFormat.format;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.blinxbox.restinstagram.exception.InstagramJsonMappingException;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonException;
import com.restfb.json.JsonObject;
import com.restfb.util.ReflectionUtils;

/**
 * Holds a collection of Instagram objects. You can access the list directly by
 * using getData. In addition it supports the ability to view the next page via
 * 
 * @author Efi MK
 * @param <T>
 *            - The collection holds this value.
 */
public class InstagramCollection<T> {
	/**
	 * The instagram client used for fetching insformation.
	 */
	private final InstagramClient mClient;
	/**
	 * Holds the actual data fetched.
	 */
	private final List<T> data;
	/**
	 * Holds next page that can be fetched.
	 */
	private String nextPageUrl;

	/**
	 * Creates a connection with the given {@code jsonObject}.
	 * 
	 * @param defaultInstagramClient
	 *            The {@code InstagramClient} used to fetch additional pages and
	 *            map data to JSON objects.
	 * @param json
	 *            Raw JSON which must include a {@code data} field that holds a
	 *            JSON array and optionally a {@code paging} field that holds a
	 *            JSON object with next/previous page URLs.
	 * @param type
	 *            Object type.
	 * @throws InstagramJsonMappingException
	 *             If the provided {@code json} is invalid.
	 */
	public InstagramCollection(final InstagramClient defaultInstagramClient,
			final String json, final Class<T> type)
			throws InstagramJsonMappingException {
		final List<T> data = new ArrayList<T>();

		this.mClient = defaultInstagramClient;
		if (json == null) {
			throw new InstagramJsonMappingException(
					"You must supply non-null connection JSON.");
		}

		JsonObject jsonObject = null;

		try {
			jsonObject = new JsonObject(json);
		} catch (final JsonException e) {
			throw new InstagramJsonMappingException(format(
					"The connection JSON you provided was invalid: {0}", json),
					e);
		}

		// Pull out data
		final JsonArray jsonData = jsonObject.getJsonArray("data");
		for (int i = 0; i < jsonData.length(); i++) {
			data.add(type.equals(JsonObject.class) ? (T) jsonData.get(i)
					: mClient.getJsonMapper().toJavaObject(
							jsonData.get(i).toString(), type));
		}

		// Pull out paging info, if present
		if (jsonObject.has("pagination")) {
			final JsonObject jsonPaging = jsonObject
					.getJsonObject("pagination");

			nextPageUrl = jsonPaging.has("next_url") ? jsonPaging
					.getString("next_url") : null;
		} else {
			nextPageUrl = null;
		}

		this.data = unmodifiableList(data);

	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}

	@Override
	public boolean equals(final Object object) {
		return ReflectionUtils.equals(this, object);
	}

	@Override
	public int hashCode() {
		return ReflectionUtils.hashCode(this);
	}

	/**
	 * Data for this connection.
	 * 
	 * @return Data for this connection.
	 */
	public List<T> getData() {
		return data;
	}

	/**
	 * This connection's "next page of data" URL.
	 * 
	 * @return This connection's "next page of data" URL, or {@code null} if
	 *         there is no next page.
	 */
	public String getNextPageUrl() {
		return nextPageUrl;
	}

	/**
	 * Does this connection have a next page of data?
	 * 
	 * @return {@code true} if there is a next page of data for this connection,
	 *         {@code false} otherwise.
	 */
	public boolean hasNext() {
		return !isBlank(getNextPageUrl());
	}

}
