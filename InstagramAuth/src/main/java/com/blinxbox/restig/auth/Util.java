/*
 * Copyright 2012 BlinxBox, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blinxbox.restig.auth;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * Utility class supporting the Instagram dialog box.
 * 
 * @author Efi MK
 * 
 */
public final class Util {

	/**
	 * Set this to true to enable log output. Remember to turn this back off
	 * before releasing. Sending sensitive data to log is a security risk.
	 */
	private static Boolean mEnableLog = null;

	/**
	 * Check if android:debuggable flag is turn on or not.
	 * 
	 * @param ctx
	 *            - Application's context. Cannot be null.
	 * @param tag
	 *            - Tag used for logging purposes.
	 * @return True if flag is set to true, false otherwise.
	 */
	public synchronized static boolean isDebugMode(final Context ctx,
			final String tag) {
		if (mEnableLog == null) {
			boolean debug = false;
			PackageInfo packageInfo = null;
			try {
				packageInfo = ctx.getPackageManager().getPackageInfo(
						ctx.getApplicationContext().getPackageName(),
						PackageManager.GET_CONFIGURATIONS);
			} catch (final NameNotFoundException e) {
				Log.w(tag, e.getMessage());
			}
			if (packageInfo != null) {
				final int flags = packageInfo.applicationInfo.flags;
				if ((flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
					debug = true;
				} else {
					debug = false;
				}
			}
			mEnableLog = debug;
		}
		return mEnableLog;

	}

	public static String encodeUrl(final Bundle parameters) {
		if (parameters == null) {
			return "";
		}

		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final String key : parameters.keySet()) {
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(URLEncoder.encode(key) + "="
					+ URLEncoder.encode(parameters.getString(key)));
		}
		return sb.toString();
	}

	public static Bundle decodeUrl(final String s) {
		final Bundle params = new Bundle();
		if (s != null) {
			final String array[] = s.split("&");
			for (final String parameter : array) {
				final String v[] = parameter.split("=");
				params.putString(URLDecoder.decode(v[0]),
						URLDecoder.decode(v[1]));
			}
		}
		return params;
	}

	/**
	 * Parse a URL query and fragment parameters into a key-value bundle.
	 * 
	 * @param url
	 *            the URL to parse
	 * @return a dictionary bundle of keys and values
	 */
	public static Bundle parseUrl(String url) {
		// hack to prevent MalformedURLException
		url = url.replace("fbconnect", "http");
		try {
			final URL u = new URL(url);
			final Bundle b = decodeUrl(u.getQuery());
			b.putAll(decodeUrl(u.getRef()));
			return b;
		} catch (final MalformedURLException e) {
			return new Bundle();
		}
	}

	public static void clearCookies(final Context context) {
		// Edge case: an illegal state exception is thrown if an instance of
		// CookieSyncManager has not be created. CookieSyncManager is normally
		// created by a WebKit view, but this might happen if you start the
		// app, restore saved state, and click logout before running a UI
		// dialog in a WebView -- in which case the app crashes
		@SuppressWarnings("unused")
		final CookieSyncManager cookieSyncMngr = CookieSyncManager
				.createInstance(context);
		final CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	/**
	 * A proxy for Log.d api that kills log messages in release build. It not
	 * recommended to send sensitive information to log output in shipping apps.
	 * 
	 * @param ctx
	 *            - Application's context. Cannot be null.
	 * @param tag
	 *            - Tag used for logging purposes.
	 * @param msg
	 *            - Message to write to the log.
	 */
	public static void logd(final Context ctx, final String tag,
			final String msg) {
		if (isDebugMode(ctx, tag)) {
			Log.d(tag, msg);
		}
	}
}
