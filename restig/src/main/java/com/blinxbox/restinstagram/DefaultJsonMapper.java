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

import static com.restfb.json.JsonObject.NULL;
import static com.restfb.util.ReflectionUtils.findFieldsWithAnnotation;
import static com.restfb.util.ReflectionUtils.getFirstParameterizedTypeArgument;
import static com.restfb.util.StringUtils.isBlank;
import static com.restfb.util.StringUtils.trimToEmpty;
import static java.text.MessageFormat.format;
import static java.util.Collections.unmodifiableList;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.blinxbox.restinstagram.exception.InstagramJsonMappingException;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.util.ReflectionUtils.FieldWithAnnotation;

/**
 * Default implementation of a JSON-to-Java mapper.
 * 
 * @author Efi MK
 */
public class DefaultJsonMapper implements JsonMapper {
	/**
	 * We call this instance's
	 * {@link JsonMappingErrorHandler#handleMappingError(String)} method on
	 * mapping failure so client code can decide how to handle the problem.
	 */
	protected JsonMappingErrorHandler mJsonMappingErrorHandler;

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(DefaultJsonMapper.class.getName());

	/**
	 * Creates a JSON mapper which will throw
	 * {@link com.blinxbox.restinstagram.exception.InstagramJsonMappingException}
	 * whenever an error occurs when mapping JSON data to Java objects.
	 */
	public DefaultJsonMapper() {
		this(new JsonMappingErrorHandler() {
			/**
			 * @see com.blinxbox.restinstagram.DefaultJsonMapper.JsonMappingErrorHandler#handleMappingError(java.lang.String,
			 *      java.lang.Class, java.lang.Exception)
			 */
			@Override
			public boolean handleMappingError(final String unmappableJson,
					final Class<?> targetType, final Exception exception) {
				return false;
			}
		});
	}

	/**
	 * Creates a JSON mapper which delegates to the provided
	 * {@code jsonMappingErrorHandler} for handling mapping errors.
	 * 
	 * @param jsonMappingErrorHandler
	 *            The JSON mapping error handler to use.
	 * @throws IllegalArgumentException
	 *             If {@code jsonMappingErrorHandler} is {@code null}.
	 */
	public DefaultJsonMapper(
			final JsonMappingErrorHandler jsonMappingErrorHandler)
			throws IllegalArgumentException {
		if (jsonMappingErrorHandler == null) {
			throw new IllegalArgumentException(
					"The jsonMappingErrorHandler parameter cannot be null.");
		}

		this.mJsonMappingErrorHandler = jsonMappingErrorHandler;
	}

	@Override
	public <T> List<T> toJavaList(String json, final Class<T> type) {
		if (type == null) {
			throw new InstagramJsonMappingException(
					"You must specify the Java type to map to.");
		}

		json = trimToEmpty(json);

		if (isBlank(json)) {
			if (mJsonMappingErrorHandler.handleMappingError(json, type, null)) {
				return null;
			}
			throw new InstagramJsonMappingException(
					"JSON is an empty string - can't map it.");
		}

		List<T> list = new ArrayList<T>();
		try {

			final JsonArray jsonArray = new JsonArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add(toJavaObject(jsonArray.get(i).toString(), type));
			}

			list = unmodifiableList(list);
		} catch (final InstagramJsonMappingException e) {
			throw e;
		} catch (final Exception e) {
			if (mJsonMappingErrorHandler.handleMappingError(json, type, e)) {
				list = null;
			} else {
				throw new InstagramJsonMappingException(
						"Unable to convert Instagram response "
								+ "JSON to a list of " + type.getName()
								+ " instances", e);
			}
		}

		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T toJavaObject(final String json, final Class<T> type) {
		if (isBlank(json)) {
			return handleError(json, type,
					"JSON is an empty string - can't map it.", null);
		}

		// Means it's an array and not a regular object.
		if (json.startsWith("[")) {
			return handleError(
					json,
					type,
					format("JSON is an array but is being mapped as an object - you should map it as a List instead. Offending JSON is ''{0}''.",
							json), null);
		}

		try {
			// Are we asked to map to JsonObject? If so, short-circuit right
			// away.
			if (type.equals(JsonObject.class)) {
				return (T) new JsonObject(json);
			}

			final List<FieldWithAnnotation<Instagram>> fieldsWithAnnotation = findFieldsWithAnnotation(
					type, Instagram.class);
			// If there are no annotated fields, assume we're mapping to a
			// built-in
			// type. If this is actually the empty object, just return a new
			// instance
			// of the corresponding Java type.
			if (fieldsWithAnnotation.size() == 0) {
				if (isEmptyObject(json)) {
					return createInstance(type);
				} else {
					return toPrimitiveJavaType(json, type);
				}
			}

			final JsonObject jsonObject = new JsonObject(json);
			final T instance = createInstance(type);

			// For each Instagram-annotated field on the current Java object,
			// pull data
			// out of the JSON object and put it in the Java object
			for (final FieldWithAnnotation<Instagram> fieldWithAnnotation : fieldsWithAnnotation) {
				final String instagramFieldName = getInstagramFieldName(fieldWithAnnotation);

				if (!jsonObject.has(instagramFieldName)) {
					if (LOGGER.isLoggable(FINER)) {
						LOGGER.finer(format(
								"No JSON value present for ''{0}'', skipping. JSON is ''{1}''.",
								instagramFieldName, json));
					}

					continue;
				}

				fieldWithAnnotation.getField().setAccessible(true);

				try {
					fieldWithAnnotation.getField().set(
							instance,
							toJavaType(fieldWithAnnotation, jsonObject,
									instagramFieldName));
				} catch (final Exception e) {
					if (!mJsonMappingErrorHandler.handleMappingError(json,
							type, e)) {
						throw e;
					}
				}
			}

			return instance;
		} catch (final InstagramJsonMappingException exception) {
			throw exception;
		} catch (final Exception exception) {
			return handleError(
					json,
					type,
					format("Unable to map JSON to Java. Offending JSON is ''{0}''.",
							json), exception);

		}
	}

