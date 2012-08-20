/**
 * 
 */
package com.blinxbox.restinstagram;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blinxbox.restinstagram.types.MediaPost;

/**
 * @author Efi MK
 * 
 */
public class InstagramCollectionTest {

	/**
	 * Recent tags json response.
	 */
	private static final String RECENT_TAG_IMAGES = "{   \"pagination\":  {     \"next_max_tag_id\": \"1336503395126\",     \"deprecation_warning\": \"next_max_id and min_id are deprecated for this endpoint; use min_tag_id and max_tag_id instead\",     \"next_max_id\": \"1336503395126\",     \"next_min_id\": \"1336504621170\",     \"min_tag_id\": \"1336504621170\",     \"next_url\": \"http://nexturl.com\"   },   \"meta\":  {   \"code\": 200   },   \"data\":  [      {       \"attribution\": null,       \"tags\":  [         \"bydalen\",         \"snowboard\",         \"sunny\",         \"snow\",         \"friend\"       ],       \"location\":  {         \"latitude\": 59.442253112,         \"longitude\": 18.066232681       },       \"comments\":  {         \"count\": 0,         \"data\":  []       },       \"filter\": \"Brannan\",       \"created_time\": \"1336479417\",       \"link\": \"http://instagr.am/p/KXYbcOKxPO/\",       \"likes\":  {         \"count\": 0,         \"data\":  []       },       \"images\":  {         \"low_resolution\":  {           \"url\": \"http://distilleryimage10.instagram.com/b4fe6996990711e1ab011231381052c0_6.jpg\",           \"width\": 306,           \"height\": 306         },         \"thumbnail\":  {           \"url\": \"http://distilleryimage10.instagram.com/b4fe6996990711e1ab011231381052c0_5.jpg\",           \"width\": 150,           \"height\": 150         },         \"standard_resolution\":  {           \"url\": \"http://distilleryimage10.instagram.com/b4fe6996990711e1ab011231381052c0_7.jpg\",           \"width\": 612,           \"height\": 612         }       },       \"caption\":  {         \"created_time\": \"1336479421\",         \"text\": \"#Friend #Snowboard #Sunny #Snow #Bydalen\",         \"from\":  {           \"username\": \"douglas_truthful\",           \"profile_picture\": \"http://images.instagram.com/profiles/profile_53227755_75sq_1336460101.jpg\",           \"id\": \"53227755\",           \"full_name\": \"Douglas Ekermark\"         },         \"id\": \"186725375129883031\"       },       \"type\": \"image\",       \"id\": \"186725348403778510_53227755\",       \"user\":  {         \"username\": \"douglas_truthful\",         \"website\": \"\",         \"bio\": \"- Truthful - Lyric/Rap - 17 y/o Male - Swedish -\",         \"profile_picture\": \"http://images.instagram.com/profiles/profile_53227755_75sq_1336460101.jpg\",         \"full_name\": \"Douglas Ekermark\",         \"id\": \"53227755\"       }     },      {       \"attribution\": null,       \"tags\":  [         \"wegrow\",         \"webstagram\",         \"whatisee\",         \"winter\",         \"igersfc\",         \"nature\",         \"panorama\",         \"poland\",         \"instatalent\",         \"snow\",         \"natgeo\",         \"bestoftheday\",         \"instago\",         \"phototag_it\",         \"beitalian\",         \"picoftheday\",         \"photooftheday\"       ],       \"location\": null,       \"comments\":  {         \"count\": 1,         \"data\":  [            {             \"created_time\": \"1336479400\",             \"text\": \"#winter #snow #instatalent #instago #whatisee #webstagram #wegrow #panorama #nature #natgeo\",             \"from\":  {               \"username\": \"dottflavio\",               \"profile_picture\": \"http://images.instagram.com/profiles/profile_30787376_75sq_1333475965.jpg\",               \"id\": \"30787376\",               \"full_name\": \"flavio\"             },             \"id\": \"186725200680002692\"           }         ]       },       \"filter\": \"Normal\",       \"created_time\": \"1336478017\",       \"link\": \"http://instagr.am/p/KXVwetpSVd/\",       \"likes\":  {         \"count\": 6,         \"data\":  [            {             \"username\": \"danieldavidshalibo\",             \"profile_picture\": \"http://images.instagram.com/profiles/profile_3300779_75sq_1336176351.jpg\",             \"id\": \"3300779\",             \"full_name\": \"Daniel David Shalibo\"           },            {             \"username\": \"peppefax\",             \"profile_picture\": \"http://images.instagram.com/profiles/profile_30709312_75sq_1333798947.jpg\",             \"id\": \"30709312\",             \"full_name\": \"Giuseppe Fazio\"           },            {             \"username\": \"misterizio\",             \"profile_picture\": \"http://images.instagram.com/profiles/profile_51499495_75sq_1336123156.jpg\",             \"id\": \"51499495\",             \"full_name\": \"misterizio\"           },            {             \"username\": \"epicpopsicle\",             \"profile_picture\": \"http://images.instagram.com/profiles/profile_35553905_75sq_1335486728.jpg\",             \"id\": \"35553905\",             \"full_name\": \"Nasir Tajik\"           },            {             \"username\": \"nicolealphonce\",             \"profile_picture\": \"http://images.instagram.com/profiles/profile_16878208_75sq_1334428844.jpg\",             \"id\": \"16878208\",             \"full_name\": \"Nicole Alphonce\"           },            {             \"username\": \"charplin\",             \"profile_picture\": \"http://images.instagram.com/profiles/profile_14972135_75sq_1335644930.jpg\",             \"id\": \"14972135\",             \"full_name\": \"Carlos\"           }         ]       },       \"images\":  {         \"low_resolution\":  {           \"url\": \"http://distilleryimage1.instagram.com/724a1706990411e18bb812313804a181_6.jpg\",           \"width\": 306,           \"height\": 306         },         \"thumbnail\":  {           \"url\": \"http://distilleryimage1.instagram.com/724a1706990411e18bb812313804a181_5.jpg\",           \"width\": 150,           \"height\": 150         },         \"standard_resolution\":  {           \"url\": \"http://distilleryimage1.instagram.com/724a1706990411e18bb812313804a181_7.jpg\",           \"width\": 612,           \"height\": 612         }       },       \"caption\":  {         \"created_time\": \"1336478066\",         \"text\": \"Inverno #poland #igersfc #beitalian #bestoftheday #picoftheday #phototag_it #photooftheday\",         \"from\":  {           \"username\": \"dottflavio\",           \"profile_picture\": \"http://images.instagram.com/profiles/profile_30787376_75sq_1333475965.jpg\",           \"id\": \"30787376\",           \"full_name\": \"flavio\"         },         \"id\": \"186714009303852128\"       },       \"type\": \"image\",       \"id\": \"186713600048833885_30787376\",       \"user\":  {         \"username\": \"dottflavio\",         \"website\": \"http://Twitter.com/dottflavio\",         \"bio\": \"Appassionato di motori e fotografia. Romagnolo di Forlì Italy. Photo from Samsung Galaxy S2 and Fuji finepix S9500 \",         \"profile_picture\": \"http://images.instagram.com/profiles/profile_30787376_75sq_1333475965.jpg\",         \"full_name\": \"flavio\",         \"id\": \"30787376\"       }     }   ] }";

