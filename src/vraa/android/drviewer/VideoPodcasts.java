package vraa.android.drviewer;

import java.util.ArrayList;
import java.util.List;

import vraa.android.drviewer.pojo.VideoPodcastShow;
import vraa.android.drviewer.util.CacheHelper;
import vraa.android.drviewer.util.Scraper;
import vraa.android.drviewer.util.UIUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class VideoPodcasts extends Activity {
	private int refetch = Menu.FIRST;
	private int home = Menu.FIRST + 1;
	private int group1Id = 1;

	protected static final int MESSAGE_SUCCESS = 0;
	protected static final int MESSAGE_NO_SHOWS = 1;
	private Handler videoPodCastsLookUpHandler;
	private SearchAdapter searchAdapter;
	private ListView videoPodcastsShowsList;
    private ProgressDialog pdVideoPodcastShows;
    private Context context;
	String _videoPodcastShowsFileName = "videoPodcastShows.txt";
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vodcasts);
		
		searchAdapter = new SearchAdapter(this);
		context = this;
		
		findAllViewsById();
		localize();
		createHandlers();
		registerListeners();

		videoPodcastsShowsList.setAdapter(searchAdapter);

		getVideoPodcastShows(videoPodCastsLookUpHandler.obtainMessage(), false);
    }

    private void findAllViewsById() {
    	videoPodcastsShowsList = (ListView) findViewById(R.id.searchListView);
    }

	private void createHandlers() {
		videoPodCastsLookUpHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case MESSAGE_SUCCESS:
					List<VideoPodcastShow> videoPodcastShows = (List<VideoPodcastShow>) msg.obj;
					CacheHelper.saveVideoPodcast(context, videoPodcastShows, _videoPodcastShowsFileName);
					searchAdapter.AddData(videoPodcastShows);
					searchAdapter.notifyDataSetChanged();
					break;
				case MESSAGE_NO_SHOWS:
					break;
				}
				pdVideoPodcastShows.dismiss();
			}
		};
	}
	
	private void registerListeners() {
		videoPodcastsShowsList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				VideoPodcastShow videoPodcastShow = searchAdapter.getItem(position);
				UIUtils.showVideoPodcastEpisodes(VideoPodcasts.this, videoPodcastShow);
			}
		});
	}
	
	public void getVideoPodcastShows(final Message message, final boolean refetch) {
        pdVideoPodcastShows = ProgressDialog.show(this, "One moment...", "Scraping DR video podcasts", true, false);
		Thread thread = new Thread() {
			@Override
			public void run() {
				List<VideoPodcastShow> videoPodcastShows = CacheHelper.readFile(context, _videoPodcastShowsFileName);
				if (videoPodcastShows == null || refetch){ videoPodcastShows = Scraper.getVideoPodcastShows(); }
				if (videoPodcastShows != null && videoPodcastShows.size() > 0){
					message.what = MESSAGE_SUCCESS;
					message.obj = videoPodcastShows;
				}
				else
					message.what = MESSAGE_NO_SHOWS;
				
				message.sendToTarget();
			}
		};
		thread.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(group1Id, refetch, refetch, "Refecth");
		menu.add(group1Id, home, home, "Home");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case 1:
			getVideoPodcastShows(videoPodCastsLookUpHandler.obtainMessage(), true);
	        return true;
	    case 2:
	    	final Intent intent = new Intent(context, Main.class);
	    	context.startActivity(intent);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	private void localize(){
	}
	
	private static class SearchAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<VideoPodcastShow> videoPodcastShows;
		
		public SearchAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			videoPodcastShows = new ArrayList<VideoPodcastShow>();
		}

		public int getCount() {
			return videoPodcastShows.size();
		}

		public VideoPodcastShow getItem(int position) {
			return videoPodcastShows.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.search_list_item, null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.xmlPath = (TextView) convertView.findViewById(R.id.xmlPath);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			VideoPodcastShow videoPodCastShow = videoPodcastShows.get(position);

			holder.title.setText(videoPodCastShow.getTitle());
			holder.xmlPath.setText(videoPodCastShow.getXmlPath());

			return convertView;
		}
		
		public void AddData(List<VideoPodcastShow> videoPodcastShows) {
			this.videoPodcastShows.addAll(videoPodcastShows);
		}

		static class ViewHolder {
			TextView title;
			TextView xmlPath;
		}
	}
}