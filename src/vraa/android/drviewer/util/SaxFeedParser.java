package vraa.android.drviewer.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.SAXException;

import vraa.android.drviewer.VideoPodcastEpisodes;
import vraa.android.drviewer.pojo.PodcastEpisode;
import vraa.android.drviewer.pojo.PodcastEpisode.Enclosure;
import vraa.android.drviewer.pojo.VideoPodcastEpisode;
import vraa.android.drviewer.pojo.VideoPodcastShow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import android.widget.ImageView;

public class SaxFeedParser {
	final URL feedUrl;

	public SaxFeedParser(String feedUrl) {
		try {
			this.feedUrl = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private InputStream getInputStream() {
		try {
			return feedUrl.openConnection().getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<VideoPodcastEpisode> parseVideoPodcast() {
		List<VideoPodcastEpisode> vodcastEpisodes = new ArrayList<VideoPodcastEpisode>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = factory.newDocumentBuilder();
			Document doc = db.parse(getInputStream());
			
			String vodcastImageUrl = getPodcastShowImageUrl(doc);


			NodeList nodeList = doc.getElementsByTagName("item");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nodeVodcast = nodeList.item(i);

				VideoPodcastEpisode vodcastEpisode = new VideoPodcastEpisode();
				vodcastEpisode.setTitle(getValue(nodeVodcast, "title"));
				vodcastEpisode.setLink(getValue(nodeVodcast, "link"));
				vodcastEpisode.setVodcastShowImagePath(vodcastImageUrl);
				vodcastEpisodes.add(vodcastEpisode);
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return vodcastEpisodes;
	}

	public List<PodcastEpisode> parsePodcasts() {
		List<PodcastEpisode> podcastEpisodes = new ArrayList<PodcastEpisode>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = factory.newDocumentBuilder();
			Document doc = db.parse(getInputStream());
			String vodcastImageUrl = getPodcastShowImageUrl(doc);
			NodeList nodeList = doc.getElementsByTagName("item");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nodePodcast = nodeList.item(i);

				PodcastEpisode podcastEpisode = new PodcastEpisode();
				podcastEpisode.setTitle(getValue(nodePodcast, "title"));
				podcastEpisode.setLink(getValue(nodePodcast, "link"));
				podcastEpisode.setVodcastShowImagePath(vodcastImageUrl);
				setEnclosure(podcastEpisode, nodePodcast);
				podcastEpisodes.add(podcastEpisode);
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return podcastEpisodes;
	}

	private String getPodcastShowImageUrl(Document doc) {
		//Adding podcast show image
		NodeList nodeListVodcastImage = doc.getElementsByTagName("channel");
		Element fstElmnt = (Element) nodeListVodcastImage.item(0);
		NodeList nameList = fstElmnt.getElementsByTagName("itunes:image");
		Element nameElement = (Element) nameList.item(0);
		String vodcastImageUrl = ((Node) nameElement).getAttributes().getNamedItem("href").getNodeValue();
		return vodcastImageUrl;
	}

	private String getValue(Node node, String value) {
		Element fstElmnt = (Element) node;
		if (fstElmnt != null){
			NodeList nameList = fstElmnt.getElementsByTagName(value);
			if (nameList != null){
				Element nameElement = (Element) nameList.item(0);
				if (nameElement != null){
					nameList = nameElement.getChildNodes();
					if (nameList.getLength() > 0){
						return ((Node) nameList.item(0)).getNodeValue();
					}
				}
			}
		}
		return null;
	}
	
	private void setEnclosure(PodcastEpisode podcastEpisode, Node node){
		Enclosure enclosure = podcastEpisode.getEnclosure();
		enclosure.setLength(0);
	}
}
