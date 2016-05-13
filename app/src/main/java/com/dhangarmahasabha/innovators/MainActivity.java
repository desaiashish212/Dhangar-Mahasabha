package com.dhangarmahasabha.innovators;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.dhangarmahasabha.innovators.db.DBHandler;
import com.dhangarmahasabha.innovators.fragment.NewsFragment;
import com.dhangarmahasabha.innovators.model.Category;
import com.dhangarmahasabha.innovators.navdrawer.NavDrawerItem;
import com.dhangarmahasabha.innovators.navdrawer.NavDrawerListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.innovators.localizationactivity.LocalizationActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class MainActivity extends LocalizationActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;
	private ActionBar actionBar;
	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private FragmentTransaction fragmentTransaction;
	private android.support.v4.app.FragmentManager fragmentManager;
	private DBHandler handler;
	private ArrayList<Category> categoryArrayList;
	private int status=1;
	private String language;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setDefaultLanguage(getLanguage());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
		.build();
		ImageLoader.getInstance().init(config);
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		handler = new DBHandler(this);
		System.out.println("Language....."+LocalizationActivity.getLanguage());
		System.out.println("Categoryyyyyy:"+handler.getAllCategory(1));
		navDrawerItems = new ArrayList<NavDrawerItem>();
		language = LocalizationActivity.getLanguage();
		if (language.equals("ma")){
			status =1;
		}else if (language.equals("hi")){
			status=2;
		}else if (language.equals("en")){
			status =3;
		}
		categoryArrayList = handler.getAllCategory(status);
		System.out.println("Lengthhhhhh:"+categoryArrayList.size());

		for (int i=0; i<categoryArrayList.size();i++){

			navDrawerItems.add(new NavDrawerItem(categoryArrayList.get(i).getCat_name(), navMenuIcons.getResourceId(0, -1), newsPresent(String.valueOf(categoryArrayList.get(i).getCat_id())), newsCount(String.valueOf(categoryArrayList.get(i).getCat_id()))));
		}
//		// Home
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1), newsPresent("1"), newsCount("1")));
//		// Find People
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(0, -1), newsPresent("2"), newsCount("2")));
//		// Photos
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(0, -1),newsPresent("3"),newsCount("3")));
//		// Communities, Will add a counter here
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(0, -1), newsPresent("4"), newsCount("4")));
//		// Pages
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(0, -1), newsPresent("5"), newsCount("5")));
//		// What's hot, We  will add a counter here
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(0, -1), newsPresent("6"), newsCount("6")));
//

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);
		actionBar = getSupportActionBar();
		TextView TextViewNewFont = new TextView(MainActivity.this);

		LayoutParams layoutparams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		TextViewNewFont.setLayoutParams(layoutparams);

		TextViewNewFont.setText(getResources().getString(R.string.app_name));

		TextViewNewFont.setTextColor(Color.RED);

		TextViewNewFont.setGravity(Gravity.CENTER);

		TextViewNewFont.setTextSize(27);

		// Assests folder font folder path
//		ExternalFontPath = "ExternalFonts/chopinscript.ttf";
//
//		// Load Typeface font url String ExternalFontPath
//		FontLoaderTypeface = Typeface.createFromAsset(getAssets(), ExternalFontPath);
//
//		TextViewNewFont.setTypeface(FontLoaderTypeface);

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		actionBar.setCustomView(TextViewNewFont);
		// enabling action bar app icon and behaving it as toggle button
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.string.drawer_open, // nav drawer open - description for accessibility
				R.string.drawer_close // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				actionBar.setTitle(getResources().getString(R.string.app_name));
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				actionBar.setTitle(getResources().getString(R.string.app_name));
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		// Check if Google Play Service is installed in Device
		// Play services is needed to handle GCM stuffs
		if (!checkPlayServices()) {
			Toast.makeText(
					getApplicationContext(),
					"This device doesn't support Play services, App will not work normally",
					Toast.LENGTH_LONG).show();
		}
	}
	public boolean newsPresent(String status){
		boolean result = false;
		if (handler.getNewsCount(status)>0){
			result =true;
		}
		return result;
	}
	public String newsCount(String status){
		String result="0";
		if (handler.getNewsCount(status)>0){
			result =String.valueOf(handler.getNewsCount(status));
		}
		return result;
	}
	@Override
	public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onPostCreate(savedInstanceState, persistentState);
		mDrawerToggle.syncState();
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			System.out.println("AAAAAA"+categoryArrayList.get(position).getCat_id());
			System.out.println("bbbbb"+categoryArrayList.get(position).getCat_name());
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.menu_tool:
				//startActivity(new Intent(MainActivity.this,SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.menu_tool).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		System.out.println("AAAAAA"+categoryArrayList.get(position).getCat_id());
		System.out.println("bbbbb"+categoryArrayList.get(position).getCat_name());
		// update the main content by replacing fragments
		Bundle bundle = new Bundle();
		bundle.putString("id", String.valueOf(categoryArrayList.get(position).getCat_id()));
		bundle.putString("name",categoryArrayList.get(position).getCat_name());
		Fragment fragment = new NewsFragment();

		fragment.setArguments(bundle);


//		switch (position) {
//		case 0:
//			fragment = new TajyaBatmyaFragment();
//			break;
//		case 1:
//			fragment = new MahatvachyaFragment();
//			break;
//		case 2:
//			fragment = new RashtriyFragment();
//			break;
//		case 3:
//			fragment = new VivahFragment();
//			break;
//		case 4:
//			fragment = new KaolFragment();
//			break;
//		case 5:
//			fragment = new MaharashtraFragment();
//			break;
//
//		default:
//			break;
//		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("RegisterActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		actionBar.setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	// Check if Google Playservices is installed in Device or not
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// When Play services not found in device
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				// Show Error dialog to install Play services
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(
						getApplicationContext(),
						"This device doesn't support Play services, App will not work normally",
						Toast.LENGTH_LONG).show();
				finish();
			}
			return false;
		} else {
			Toast.makeText(
					getApplicationContext(),
					"This device supports Play services, App will work normally",
					Toast.LENGTH_LONG).show();
		}
		return true;
	}
	// When Application is resumed, check for Play services support to make sure
	// app will be running normally
	@Override
	public void onResume() {
		super.onResume();
		checkPlayServices();
	}

}
