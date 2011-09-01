package vraa.android.drviewer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vraa.android.drviewer.pojo.VideoPodcastEpisode;
import vraa.android.drviewer.pojo.VideoPodcastShow;
import vraa.android.drviewer.util.CacheHelper;
import vraa.android.drviewer.util.DrawableManager;
import vraa.android.drviewer.util.SaxFeedParser;
import vraa.android.drviewer.util.Scraper;
import vraa.android.drviewer.util.UIUtils;

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

public class VideoPodcastEpisodes extends Activity {
	protected static final int MESSAGE_SUCCESS = 0;
	protected static final int MESSAGE_NO_EPISODES = 1;
	private Handler vodCastsLookUpHandler;
	private VideoPodcastShow vodcastShow;
	private ListView vodcastEpisodesList;
    private ProgressDialog pdVodcastEpisodes;
	private SearchAdapter searchAdapter;
    private Context context;
    private ImageView vodcastShowImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vodcast_episodes);

		searchAdapter = new SearchAdapter(this);
		context = this;
		
		vodcastShow = (VideoPodcastShow) getIntent().getExtras().get("vraa.android.drviewer.VideoPodcastShow");
		
		findAllViewsById();
		createHandlers();
		registerListeners();

		vodcastEpisodesList.setAdapter(searchAdapter);
		pdVodcastEpisodes = ProgressDialog.show(this, "One moment...", "Getting vodcast episodes", true, false);
		getVodcastEpisodes(vodCastsLookUpHandler.obtainMessage(), vodcastShow);
	}

	private void findAllViewsById() {
		vodcastEpisodesList = (ListView) findViewById(R.id.searchListView);
		vodcastShowImage = (ImageView) findViewById(R.id.vodCastShowImage);
	}

	private void createHandlers() {
		vodCastsLookUpHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case MESSAGE_SUCCESS:
					List<VideoPodcastEpisode> vodcastEpisodes = (List<VideoPodcastEpisode>) msg.obj;
					if(vodcastEpisodes.size() > 0){
						try {
							URL newurl = new URL(vodcastEpisodes.get(0).getVodcastShowImagePath());
							Bitmap vodcastShowBitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
							vodcastShowImage.setImageBitmap(vodcastShowBitmap);
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//CacheHelper.saveFile(context, vodcastEpisodes, _vodcastShowsFileName);
						searchAdapter.AddData(vodcastEpisodes);
						searchAdapter.notifyDataSetChanged();
					}
					break;
				case MESSAGE_NO_EPISODES:
					break;
				}
				pdVodcastEpisodes.dismiss();
			}
		};
	}
	
	private void registerListeners() {
		vodcastEpisodesList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				VideoPodcastEpisode vodcastEpisode = searchAdapter.getItem(position);
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse(vodcastEpisode.getLink()), "video/*");
				startActivity(intent); 
			}
		});
	}
	
	public void getVodcastEpisodes(final Message message, final VideoPodcastShow vodcastShow) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				SaxFeedParser parser = new SaxFeedParser(vodcastShow.getXmlPath());
				List<VideoPodcastEpisode> vodcastEpisodes = parser.parseVideoPodcast();
				
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
		private List<VideoPodcastEpisode> vodcastEpisodes;
		
		public SearchAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			vodcastEpisodes = new ArrayList<VideoPodcastEpisode>();
		}

		public int getCount() {
			return vodcastEpisodes.size();
		}

		public VideoPodcastEpisode getItem(int position) {
			return vodcastEpisodes.get(position);
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
			
			VideoPodcastEpisode vodCastShow = vodcastEpisodes.get(position);

			holder.title.setText(vodCastShow.getTitle());

			return convertView;
		}
		
		public void AddData(List<VideoPodcastEpisode> vodcastEpisodes) {
			this.vodcastEpisodes.addAll(vodcastEpisodes);
		}

		static class ViewHolder {
			TextView title;
			ImageView vodcastShowImage;
		}
	}
}
