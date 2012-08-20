## Synopsis
Instagram(IG) API wrapper for Android for handling an Instagram app lifecycle. Split into 2 sub-projects:

 * **InstagramAuth** Authentication library, based uppon [Facebook for Android](https://github.com/facebook/facebook-android-sdk) library.
 * **restig** A wrapper arround [IG rest API](http://instagram.com/developer/) calls. Based upon [restFB](http://restfb.com/).


## Documentation
Currently short on documentation, will add more as time permits.
### Examples
#### InstagramAuth
	/**
	 * Authorize a user in Instagram. Will show up the auth dialog and will
	 * allow the user to enter his/her credentials.
	 * <p>
	 * <b> Pay attention</b> Use the listener afterwards to save the
	 * authentication token.
	 * 
	 * @param listener
	 *            - A dialog listener that can react to various authentication
	 *            events. Cannot be null.
	 * @param permissions
	 *            - Use various permissions. Can be empty but not null.
	 * @param activity
	 *            - An activity that will be the parent of the dialog box.
	 */
	public void authorize(final DialogListener listener,
			final Activity activity, final String... permissions) {
		InstagramModule module = new InstagramModule();
		final InstagramAuthDialog dialog = new InstagramAuthDialog(activity,
				listener, mAppId, permissions);
		dialog.setCancelable(false);
		dialog.show();
	}
	
	public class InstagramModule implements
		DialogListener {
		@Override
		public void onComplete(final Bundle values) {
			String accessToken = values.getString("access_token");
		}

		@Override
		public void onError(final DialogError error) {
			// Log error
		}

		@Override
		public void onCancel() {
			// User canceled.
		}
	}
	...
	authorize(new InstagramModule(), activity, "likes", "comments");

#### restig
	InstagramClient userClient = new DefaultInstagramClient(appId, mAccessToken);
	final String endPoint = format("media/%s/comments",
						"1234");
	final InstagramCollection<Comment> comments = client
						.fetchCollection(endPoint, Comment.class);
## Installation
Based on maven.
### InstagramAuth
	<dependency>
		<groupId>com.blinxbox.restinstagram</groupId>
		<artifactId>auth</artifactId>
		<version>1.2-SNAPSHOT</version>
		<scope>provided</scope>
	</dependency>

	<dependency>
		<groupId>com.blinxbox.restinstagram</groupId>
		<artifactId>auth</artifactId>
		<version>1.2-SNAPSHOT</version>
		<type>apklib</type>
	</dependency>
### restig
	<dependency>
		<groupId>com.blinxbox.restinstagram</groupId>
		<artifactId>restig</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
	
## Contributing
Feel like giving back? We'll happily take contributions via GitHub. For questions, please turn to [Instagram API Developers](https://groups.google.com/forum/?fromgroups#!forum/instagram-api-developers) on Google Groups.

## License
Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)

## Authors
 * Efi Merdler-Kravitz (efi.merdler@gmail.com)
 * Nir Segev (nirsegev@gmail.com)
 * the Android open-source community (http://stackoverflow.com/questions/tagged/android)