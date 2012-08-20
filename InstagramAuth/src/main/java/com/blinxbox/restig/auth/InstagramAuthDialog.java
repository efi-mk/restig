/*
 * Copyright 2012 BlixBox, Inc.
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

import static java.lang.String.format;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Dialog box used for Instagram authentication flow. For more information
 * please read <a
 * href="http://instagr.am/developer/authentication/">http://instagr
 * .am/developer/authentication/</>
 * 
 * @author Efi MK
 * 
 */
public class InstagramAuthDialog extends Dialog {

	/**
	 * Redirect the ig auth flow to this URL.
	 */
	public static final String REDIRECT_URI = "http://www.blinxbox.com/instagram";
	/**
	 * Authentication end point for Instagram
	 */
	public static final String AUTH_URI = "https://instagram.com/oauth/authorize/?client_id=%s&redirect_uri=%s&response_type=token%s";

	/**
	 * Uri used for accessing authorization data.
	 */
	public static final String INSTAGRAM_URI = "https://instagram.com";
	/**
	 * Uri used for accessing authorization data.
	 */
	public static final String INSTAGRAM_RESET_PASSWORD = "https://instagram.com";

	/**
	 * Fill the entire screen with the webview layout.
	 */
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);

	/**
	 * Listen to authentication events.
	 */
	private final DialogListener mListener;
	/**
	 * When loading the auth web page show this progress bar.
	 */
	private ProgressDialog mSpinner;
	/**
	 * Close this dialog.
	 */
	private ImageView mCrossImage;
	/**
	 * Holds the authentication web page.
	 */
	private WebView mWebView;
	/**
	 * Holds all layout.
	 */
	private FrameLayout mContent;
	/**
	 * Client ID of the application.
	 */
	private final String mClientId;
	/**
	 * Contains the permissions to use.
	 */
	private final String mPermissions;

	/**
	 * Create a new dialog box.
	 * 
	 * @param context
	 *            - App context. Cannot be null.
	 * @param listener
	 *            - Listen to various authentication events.
	 * @param clientId
	 *            - Instagram client ID. You can retrieve your client ID from <a
	 *            href
	 *            ="http://instagr.am/developer/clients/manage/">http://instagr
	 *            .am/developer/clients/manage/</>. Cannot be null or empty.
	 * @param permissions
	 */
	public InstagramAuthDialog(final Context context,
			final DialogListener listener, final String clientId,
			final String... permissions) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		if (listener == null) {
			throw new IllegalArgumentException("listener is null");
		}
		if (clientId == null) {
			throw new IllegalArgumentException("clientId is null");
		}

		mListener = listener;
		this.mClientId = clientId;
		mPermissions = createPermissions(permissions);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSpinner = new ProgressDialog(getContext());
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");

		mContent = new FrameLayout(getContext());

		/*
		 * Create the 'x' image, but don't add to the mContent layout yet at
		 * this point, we only need to know its drawable width and height to
		 * place the webview
		 */
		createCrossImage();

		/*
		 * Now we know 'x' drawable width and height, layout the webivew and add
		 * it the mContent layout
		 */
		final int crossWidth = mCrossImage.getDrawable().getIntrinsicWidth();
		setUpWebView(crossWidth / 2);

		/*
		 * Finally add the 'x' image to the mContent layout and add mContent to
		 * the Dialog view
		 */
		mContent.addView(mCrossImage, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addContentView(mContent, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	/**
	 * Create a permissions string ,e.g. scope=likes+comments
	 * 
	 * @param permissions
	 *            - A variable array of permissions, where each cell contains a
	 *            single permission.
	 * @return A string representing the permissions.
	 */
	private String createPermissions(final String... permissions) {
		String returnValue = "";
		if (permissions.length > 0) {
			final StringBuilder builder = new StringBuilder("&scope=");
			String spaceString = "";
			for (final String string : permissions) {
				// Separate by +, see
				// http://instagr.am/developer/authentication/#scope
				builder.append(spaceString + string);
				spaceString = "+";
			}

			returnValue = builder.toString();
		}
		return returnValue;
	}

	/**
	 * Create and handle the X button.
	 */
	private void createCrossImage() {
		mCrossImage = new ImageView(getContext());
		// Dismiss the dialog when user click on the 'x'
		mCrossImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				mListener.onCancel();
				InstagramAuthDialog.this.dismiss();
			}
		});
		final Drawable crossDrawable = getContext().getResources().getDrawable(
				R.drawable.close);
		mCrossImage.setImageDrawable(crossDrawable);
		/*
		 * 'x' should not be visible while webview is loading make it visible
		 * only after webview has fully loaded
		 */
		mCrossImage.setVisibility(View.INVISIBLE);
		mCrossImage.setPadding(0, 10, 0, 0);
	}

	/**
	 * Setup the web view that displays login screen.
	 * 
	 * @param margin
	 *            - Margin around the web view.
	 */
	private void setUpWebView(final int margin) {
		final LinearLayout webViewContainer = new LinearLayout(getContext());
		mWebView = new WebView(getContext());
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new InstagramAuthDialog.IgWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSavePassword(false);
		final String url = format(AUTH_URI, mClientId, REDIRECT_URI,
				mPermissions);
		mWebView.loadUrl(url);
		mWebView.setLayoutParams(FILL);
		mWebView.setVisibility(View.INVISIBLE);

		webViewContainer.addView(mWebView);
		mContent.addView(webViewContainer);
	}

	private class IgWebViewClient extends WebViewClient {

		private static final String INSTAGRAM_WEB_VIEW = "Instagram-WebView";
		/**
		 * Indicates the authorization is done. No need to show any Loading...
		 * sign. It is required under ICS which runs onPageStarted when
		 * receiving REDIRECT_URI
		 */
		private boolean mFinished;

		@Override
		public boolean shouldOverrideUrlLoading(final WebView view,
				final String url) {
			Util.logd(InstagramAuthDialog.this.getContext(),
					INSTAGRAM_WEB_VIEW, "Redirect URL: " + url);
			boolean returnValue = true;
			if (url.startsWith(REDIRECT_URI)) {
				// We got out token !
				final Bundle values = Util.parseUrl(url);
				returnValue = false;

				// Do we have an error ?
				if (values.containsKey("error")) {
					mListener.onError(new DialogError(
							values.getString("error"), 0, url));
				} else {
					mListener.onComplete(values);
				}
				Log.i(INSTAGRAM_WEB_VIEW, "Redirected");
				mSpinner.dismiss();
				InstagramAuthDialog.this.dismiss();
				mFinished = true;

			} else if (url.startsWith("https://instagram.com/oauth/authorize")
					|| url.startsWith("https://instagram.com/accounts/login")) {
				// Continue with the authorization flow.
				returnValue = false;

			} else {
				// Any other url start a new viewer.
				// launch non-dialog URLs in a full browser
				getContext().startActivity(
						new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				returnValue = true;
			}

			// launch non-dialog URLs in a full browser, i.e.
			// "forgot my password" in case it's not a URL we are taking care
			// of.
			return returnValue;
		}

		@Override
		public void onReceivedError(final WebView view, final int errorCode,
				final String description, final String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(new DialogError(description, errorCode,
					failingUrl));
			InstagramAuthDialog.this.dismiss();
		}

		@Override
		public void onPageStarted(final WebView view, final String url,
				final Bitmap favicon) {
			Util.logd(InstagramAuthDialog.this.getContext(),
					INSTAGRAM_WEB_VIEW, "Webview loading URL: " + url);
			super.onPageStarted(view, url, favicon);
			if (!mFinished) {
				mSpinner.show();
			}

		}

		@Override
		public void onPageFinished(final WebView view, final String url) {
			super.onPageFinished(view, url);
			try {
				// In some cases we dismiss the originating parent activity if
				// so
				// then don't dismiss this dialog otherwise you'll get an error.
				Util.logd(InstagramAuthDialog.this.getContext(),
						INSTAGRAM_WEB_VIEW, "Webview finished URL: " + url);
				if (isShowing()) {
					mSpinner.dismiss();
				}
				/*
				 * Once webview is fully loaded, set the mContent background to
				 * be transparent and make visible the 'x' image.
				 */
				mWebView.setVisibility(View.VISIBLE);
				mCrossImage.setVisibility(View.VISIBLE);
			} catch (final Exception error) {
				// Ignore any error, just print it.
				Log.e(INSTAGRAM_WEB_VIEW, error.getMessage());
			}
		}
	}

	/**
	 * Callback interface for dialog requests.
	 * 
	 */
	public static interface DialogListener {

		/**
		 * Called when a dialog completes.
		 * 
		 * Executed by the thread that initiated the dialog.
		 * 
		 * @param values
		 *            Key-value string pairs extracted from the response.
		 */
		public void onComplete(Bundle values);

		/**
		 * Called when a dialog has an error.
		 * 
		 * Executed by the thread that initiated the dialog.
		 * 
		 */
		public void onError(DialogError error);

		/**
		 * Called when a dialog is canceled by the user.
		 * 
		 * Executed by the thread that initiated the dialog.
		 * 
		 */
		public void onCancel();

	}
}
