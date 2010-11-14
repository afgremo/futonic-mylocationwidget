package com.futonredemption.mylocation;

import java.util.ArrayList;
import java.util.Locale;

import org.beryl.app.ChoosableIntent;
import org.beryl.app.IntentChooser;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.net.Uri;

public class MyLocation implements ILocationWidgetInfo {

	private final Location _location;
	private final Context _context;
	private Address _address = null;

	public MyLocation(final Context context, final Location location) {
		_location = location;
		_context = context;
	}

	public double getLatitude() {
		return _location.getLatitude();
	}
	
	public double getLongitude() {
		return _location.getLongitude();
	}
	
	public static class util {
		public static class Log {
			public static void d(String tag, String msg) {
				if(msg == null) msg = "[null]";
				android.util.Log.d(tag, msg);
			}
		}
	}
	public CharSequence getTitle() {
		CharSequence title = null;
		
		if(_address != null) {
			if(_address.getMaxAddressLineIndex() > 0) {
				title = _address.getAddressLine(0);
			}
		}
		else {
			title = _context.getText(R.string.coordinates);
		}
		
		return title;
	}
	
	public CharSequence getDescription() {
		CharSequence description = null;
		
		if(_address != null) {
			if(_address.getMaxAddressLineIndex() > 1) {
				description = _address.getAddressLine(1);
			}
			else {
				description = _address.getFeatureName();
			}
		}
		else {
			description = getOneLineCoordinates();
		}
		
		return description;
	}
	
	public void attachAddress(Address address) {
		_address = address;
	}
	
	public Intent getActionIntent() {
		return Intents.actionRefresh(_context);
	}

	private Intent getShareCoordinatesIntent() {
		final Intent coordinateShare = Intents.createSend(_context.getText(R.string.my_location), getOneLineCoordinates());
		return Intent.createChooser(coordinateShare, _context.getText(R.string.coordinates));
	}

	private Intent getShareAddressIntent() {
		final Intent addressShare = Intents.createSend(_context.getText(R.string.my_location), getOneLineAddress());
		return Intent.createChooser(addressShare, _context.getText(R.string.address));
	}
	
	private Intent getShareMapsLinkIntent() {
		final Intent addressShare = Intents.createSend(_context.getText(R.string.my_location), getGoogleMapsUrl(getOneLineAddress()));
		return Intent.createChooser(addressShare, _context.getText(R.string.maps_link));
	}
	
	private Intent getShareAddressWithMapLinkIntent() {
		final String yourFriendIsHere = _context.getString(R.string.your_friend_is_here);
		final StringBuilder message = new StringBuilder();
		message.append(getOneLineAddress());
		message.append(Constants.NEWLINE);
		message.append(getGoogleMapsUrl(yourFriendIsHere));
		
		final Intent addressShare = Intents.createSend(_context.getText(R.string.my_location), message);
		return Intent.createChooser(addressShare, _context.getText(R.string.address_with_link));
	}
	
	public Intent getShareIntent() {
		final ArrayList<ChoosableIntent> intents = new ArrayList<ChoosableIntent>();
		if(_address != null) {
			intents.add(createChoosable(R.string.address, getShareAddressIntent()));
		}
		intents.add(createChoosable(R.string.coordinates, getShareCoordinatesIntent()));
		intents.add(createChoosable(R.string.maps_link, getShareMapsLinkIntent()));
		if(_address != null) {
			intents.add(createChoosable(R.string.address_with_link, getShareAddressWithMapLinkIntent()));
		}
		
		final CharSequence title = _context.getText(R.string.share_location);
		final int iconResId = R.drawable.stat_icon;
		
		return IntentChooser.createChooserIntent(_context, title, iconResId, intents);
	}

	private ChoosableIntent createChoosable(final int stringId, final Intent intent) {
		final CharSequence title = _context.getText(stringId);
		return new ChoosableIntent(title, intent);
	}
	
	private String getOneLineAddress() {

		final StringBuilder sb = new StringBuilder();
		sb.append(getTitle());
		sb.append(" ");
		sb.append(getDescription());
		return sb.toString();
	}
	
	private CharSequence getOneLineCoordinates() {
		return String.format(Locale.ENGLISH, "Lat: %s Long: %s", getLatitude(), getLongitude());
	}
	
	public Intent getViewIntent() {
		final ArrayList<ChoosableIntent> intents = new ArrayList<ChoosableIntent>();
		intents.add(createChoosable(R.string.nolocal_google_maps, Intents.viewWebsite(getGoogleMapsUrl(getOneLineAddress()))));
		intents.add(createChoosable(R.string.nolocal_flickr_photos, Intents.viewWebsite(getFlickrPhotosUrl())));
		intents.add(createChoosable(R.string.nolocal_panoramio_photos, Intents.viewWebsite(getPanoramioPhotosUrl())));
		
		final CharSequence title = _context.getText(R.string.view_location);
		final int iconResId = R.drawable.stat_icon;
		return IntentChooser.createChooserIntent(_context, title, iconResId, intents);
	}

	public int getWidgetState() {
		return Constants.WIDGETLAYOUTSTATE_Default;
	}

	private String getFlickrPhotosUrl() {
		return String.format(Locale.ENGLISH, Constants.URL_FlickrPhotos, getLatitude(), getLongitude());
	}
	private String getGoogleMapsUrl(String message) {
		return String.format(Locale.ENGLISH, Constants.URL_GmapsBase, getLatitude(), getLongitude(), Uri.encode(message));
	}

	private String getPanoramioPhotosUrl() {
		return String.format(Locale.ENGLISH, "http://www.panoramio.com/map/#lt=%f&ln=%f&z=0&k=2", getLatitude(), getLongitude());
	}
	
	public Intent getNotificationIntent() {
		return getViewIntent();
	}
}
