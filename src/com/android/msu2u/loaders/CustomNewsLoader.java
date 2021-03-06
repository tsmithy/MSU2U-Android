package com.android.msu2u.loaders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.msu2u.ContainerNews;
import com.android.msu2u.R;

/**
 * Demonstration of the implementation of a custom Loader.
 */
public class CustomNewsLoader extends SherlockFragmentActivity {
	
	// we use this to temporarily hold our json rows, for each row we return 
	// until we use the array to create our events object.
	static ArrayList<String> resultRow;
	static List<News> entries = null;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        FragmentManager fm = getSupportFragmentManager();
        
        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            AppListFragment list = new AppListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }


    /**
     * This class holds the per-item data in our Loader.
     */
    public static class News{
    	private int articleID;
    	private String lastChanged;
    	private String title;
    	private String link;
    	private String comments;
    	private String pubDate;
    	private String docCreator;
    	private String category1;
    	private String category2;
    	private String shortDescription;
    	private String longDescription; 
    	private int mediaID;
    	private int sourceID;
    	private String articleLink;
		private int getArticleID() {
			return articleID;
		}
		private void setArticleID(int articleID) {
			this.articleID = articleID;
		}
		private String getLastChanged() {
			return lastChanged;
		}
		private void setLastChanged(String lastChanged) {
			this.lastChanged = lastChanged;
		}
		private String getTitle() {
			return title;
		}
		private void setTitle(String title) {
			this.title = title;
		}
		private String getLink() {
			return link;
		}
		private void setLink(String link) {
			this.link = link;
		}
		private String getComments() {
			return comments;
		}
		private void setComments(String comments) {
			this.comments = comments;
		}
		private String getPubDate() {
			return pubDate;
		}
		private void setPubDate(String pubDate) {
			this.pubDate = pubDate;
		}
		private String getDocCreator() {
			return docCreator;
		}
		private void setDocCreator(String docCreator) {
			this.docCreator = docCreator;
		}
		private String getCategory1() {
			return category1;
		}
		private void setCategory1(String category1) {
			this.category1 = category1;
		}
		private String getCategory2() {
			return category2;
		}
		private void setCategory2(String category2) {
			this.category2 = category2;
		}
		private String getShortDescription() {
			return shortDescription;
		}
		private void setShortDescription(String shortDescription) {
			this.shortDescription = shortDescription;
		}
		private String getLongDescription() {
			return longDescription;
		}
		private void setLongDescription(String longDescription) {
			this.longDescription = longDescription;
		}
		private int getMediaID() {
			return mediaID;
		}
		private void setMediaID(int mediaID) {
			this.mediaID = mediaID;
		}
		private int getSourceID() {
			return sourceID;
		}
		private void setSourceID(int sourceID) {
			this.sourceID = sourceID;
		}
		private String getArticleLink() {
			return articleLink;
		}
		private void setArticleLink(String articleLink) {
			this.articleLink = articleLink;
		}
    	
    } // end News Class
    

  
  

    /**
     * A custom Loader that loads all of the installed applications.
     */
    public static class EventListLoader extends AsyncTaskLoader<List<News>> {
        
        final PackageManager mPm;

        List<News> mApps; 

        public EventListLoader(Context context) {
            super(context);

            // Retrieve the package manager for later use; note we don't
            // use 'context' directly but instead the save global application
            // context returned by getContext().
            mPm = getContext().getPackageManager();
        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override public List<News> loadInBackground() {
        	
        	String result = "";
        	//List<News> entries = null;
        	
        	// http post 
        	try{
        		// we do our http lifting
        		HttpClient httpclient = new DefaultHttpClient();
        		String url = "http://cs2.mwsu.edu/~msu2u/get_article_from_db.php";
        		HttpPost httppost = new HttpPost(url);
        		HttpResponse response = httpclient.execute(httppost);
        		
        		final int statusCode = response.getStatusLine().getStatusCode();
        		
        		if (statusCode != HttpStatus.SC_OK) { 
    	            Log.w(getClass().getSimpleName(), "Error " + statusCode + " for URL " + url); 
    	            return null;
    	        }
        		HttpEntity entity = response.getEntity();
        		InputStream webs = entity.getContent();
        		
        		//New test code 
        		//Reader inputStreamReader = new InputStreamReader(webs);
        		//Gson gson = new Gson();
        		//entries = (List<AppEntry>) gson.fromJson(inputStreamReader, AppEntry.class);
        		
        		// convert response to string
        		try{
        			//ERROR HERE
        			BufferedReader reader = new BufferedReader(new InputStreamReader(webs,"iso-8859-1"),8);
        			StringBuilder sb  = new StringBuilder();
        			String line = null;
        			while((line = reader.readLine()) != null){
        				sb.append(line + "\n");
        			}
        			// close our imputstream
        			webs.close();
        			//convert our string builder to a string 
        			result = sb.toString(); 
        		}catch(Exception e){
        			Log.e("log_tag", "Error converting result " +e.toString());
        		}
        	}catch(Exception e){
        		Log.e("log_tag", "Error in http connection " +e.toString());
        	}
        	
        	// parse json data
        	try{
        		
        		JSONArray jArray = new JSONArray(result);
        		entries = new ArrayList<News>(jArray.length());
        		for (int i = 0; i < jArray.length(); i++) {
					// get our object, the is one event worth of data 
        			JSONObject json_data = jArray.getJSONObject(i);
        			// create a new events
        			News resultRow = new News();
        			// set that event's attributes 
        			resultRow.setArticleID(json_data.getInt("Article_ID"));
        			resultRow.setLastChanged(json_data.getString("last_changed"));
        			resultRow.setTitle(json_data.getString("Title"));
        			resultRow.setComments(json_data.getString("Comments"));
        			resultRow.setPubDate(json_data.getString("Pub_Date"));
        			resultRow.setDocCreator(json_data.getString("Doc_Creator"));
        			resultRow.setCategory1(json_data.getString("Category_1"));
        			resultRow.setCategory2(json_data.getString("Category_2"));
        			resultRow.setShortDescription(json_data.getString("Short_Description"));
        			resultRow.setLongDescription(json_data.getString("Long_Description"));
        			resultRow.setMediaID(json_data.getInt("Media_ID"));
        			resultRow.setSourceID(json_data.getInt("Source_ID"));
        			resultRow.setArticleLink(json_data.getString("Article_Link"));
        			
        			// double check 
        			//resultRow.seteIcon(resources.getDrawable(R.drawable.ic_news));
        			// for icon, i'll pass the category and change icon base on that in the event class
        			
        			entries.add(resultRow);
        			
				}
        	}catch(Exception e){
        		Log.e("log_tag", "Error parsing data " +e.toString());
        	}
            //final Context context = getContext();
            // Done!
        	return entries;
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override public void deliverResult(List<News> apps) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (apps != null) {
                    onReleaseResources(apps);
                }
            }
            List<News> oldApps = apps;
            mApps = apps;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(apps);
            }

            // At this point we can release the resources associated with
            // 'oldApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldApps != null) {
                onReleaseResources(oldApps);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override protected void onStartLoading() {
            if (mApps != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mApps);
            }

            
            if (takeContentChanged() || mApps == null) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override public void onCanceled(List<News> apps) {
            super.onCanceled(apps);

            // At this point we can release the resources associated with 'apps'
            // if needed.
            onReleaseResources(apps);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (mApps != null) {
                onReleaseResources(mApps);
                mApps = null;
            }
        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<News> apps) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }



    public static class AppListAdapter extends ArrayAdapter<News> {
        private final LayoutInflater mInflater;

        public AppListAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<News> data) {
            clear();
            if (data != null) {
                for (News event : data) {
                    add(event);
                }
            }
        }

        /**
         * Populate new items in the list.
         */
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.list_item_icon_text, parent, false);
            } else {
                view = convertView;
            }

            News item = getItem(position);
            //((ImageView)view.findViewById(R.id.icon)).setImageDrawable(item.geteIcon());
            ((TextView)view.findViewById(R.id.text)).setText(item.getTitle());
            //((TextView)view.findViewById(R.id.text2)).setText(item.getShortDescription());

            return view;
        }
    }

    public static class AppListFragment extends SherlockListFragment
            implements LoaderManager.LoaderCallbacks<List<News>> {

        // This is the Adapter being used to display the list's data.
        AppListAdapter mAdapter;

        // If non-null, this is the current filter the user has provided.
        String mCurFilter;

        OnQueryTextListenerCompat mOnQueryTextListenerCompat;

        @Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Give some text to display if there is no data.  In a real
            // application this would come from a resource.
            setEmptyText("No News");

            // We have a menu item to show in action bar.
            setHasOptionsMenu(true);

            // Create an empty adapter we will use to display the loaded data.
            mAdapter = new AppListAdapter(getActivity());
            setListAdapter(mAdapter);

            // Start out with a progress indicator.
            setListShown(false);

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(0, null, this);
        }

        @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Place an action bar item for searching.
            MenuItem item = menu.add("Search");
            item.setIcon(android.R.drawable.ic_menu_search);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            View searchView = SearchViewCompat.newSearchView(getActivity());
            if (searchView != null) {
                SearchViewCompat.setOnQueryTextListener(searchView,
                        new OnQueryTextListenerCompat() {
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        // Called when the action bar search text has changed.  Since this
                        // is a simple array adapter, we can just have it do the filtering.
                        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
                        mAdapter.getFilter().filter(mCurFilter);
                        return true;
                    }
                });
                item.setActionView(searchView);
            }
        }

        @Override public void onListItemClick(ListView l, View v, int position, long id) {
            // Insert desired behavior here.
        	//WebView webview = (WebView) findViewById(android.R.id.);
        	
        	//News item = (News) ((Menu) entries).getItem(position);
        	//Intent anotherActivityIntent = new Intent(this, ContainerNews.class);
        	//anotherActivityIntent.putExtra("my.package.dataToPass",item);
        	//startActivity(anotherActivityIntent);
        	
            Log.i("LoaderCustom", "Item clicked: " + id);
        }

        @Override public Loader<List<News>> onCreateLoader(int id, Bundle args) {
            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader with no arguments, so it is simple.
            return new EventListLoader(getActivity());
        }

        @Override public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
            // Set the new data in the adapter.
            mAdapter.setData(data);

            // The list should now be shown.
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }

        @Override public void onLoaderReset(Loader<List<News>> loader) {
            // Clear the data in the adapter.
            mAdapter.setData(null);
        }
    }

}
