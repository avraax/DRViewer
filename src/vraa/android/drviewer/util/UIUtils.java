package vraa.android.drviewer.util;

import vraa.android.drviewer.PodcastEpisodes;
import vraa.android.drviewer.Podcasts;
import vraa.android.drviewer.VideoPodcasts;
import vraa.android.drviewer.VideoPodcastEpisodes;
import vraa.android.drviewer.pojo.PodcastShow;
import vraa.android.drviewer.pojo.VideoPodcastShow;
import android.content.Context;
import android.content.Intent;

public class UIUtils {

    public static void showVideoPodcastEpisodes(Context context, VideoPodcastShow videoPodcastShow) {
    	final Intent intent = new Intent(context, VideoPodcastEpisodes.class);
    	intent.putExtra("vraa.android.drviewer.VideoPodcastShow", videoPodcastShow);
    	context.startActivity(intent);
    }

    public static void showPodcastEpisodes(Context context, PodcastShow podcastShow) {
    	final Intent intent = new Intent(context, PodcastEpisodes.class);
    	intent.putExtra("vraa.android.drviewer.PodcastShow", podcastShow);
    	context.startActivity(intent);
    }
    
    public static void showVodcasts(Context context) {
    	final Intent intent = new Intent(context, VideoPodcasts.class);
    	context.startActivity(intent);
    }
    
    public static void showPodcasts(Context context) {
    	final Intent intent = new Intent(context, Podcasts.class);
    	context.startActivity(intent);
    }
}