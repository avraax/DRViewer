package vraa.android.drviewer.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import vraa.android.drviewer.pojo.PodcastEpisode;
import vraa.android.drviewer.pojo.PodcastShow;
import vraa.android.drviewer.pojo.VideoPodcastEpisode;
import vraa.android.drviewer.pojo.VideoPodcastShow;

public class Scraper {
	public static List<VideoPodcastShow> getVideoPodcastShows() {
		List<VideoPodcastShow> videoPodcastShows = new ArrayList<VideoPodcastShow>();
		try {
			Document doc = Jsoup.connect("http://www.dr.dk/Podcast/video.htm").get();
			Elements shows = doc.select("p.icExternal a[title=xml]").parents().get(7).select("div.content");
			for (Element show : shows) {
				VideoPodcastShow vodcastShow = new VideoPodcastShow();
				vodcastShow.setTitle(show.select("div.txtContent a").first().text().trim());
				vodcastShow.setXmlPath(show.select("p.icExternal a[title=xml]").attr("href").trim());
				videoPodcastShows.add(vodcastShow);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return videoPodcastShows;
	}
	
	public static List<PodcastShow> getPodcastShows() {
		List<String> drPodcastUrl = new ArrayList<String>();
		drPodcastUrl.add("http://www.dr.dk/Podcast/A-G.htm");
		drPodcastUrl.add("http://www.dr.dk/Podcast/H-N.htm");
		drPodcastUrl.add("http://www.dr.dk/Podcast/O-AA.htm");
		List<PodcastShow> podcastShows = new ArrayList<PodcastShow>();
		try {
			for (String url : drPodcastUrl) {
				Document doc = Jsoup.connect(url).timeout(60000).get();
				Elements shows = doc.select("p.icExternal a[title=xml]").parents().get(7).select("div.content");
				for (Element show : shows) {
					PodcastShow vodcastShow = new PodcastShow();
					vodcastShow.setTitle(show.select("div.txtContent a").first().text().trim());
					vodcastShow.setXmlPath(show.select("p.icExternal a[title=xml]").attr("href").trim());
					podcastShows.add(vodcastShow);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return podcastShows;
	}
}
