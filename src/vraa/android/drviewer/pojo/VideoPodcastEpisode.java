package vraa.android.drviewer.pojo;

import java.net.URL;
import java.util.Date;

public class VideoPodcastEpisode {
	private String vodcastShowImagePath;
	private String title;
	private String link;
	private String summary;
	private String description;
	private String category;
	private String duration;
	private Date pubDate;
	private Enclosure enclosure;

	public String getVodcastShowImagePath() {
		return vodcastShowImagePath;
	}

	public void setVodcastShowImagePath(String vodcastShowImagePath) {
		this.vodcastShowImagePath = vodcastShowImagePath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setDuration(Date pubDate) {
		this.pubDate = pubDate;
	}

	public Enclosure getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(Enclosure enclosure) {
		this.enclosure = enclosure;
	}
	
	private class Enclosure{
		private URL url;
		private int length;
		private String type;


		public URL getUrl() {
			return url;
		}

		public void setUrl(URL url) {
			this.url = url;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
}
