package vraa.android.drviewer;

import java.util.ArrayList;
import java.util.List;

import vraa.android.drviewer.pojo.PodcastShow;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Podcasts extends Activity {
	private int refetch = Menu.FIRST;
	private int home = Menu.FIRST + 1;
	private int group1Id = 1;

	protected static final int MESSAGE_SUCCESS = 0;
	protected static final int MESSAGE_NO_SHOWS = 1;
	private Handler podCastsLookUpHandler;
	private SearchAdapter searchAdapter;
	private ListView podcastsShowsList;
	private ProgressDialog pdPodcastShows;
	private Context context;
	String podcastShowsFileName = "podcastShows.txt";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.podcasts);

		searchAdapter = new SearchAdapter(this);
		context = this;

		findAllViewsById();
		localize();
		createHandlers();
		registerListeners();

		podcastsShowsList.setAdapter(searchAdapter);

		getPodcastShows(podCastsLookUpHandler.obtainMessage(), false);
	}

	private void findAllViewsById() {
		podcastsShowsList = (ListView) findViewById(R.id.searchListView);
	}

	private void createHandlers() {
		podCastsLookUpHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MESSAGE_SUCCESS:
					List<PodcastShow> podcastShows = (List<PodcastShow>) msg.obj;
					CacheHelper.savePodcasts(context, podcastShows,
							podcastShowsFileName);
					searchAdapter.AddData(podcastShows);
					searchAdapter.notifyDataSetChanged();
					break;
				case MESSAGE_NO_SHOWS:
					break;
				}
				pdPodcastShows.dismiss();
			}
		};
	}

	private void registerListeners() {
		podcastsShowsList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				PodcastShow podcastShow = searchAdapter.getItem(position);
				UIUtils.showPodcastEpisodes(Podcasts.this, podcastShow);
			}
		});
	}

	public void getPodcastShows(final Message message, final boolean refetch) {

		pdPodcastShows = ProgressDialog.show(this, "One moment...",
				"Scraping DR podcasts", true, false);
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				List<PodcastShow> videoPodcastShows = CacheHelper.readPodcasts(
						context, podcastShowsFileName);
				if (videoPodcastShows == null || refetch) {
					videoPodcastShows = Scraper.getPodcastShows();
				}
				if (videoPodcastShows != null && videoPodcastShows.size() > 0) {
					message.what = MESSAGE_SUCCESS;
					message.obj = videoPodcastShows;
				} else
					message.what = MESSAGE_NO_SHOWS;

				message.sendToTarget();
			}
		};
		thread.start();
	}

	private void localize() {
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
			getPodcastShows(podCastsLookUpHandler.obtainMessage(), true);
	        return true;
	    case 2:
	    	final Intent intent = new Intent(context, Main.class);
	    	context.startActivity(intent);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	private static class SearchAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<PodcastShow> podcastShows;

		public SearchAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			podcastShows = new ArrayList<PodcastShow>();
		}

		public int getCount() {
			return podcastShows.size();
		}

		public PodcastShow getItem(int position) {
			return podcastShows.get(position);
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
				holder.xmlPath = (TextView) convertView
						.findViewById(R.id.xmlPath);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			PodcastShow videoPodCastShow = podcastShows.get(position);

			holder.title.setText(videoPodCastShow.getTitle());
			holder.xmlPath.setText(videoPodCastShow.getXmlPath());

			return convertView;
		}

		public void AddData(List<PodcastShow> podcastShows) {
			this.podcastShows.clear();
			this.podcastShows.addAll(podcastShows);
		}

		static class ViewHolder {
			TextView title;
			TextView xmlPath;
		}
	}
}