	/**
	 * @throws java.lang.Exception
	 *             - Ignore.
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.blinxbox.restinstagram.InstagramCollection#InstagramCollection(com.blinxbox.restinstagram.InstagramClient, java.lang.String, java.lang.Class)}
	 * .
	 */
	@Test
	public void instagramCollection_ValidPost_ListOfPostsAvailable() {
		final InstagramClient client = mock(InstagramClient.class);
		when(client.getJsonMapper()).thenReturn(new DefaultJsonMapper());

		final InstagramCollection<MediaPost> collection = new InstagramCollection<MediaPost>(
				client, RECENT_TAG_IMAGES, MediaPost.class);
		final List<MediaPost> posts = collection.getData();
		assertEquals(2, posts.size());

	}

	/**
	 * Test method for
	 * {@link com.blinxbox.restinstagram.InstagramCollection#getNextPageUrl()}.
	 */
	@Test
	public void testGetNextPageUrl() {
		final InstagramClient client = mock(InstagramClient.class);
		when(client.getJsonMapper()).thenReturn(new DefaultJsonMapper());

		final InstagramCollection<MediaPost> collection = new InstagramCollection<MediaPost>(
				client, RECENT_TAG_IMAGES, MediaPost.class);
		assertEquals("http://nexturl.com", collection.getNextPageUrl());
	}

}
