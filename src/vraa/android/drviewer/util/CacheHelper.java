package vraa.android.drviewer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import vraa.android.drviewer.pojo.PodcastShow;
import vraa.android.drviewer.pojo.VideoPodcastShow;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class CacheHelper {
	
	static String _folderName = "DRViewer";
	public static boolean savePodcasts(Context context, List<PodcastShow> configs, String fileName){
		// check if available and not read only
		if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
			Log.w("FileUtils", "Storage not available or read only");
			return false;
		}

		// Create a path where we will place our List of objects on external
		// storage
		boolean success = false;
		if(CreateFolder(_folderName)){
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + _folderName, fileName);
			ObjectOutputStream oos = null;

			try {
				OutputStream os = new FileOutputStream(file);
				oos = new ObjectOutputStream(os);
				oos.writeObject(configs);
				success = true;
			} catch (IOException e) {
				Log.w("FileUtils", "Error writing " + file, e);
			} catch (Exception e) {
				Log.w("FileUtils", "Failed to save file", e);
			} finally {
				try {
					if (null != oos)
						oos.close();
				} catch (IOException ex) {
				}
			}
		}

		return success;
	}
	
	public static boolean saveVideoPodcast(Context context, List<VideoPodcastShow> configs, String fileName) {

		// check if available and not read only
		if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
			Log.w("FileUtils", "Storage not available or read only");
			return false;
		}

		// Create a path where we will place our List of objects on external
		// storage
		boolean success = false;
		if(CreateFolder(_folderName)){
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + _folderName, fileName);
			ObjectOutputStream oos = null;

			try {
				OutputStream os = new FileOutputStream(file);
				oos = new ObjectOutputStream(os);
				oos.writeObject(configs);
				success = true;
			} catch (IOException e) {
				Log.w("FileUtils", "Error writing " + file, e);
			} catch (Exception e) {
				Log.w("FileUtils", "Failed to save file", e);
			} finally {
				try {
					if (null != oos)
						oos.close();
				} catch (IOException ex) {
				}
			}
		}

		return success;
	}

	public static List<VideoPodcastShow> readFile(Context context, String filename) {
		ObjectInputStream ois = null;
		List<VideoPodcastShow> result = null;

		if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
			Log.w("FileUtils", "Storage not available or read only");
			return null;
		}

		try {
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + _folderName, filename);
			FileInputStream fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			result = (List<VideoPodcastShow>) ois.readObject();
			ois.close();
		} catch (Exception ex) {
			Log.e("FileUtils", "failed to load file", ex);
		} finally {
			try {
				if (null != ois)
					ois.close();
			} catch (IOException ex) {
			}
		}

		return result;
	}

	public static List<PodcastShow> readPodcasts(Context context, String filename) {
		ObjectInputStream ois = null;
		List<PodcastShow> result = null;

		if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
			Log.w("FileUtils", "Storage not available or read only");
			return null;
		}

		try {
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + _folderName, filename);
			FileInputStream fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			result = (List<PodcastShow>) ois.readObject();
			ois.close();
		} catch (Exception ex) {
			Log.e("FileUtils", "failed to load file", ex);
		} finally {
			try {
				if (null != ois)
					ois.close();
			} catch (IOException ex) {
			}
		}

		return result;
	}

	public static boolean isExternalStorageReadOnly() {
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
			return true;
		}
		return false;
	}

	public static boolean isExternalStorageAvailable() {
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
			return true;
		}
		return false;
	}

	private static boolean CreateFolder(String folderName){
		File folder = new File(Environment.getExternalStorageDirectory() + "/" + folderName);
		boolean exists = false;
		if(!folder.exists())
		{
			exists = folder.mkdir();
		}
		else
		{
			exists = true;
		}

		return exists;
	}
}