package com.hextilla.cardbox.facebook.client;

import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.data.CardBoxUserObject;
import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.facebook.UserWithPicture;
import com.hextilla.cardbox.util.CardBoxContext;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Connection;
import com.restfb.Parameter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.conn.scheme.AsyncScheme;
import org.apache.http.nio.conn.ssl.SSLLayeringStrategy;
import org.apache.http.params.CoreConnectionPNames;

import com.samskivert.util.StringUtil;

import com.threerings.presents.client.BasicDirector;
import com.threerings.presents.client.Client;

import static com.hextilla.cardbox.Log.log;

public class SocialDirector extends BasicDirector 
{
	public interface FriendIterator
	{
		/** Need a means for iterating over our list of online friends */
		public CardBoxName next();
		public boolean hasNext();
	}
	
	public interface FriendTracker
	{
		/** Need a means for components to determining if user is one of our online friends */
		public boolean isOnlineFriend(CardBoxName friend);
		
		/** Need to provide a means for iterating over our list of online friends */
		public FriendIterator getOnlineFriendIterator();
		
		/** Need to provide a means to update the view when a user's display pic changes. */
		public void imageUpdated(CardBoxName friend);
	}

	public SocialDirector (CardBoxContext ctx) {
		super(ctx);
		_ctx = ctx;
	}
	
	public void init (Client client)
		throws Exception
	{
		CardBoxUserObject user = (CardBoxUserObject)client.getClientObject();
		_token = user.getSession();
		_fbclient = StringUtil.isBlank(_token) ? null : new DefaultFacebookClient(_token);
	} 
	
	@Override
	public void clientDidLogon (Client client)
	{
		super.clientDidLogon(client);
		if (_ctx.isFacebookEnabled()) {
			try {
				init(client);
			} catch (Exception e) {
				log.warning("An error occurred during SocialDirector initialization", e);
			}
		}
	}
	
	public synchronized FriendSet getFriends ()
	{
		if (_fbclient == null) return new FriendSet(_ctx);
		if (_friends == null)
		{
			FriendSet friends = new FriendSet(_ctx);
			Connection<UserWithPicture> friendList = _fbclient.fetchConnection("me/friends", 
					UserWithPicture.class, Parameter.with("fields", "id, name, picture"));
			
			for (List<UserWithPicture> friendPage : friendList) {
				for (UserWithPicture friend : friendPage) {
					friends.add(friend);
				}
			}
			_friends = friends;
		}
		return _friends;
	}
	
	/** Allows us to provide online-tracking throughout the client */
	public void setFriendTracker(FriendTracker ft)
	{
		_tracker = ft;
	}
	
	/** Delegate responsibility to the FriendTracker, if available */
	public boolean isOnlineFriend(CardBoxName friend)
	{
		return (_tracker == null) ? false : _tracker.isOnlineFriend(friend);
	}
	
	/** Delegate responsibility to the FriendTracker, if available */
	public FriendIterator getOnlineFriendIterator()
	{
		return (_tracker == null) ? null : _tracker.getOnlineFriendIterator();
	}
	
	public void downloadPic(CardBoxName friend, String url)
		throws Exception
	{
		// If our social services aren't online, don't even bother with this stuff
		if (_fbclient == null) return;
		
		log.info("Now downloading a friend's display picture", "friend", friend.getFriendlyName().toString(), "url", url);
		
		HttpAsyncClient _http = new DefaultHttpAsyncClient();
		_http.getParams()
        	.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 3000)
	        .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000)
	        .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
	        .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);

		//SSLLayeringStrategy _ssl = new SSLLayeringStrategy(new TrustSelfSignedStrategy(), new AllowAllHostnameVerifier());
		//AsyncScheme _https = new AsyncScheme("https", 443, _ssl);
		
		//_http.getConnectionManager().getSchemeRegistry().register(_https);
		
		final CardBoxName friendname = friend;
		final Long friendId = new Long(friendname.getFacebookId());
		final CountDownLatch latch = new CountDownLatch(1);
		_http.start();
		try {
			HttpGet get = new HttpGet(url);
			_http.execute(get, new FutureCallback<HttpResponse>() {
				
				@Override
				public void completed(HttpResponse response) {
					latch.countDown();
					String imgdata = getData(response);
					_friends.setPicFromRaw(friendId, imgdata);
					imageUpdated(friendname);
				}

				@Override
				public void cancelled() {
					latch.countDown();
					System.out.println("Asynchronous download of display picture was cancelled.");
				}

				@Override
				public void failed(Exception err) {
					latch.countDown();
					System.out.println("Asynchronous download of display picture failed.");
					err.printStackTrace();
				}
			});
			latch.await();
			log.info("Shutting down HttpAsyncClient after executing HTTP GET");
		} finally {
			_http.shutdown();
		}
	}
	
	/** Delegate responsibility to the FriendTracker, if available */
	public void imageUpdated(CardBoxName friend)
	{
		if (_tracker != null)
			_tracker.imageUpdated(friend);
	}
	
	public static String getData(HttpResponse response)
	{
		int ch = 0;
		String bytes = null;
		StringBuffer b = new StringBuffer();
		log.info("Retrieving image content from HttpResponse", "Response Status", response.getStatusLine());
		try {
			InputStream in = response.getEntity().getContent();
			long len = response.getEntity().getContentLength();
			log.info("Image content length, in bytes", "length", len);
			// If we know the length of the data, read exactly that much
			if (len > 0) {
				for (long l = 0; l < len; ++l)
					if ((ch = in.read()) != -1) {
						b.append((char)ch);
					}
			} else {
				while ((ch = in.read()) != -1) {
					len = in.available();
					b.append((char)ch);
				}
			}
			in.close();
			bytes = b.toString();
		} catch (IOException ioe) {
			log.warning("There was a problem reading the returned content", "Response Status", response.getStatusLine(), ioe);
		}
		log.info("Binary image data retrieved", "bytes", bytes);
		return bytes;
	}

	protected String _token;
	
	/** Manage the reference to the set of raw friend data from Facebook. */
	protected FriendSet _friends = null;
	
	/** The giver of life and services */
	protected CardBoxContext _ctx;
	
	protected FacebookClient _fbclient = null;
	
	// Delegate the responsibility of tracking online friends using the FriendTracker interface
	protected FriendTracker _tracker = null;
	protected FriendIterator _iterator = null;
}
