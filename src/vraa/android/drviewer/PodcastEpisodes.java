package vraa.android.drviewer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vraa.android.drviewer.pojo.PodcastEpisode;
import vraa.android.drviewer.pojo.PodcastShow;
import vraa.android.drviewer.util.SaxFeedParser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PodcastEpisodes extends Activity {
	protected static final int MESSAGE_SUCCESS = 0;
	protected static final int MESSAGE_NO_EPISODES = 1;
	private Handler podCastsLookUpHandler;
	private PodcastShow podcastShow;
	private ListView podcastEpisodesList;
    private ProgressDialog pdPodcastEpisodes;
	private SearchAdapter searchAdapter;
    private Context context;
    private ImageView podcastShowImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.podcast_episodes);

		searchAdapter = new SearchAdapter(this);
		context = this;
		
		podcastShow = (PodcastShow) getIntent().getExtras().get("vraa.android.drviewer.PodcastShow");
		
		findAllViewsById();
		createHandlers();
		registerListeners();

		podcastEpisodesList.setAdapter(searchAdapter);
		pdPodcastEpisodes = ProgressDialog.show(this, "One moment...", "Getting vodcast episodes", true, false);
		getPodcastEpisodes(podCastsLookUpHandler.obtainMessage(), podcastShow);
	}

	private void findAllViewsById() {
		podcastEpisodesList = (ListView) findViewById(R.id.searchListView);
		podcastShowImage = (ImageView) findViewById(R.id.vodCastShowImage);
	}

	private void createHandlers() {
		podCastsLookUpHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case MESSAGE_SUCCESS:
					List<PodcastEpisode> podcastEpisodes = (List<PodcastEpisode>) msg.obj;
					if(podcastEpisodes.size() > 0){
						try {
							URL newurl = new URL(podcastEpisodes.get(0).getPodcastShowImagePath());
							Bitmap vodcastShowBitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
							podcastShowImage.setImageBitmap(vodcastShowBitmap);
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//CacheHelper.saveFile(context, vodcastEpisodes, _vodcastShowsFileName);
						searchAdapter.AddData(podcastEpisodes);
						searchAdapter.notifyDataSetChanged();
					}
					break;
				case MESSAGE_NO_EPISODES:
					break;
				}
				pdPodcastEpisodes.dismiss();
			}
		};
	}
	
	private void registerListeners() {
		podcastEpisodesList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				PodcastEpisode vodcastEpisode = searchAdapter.getItem(position);
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse(vodcastEpisode.getLink()), "audio/*");
				startActivity(intent); 
			}
		});
	}
	
	public void getPodcastEpisodes(final Message message, final PodcastShow podcastShow) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				SaxFeedParser parser = new SaxFeedParser(podcastShow.getXmlPath());
				List<PodcastEpisode> vodcastEpisodes = parser.parsePodcasts();
				
				if (vodcastEpisodes != null && vodcastEpisodes.size() > 0){
					message.what = MESSAGE_SUCCESS;
					message.obj = vodcastEpisodes;
				}
				else
					message.what = MESSAGE_NO_EPISODES;
				
				message.sendToTarget();
			}
		};
		thread.start();
	}

	private static class SearchAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<PodcastEpisode> podcastEpisodes;
		
		public SearchAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			podcastEpisodes = new ArrayList<PodcastEpisode>();
		}

		public int getCount() {
			return podcastEpisodes.size();
		}

		public PodcastEpisode getItem(int position) {
			return podcastEpisodes.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.search_list_item_vodcast_episodes, null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.vodcastShowImage = (ImageView) convertView.findViewById(R.id.vodCastShowImage);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			PodcastEpisode podCastShow = podcastEpisodes.get(position);

			holder.title.setText(podCastShow.getTitle());

			return convertView;
		}
		
		public void AddData(List<PodcastEpisode> vodcastEpisodes) {
			this.podcastEpisodes.addAll(vodcastEpisodes);
		}

		static class ViewHolder {
			TextView title;
			ImageView vodcastShowImage;
		}
	}
}