	/**
	 * Check whether an external error handler wants to handle this error.
	 * 
	 * @param <T>
	 *            - Type of object.
	 * @param json
	 *            - Json string to handle.
	 * @param type
	 *            - Type of the object to convert to.
	 * @param errorMessage
	 *            - Description to use in the thrown exception.
	 * @param exception
	 *            - Any exception that should be included in the message.
	 * @return Null in case the external error handler handles the error,
	 *         otherwise an exception is thrown.
	 * @throws InstagramJsonMappingException
	 *             - Error while parsing Json.
	 */
	private <T> T handleError(final String json, final Class<T> type,
			final String errorMessage, final Exception exception)
			throws InstagramJsonMappingException {
		if (mJsonMappingErrorHandler.handleMappingError(json, type, null)) {
			return null;
		} else {

			throw new InstagramJsonMappingException(errorMessage, exception);
		}
	}

	/**
	 * Dumps out a log message when one of a multiple-mapped Instagram field
	 * name JSON-to-Java mapping operation fails.
	 * 
	 * @param instagramFieldName
	 *            The Instagram field name.
	 * @param fieldWithAnnotation
	 *            The Java field to map to and its annotation.
	 * @param json
	 *            The JSON that failed to map to the Java field.
	 */
	protected void logMultipleMappingFailedForField(
			final String instagramFieldName,
			final FieldWithAnnotation<Instagram> fieldWithAnnotation,
			final String json) {
		if (!LOGGER.isLoggable(FINER)) {
			return;
		}

		final Field field = fieldWithAnnotation.getField();

		if (LOGGER.isLoggable(FINER)) {
			LOGGER.finer("Could not map '" + instagramFieldName + "' to "
					+ field.getDeclaringClass().getSimpleName() + "."
					+ field.getName() + ", but continuing on because '"
					+ instagramFieldName + "' is mapped to multiple fields in "
					+ field.getDeclaringClass().getSimpleName() + ". JSON is "
					+ json);
		}
	}

	/**
	 * For a Java field annotated with the {@code Instagram} annotation, figure
	 * out what the corresponding Instagram JSON field name to map to it is.
	 * 
	 * @param fieldWithAnnotation
	 *            A Java field annotated with the {@code Instagram} annotation.
	 * @return The Instagram JSON field name that should be mapped to this Java
	 *         field.
	 */
	protected String getInstagramFieldName(
			final FieldWithAnnotation<Instagram> fieldWithAnnotation) {
		String instagramFieldName = fieldWithAnnotation.getAnnotation().value();
		final Field field = fieldWithAnnotation.getField();

		// If no Instagram field name was specified in the annotation, assume
		// it's the same name as the Java field
		if (isBlank(instagramFieldName)) {
			if (LOGGER.isLoggable(FINEST)) {
				LOGGER.finest("No explicit Instagram field name found for "
						+ field + ", so defaulting to the field name itself ("
						+ field.getName() + ")");
			}

			instagramFieldName = field.getName();
		}

		return instagramFieldName;
	}

