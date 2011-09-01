package vraa.android.drviewer;

import vraa.android.drviewer.util.UIUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity {
	private Button videoPodcastsButton;
	private Button podcastsButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		findAllViewsById();
		registerListeners();
	}

	private void findAllViewsById() {
		videoPodcastsButton = (Button) findViewById(R.id.videoPodcastsButton);
		podcastsButton = (Button) findViewById(R.id.podcastsButton);
	}

	private void registerListeners() {
		videoPodcastsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UIUtils.showVodcasts(Main.this);
			}
		});
		podcastsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UIUtils.showPodcasts(Main.this);
			}
		});
	}
}