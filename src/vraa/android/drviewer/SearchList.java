package vraa.android.drviewer;

import java.util.ArrayList;
import java.util.List;

import vraa.android.drviewer.pojo.VideoPodcastShow;
import vraa.android.drviewer.util.DrawableManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchList extends Activity {

	private final static int MESSAGE_SUCCESS = 0;
	private final static int MESSAGE_ERROR = 2;
	
	private ListView searchList;
	private SearchAdapter searchAdapter;
	private View footerView;
	private Handler searchListHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_list);
		
		searchAdapter = new SearchAdapter(this);
		footerView = View.inflate(this, R.layout.search_list_item_progress, null);
		
		findAllViewsById();
		createHandlers();
		registerListeners();

		searchList.addFooterView(footerView);
		searchList.setAdapter(searchAdapter);
	}

	private void findAllViewsById() {
		searchList = (ListView) findViewById(R.id.searchListView);
	}

	private void createHandlers() {
		searchListHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case MESSAGE_SUCCESS:
					searchAdapter.AddData((List<VideoPodcastShow>) msg.obj);
					searchAdapter.notifyDataSetChanged();
					break;
				case MESSAGE_ERROR:
					searchList.removeFooterView(footerView);
					Toast.makeText(SearchList.this, "Der er sket en fejl", Toast.LENGTH_LONG).show();
					break;
				}
			}
		};
	}

	private void registerListeners() {
		searchList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				searchAdapter.getItem(position);
			}
		});
	}

	private static class SearchAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private DrawableManager drawableManager;
		private List<VideoPodcastShow> vodcastShows;
		
		public SearchAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			drawableManager = new DrawableManager();
			vodcastShows = new ArrayList<VideoPodcastShow>();
		}

		public int getCount() {
			return vodcastShows.size();
		}

		public VideoPodcastShow getItem(int position) {
			return vodcastShows.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.search_list_item, null);
				holder = new ViewHolder();
				//holder.price = (TextView) convertView.findViewById(R.id.itemPrice);
				//holder.description = (TextView) convertView.findViewById(R.id.itemDescription);
				//holder.location = (TextView) convertView.findViewById(R.id.itemLocation);
				//holder.image = (ImageView) convertView.findViewById(R.id.itemImage);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			VideoPodcastShow vodCastShow = vodcastShows.get(position);

			holder.price.setText("Kr. " + vodCastShow.getTitle());
			//holder.description.setText(vodCastShow.getDescription());
			//holder.location.setText(vodCastShow.getAddressZipcode() + " " + vodCastShow.getAddressCity());
			
			//if(vodCastShow.getThumbnail() != null)
			//	drawableManager.fetchDrawableOnThread(vodcastShows.get(position).getThumbnail(), holder.image);
			//else
			//	holder.image.setImageResource(R.drawable.no_pictures);

			return convertView;
		}
		
		public void AddData(List<VideoPodcastShow> vodcastShows) {
			this.vodcastShows.addAll(vodcastShows);
		}

		static class ViewHolder {
			TextView price;
		}
	}
	
}