	@Override
	public String toJson(final Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toJson(final Object object,
			final boolean ignoreNullValuedProperties) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Given a {@code json} value of something like {@code MyValue} or
	 * {@code 123} , return a representation of that value of type {@code type}.
	 * <p>
	 * This is to support non-legal JSON served up by Instagram for API calls
	 * like {@code Friends.get} (example result: {@code [222333,1240079]}).
	 * 
	 * @param <T>
	 *            The Java type to map to.
	 * @param json
	 *            The non-legal JSON to map to the Java type.
	 * @param type
	 *            Type token.
	 * @return Java representation of {@code json}.
	 * @throws InstagramJsonMappingException
	 *             If an error occurs while mapping JSON to Java.
	 */
	@SuppressWarnings("unchecked")
	protected <T> T toPrimitiveJavaType(String json, final Class<T> type)
			throws InstagramJsonMappingException {

		if (String.class.equals(type)) {
			// If the string starts and ends with quotes, remove them, since
			// Instagram
			// can serve up strings surrounded by quotes.
			if (json.length() > 1 && json.startsWith("\"")
					&& json.endsWith("\"")) {
				json = json.replaceFirst("\"", "");
				json = json.substring(0, json.length() - 1);
			}

			return (T) json;
		}

		if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
			return (T) new Integer(json);
		}
		if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
			return (T) new Boolean(json);
		}
		if (Long.class.equals(type) || Long.TYPE.equals(type)) {
			return (T) new Long(json);
		}
		if (Double.class.equals(type) || Double.TYPE.equals(type)) {
			return (T) new Double(json);
		}
		if (Float.class.equals(type) || Float.TYPE.equals(type)) {
			return (T) new Float(json);
		}
		if (BigInteger.class.equals(type)) {
			return (T) new BigInteger(json);
		}
		if (BigDecimal.class.equals(type)) {
			return (T) new BigDecimal(json);
		}

		if (mJsonMappingErrorHandler.handleMappingError(json, type, null)) {
			return null;
		}

