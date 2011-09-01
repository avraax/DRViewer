package vraa.android.drviewer.pojo;

import java.io.Serializable;

public class PodcastShow implements Serializable {
	private String title;
	private String xmlPath;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}
}