		throw new InstagramJsonMappingException(
				format("Don''t know how to map JSON to {0}. Are you sure you''re mapping to the right class? Offending JSON is ''{1}''.",
						type, json));
	}

	/**
	 * Extracts JSON data for a field according to its {@code Instagram}
	 * annotation and returns it converted to the proper Java type.
	 * 
	 * @param fieldWithAnnotation
	 *            The field/annotation pair which specifies what Java type to
	 *            convert to.
	 * @param jsonObject
	 *            "Raw" JSON object to pull data from.
	 * @param instagramFieldName
	 *            Specifies what JSON field to pull "raw" data from.
	 * @return A new object that represent the appropriate java type.
	 * @throws InstagramJsonMappingException
	 *             If an error occurs while mapping JSON to Java.
	 */
	protected Object toJavaType(
			final FieldWithAnnotation<Instagram> fieldWithAnnotation,
			final JsonObject jsonObject, final String instagramFieldName)
			throws InstagramJsonMappingException {
		final Class<?> type = fieldWithAnnotation.getField().getType();
		final Object rawValue = jsonObject.get(instagramFieldName);

		// Short-circuit right off the bat if we've got a null value.
		if (NULL.equals(rawValue)) {
			return null;
		}

		if (String.class.equals(type)) {
			// Special handling here for better error checking.
			// Since JsonObject.getString() will return literal JSON text even
			// if it's
			// _not_ a JSON string, we check the marshaled type and bail if
			// needed.
			// For example, calling JsonObject.getString("results") on the below
			// JSON...
			// {"results":[{"name":"Mark Allen"}]}
			// ... would return the string "[{"name":"Mark Allen"}]" instead of
			// throwing an error. So we throw the error ourselves.

			// Per Antonello Naccarato, sometimes FB will return an empty JSON
			// array
			// instead of an empty string. Look for that here.
			if (rawValue instanceof JsonArray) {
				if (((JsonArray) rawValue).length() == 0
						&& LOGGER.isLoggable(FINER)) {

					LOGGER.finer("Coercing an empty JSON array "
							+ "to an empty string for " + fieldWithAnnotation);

					return "";
				}
			}

			// If the user wants a string, _always_ give her a string.
			// This is useful if, for example, you've got a @Instagram-annotated
			// string
			// field that you'd like to have a numeric type shoved into.
			// User beware: this will turn *anything* into a string, which might
			// lead
			// to results you don't expect.
			return rawValue.toString();
		}

		if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
			return new Integer(jsonObject.getInt(instagramFieldName));
		}
		if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
			return new Boolean(jsonObject.getBoolean(instagramFieldName));
		}
		if (Long.class.equals(type) || Long.TYPE.equals(type)) {
			return new Long(jsonObject.getLong(instagramFieldName));
		}
		if (Double.class.equals(type) || Double.TYPE.equals(type)) {
			return new Double(jsonObject.getDouble(instagramFieldName));
		}
		if (Float.class.equals(type) || Float.TYPE.equals(type)) {
			return new BigDecimal(jsonObject.getString(instagramFieldName))
					.floatValue();
		}
		if (BigInteger.class.equals(type)) {
			return new BigInteger(jsonObject.getString(instagramFieldName));
		}
		if (BigDecimal.class.equals(type)) {
			return new BigDecimal(jsonObject.getString(instagramFieldName));
		}
		if (List.class.equals(type)) {
			return toJavaList(rawValue.toString(),
					getFirstParameterizedTypeArgument(fieldWithAnnotation
							.getField()));
		}

		final String rawValueAsString = rawValue.toString();

		// Some other type - recurse into it
		return toJavaObject(rawValueAsString, type);
	}

	/**
	 * Creates a new instance of the given {@code type}.
	 * <p>
	 * 
	 * 
	 * @param <T>
	 *            Java type to map to.
	 * @param type
	 *            Type token.
	 * @return A new instance of {@code type}.
	 * @throws InstagramJsonMappingException
	 *             If an error occurs when creating a new instance ({@code type}
	 *             is inaccessible, doesn't have a no-arg constructor, etc.)
	 */
	protected <T> T createInstance(final Class<T> type)
			throws InstagramJsonMappingException {
		final String errorMessage = "Unable to create an instance of "
				+ type
				+ ". Please make sure that if it's a nested class, is marked 'static'. "
				+ "It should have a no-argument constructor.";

		try {
			final Constructor<T> defaultConstructor = type
					.getDeclaredConstructor();

			if (defaultConstructor == null) {
				throw new InstagramJsonMappingException(
						"Unable to find a default constructor for " + type);
			}

			// Allows protected, private, and package-private constructors to be
			// invoked
			defaultConstructor.setAccessible(true);
			return defaultConstructor.newInstance();
		} catch (final Exception e) {
			throw new InstagramJsonMappingException(errorMessage, e);
		}
	}

	/**
	 * Is the given JSON equivalent to the empty object (<code>{}</code>)?
	 * 
	 * @param json
	 *            The JSON to check.
	 * @return {@code true} if the JSON is equivalent to the empty object,
	 *         {@code false} otherwise.
	 */
	protected boolean isEmptyObject(final String json) {
		return "{}".equals(json);
	}

	/**
	 * Callback interface which allows client code to specify how JSON mapping
	 * errors should be handled.
	 * 
	 * @author Efi MK
	 */
	public static interface JsonMappingErrorHandler {
		/**
		 * This method will be called by {@code DefaultJsonMapper} if it
		 * encounters an error while attempting to map JSON to a Java object.
		 * <p>
		 * You may perform any behavior you'd like here in response to an error,
		 * e.g. logging it.
		 * <p>
		 * If the mapper should continue processing, return {@code true} and
		 * {@code null} will be mapped to the target type. If you would like the
		 * mapper to stop processing and throw
		 * {@link com.blinxbox.restinstagram.exception.InstagramJsonMappingException}
		 * , return {@code false}.
		 * 
		 * @param unmappableJson
		 *            The JSON that couldn't be mapped to a Java type.
		 * @param targetType
		 *            The Java type we were attempting to map to.
		 * @param e
		 *            The exception that occurred while performing the mapping
		 *            operation, or {@code null} if there was no exception.
		 * @return {@code true} to continue processing, {@code false} to throw a
		 *         {@link com.blinxbox.restinstagram.exception.InstagramJsonMappingException}
		 *         .
		 */
		boolean handleMappingError(String unmappableJson, Class<?> targetType,
				Exception e);
	}
}