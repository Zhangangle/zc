package com.jimome.mm.utils;

import java.io.File;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore.Video;

public class IOUtils {
	
	public static  ContentValues videoContentValues = null;
	private static final String APP_ROOTPATH = "jiaoyou";
	private static final String SQLITE_DATABASEPATH = "android";
	private static final String SQLITE_DATABASEFILE= "jimome.db";
	private static final String UPDATEFILE="jiaoyou.apk";
	private static final String IMAGECACHE_PATH = "imageCache";
	private static final String VIDEOCACHE_PATH = "videoCache";
	
	public final static String FILE_START_NAME = "VM_";
	public final static String VIDEO_EXTENSION = ".mp4";
	public final static String VIDEO_FOLDER = "/Video";
	public final static String CAMERA_FOLDER = "/Camera";
	public final static String TEMP_FOLDER = "/Temp";
	public final static String CAMERA_FOLDER_PATH = Environment.getExternalStorageDirectory()+"/jimome"+VIDEO_FOLDER;
	public final static String TEMP_FOLDER_PATH = VIDEO_FOLDER + CAMERA_FOLDER +TEMP_FOLDER;
	
	public static String VIDEO_THUMBNAIL = "thumbnail";
	
	public static String DOWNLOAD_LXHAPK = "/downloads/apk";
	
	public static File getApplicationFolder() {
		File root = Environment.getExternalStorageDirectory();
		if (root.canWrite()) {
			
			File f = new File(root, APP_ROOTPATH);
			if (!f.exists()) {
				f.mkdir();
			}
			return f;
			
		} else {
			return null;
		}
	}
	
	
	//获取图片缓存文件路径
	
	public static File getImagecacheFile(){
		File root = getApplicationFolder();
		if (root != null) {
			
			File folder = new File(root, IMAGECACHE_PATH);
			if (!folder.exists()) {
				folder.delete();
			}
			return folder;
		} else {
			return null;
		}
	}
	
	//获取视频缓存文件路径
	
		public static File getVideocacheFile(){
			File root = getApplicationFolder();
			if (root != null) {
				
				File folder = new File(root, VIDEOCACHE_PATH);
				if (!folder.exists()) {
					folder.mkdir();
				}
				return folder;
			} else {
				return null;
			}
		}
	
	public static File getVideoFile(String name) {

		File root = getVideocacheFile();
		if (root != null) {
			File file = new File(root, name);
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return file;

		} else {
			return null;
		}
	}
		
	
	//获取更新apk文件路径
	public static File getAppUpdateFile(){
		File root = getApplicationFolder();
		if (root != null) {
			
			File folder = new File(root, UPDATEFILE);
			if (!folder.exists()) {
				folder.delete();
			}
			return folder;
		} else {
			return null;
		}
	}
	
	/**
	 * 获取冷笑话APK下载路径
	 *
	 */
	public static File getLXHAPKFolder(String filename){
		File root = getApplicationFolder();
		File file = null ;
		if (root != null) {
			try {
			File folder = new File(root,DOWNLOAD_LXHAPK);
			
				if (!folder.exists()) {
					folder.mkdir();
				}
				 file = new File(folder,filename);
				 if (!file.exists()) {
			            file.createNewFile();
			           }
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			return file;
			
		} else {
			return null;
		}
	}
	
	/**
	 * 获取cjAPK下载路径
	 *
	 */
	public static File getcjVideoAPKFolder(String filename){
		File root = getApplicationFolder();
		File file = null ;
		if (root != null) {
			try {
			File folder = new File(root,DOWNLOAD_LXHAPK);
			
				if (!folder.exists()) {
					folder.mkdirs();
				}
				 file = new File(folder,filename);
				 if (!file.exists()) {
			            file.createNewFile();
			           }
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			return file;
			
		} else {
			return null;
		}
	}
	
	/**
	 * 获取数据库路径
	 */
	public static File getDatabaseFolder() {
		File root = getApplicationFolder();
		File file = null ;
		if (root != null) {
			try {
			File folder = new File(root,SQLITE_DATABASEPATH);
			
				if (!folder.exists()) {
					folder.mkdir();
				}
				 file = new File(folder,SQLITE_DATABASEFILE);
				 if (!file.exists()) {
			            file.createNewFile();
			           }
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			return file;
			
		} else {
			return null;
		}
	}
	
	
	public static File createVideoThumbnailPath(){
		File root = getApplicationFolder();
		File file = null;
		if(root != null){
			File folder = new File(root,VIDEO_THUMBNAIL);
			if(!folder.exists())
				folder.mkdir();
			 file = new File(folder,"thumbnail_video"+".jpg");
			 try {
				 if (!file.exists()) {
			            file.createNewFile();
			           }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		 return file;
		 
	}
	
	public static String createFinalPath()
	{
		long dateTaken = System.currentTimeMillis();
		String title = FILE_START_NAME + dateTaken;
		String filename = title +VIDEO_EXTENSION;
		String filePath = genrateFilePath(String.valueOf(dateTaken), true, null);
		ContentValues values = new ContentValues(7);
		values.put(Video.Media.TITLE, title);
		values.put(Video.Media.DISPLAY_NAME, filename);
		values.put(Video.Media.DATE_TAKEN, dateTaken);
		values.put(Video.Media.MIME_TYPE, "video/3gpp");
		values.put(Video.Media.DATA, filePath);
		videoContentValues = values;

		return filePath;
	}

	private static String genrateFilePath(String uniqueId, boolean isFinalPath, File tempFolderPath)
	{
		
		String fileName = FILE_START_NAME + uniqueId + VIDEO_EXTENSION;
		String dirPath = "";
		if(isFinalPath)
		{
			new File(CAMERA_FOLDER_PATH).mkdirs();
			dirPath = CAMERA_FOLDER_PATH;
		}
		else
			dirPath = tempFolderPath.getAbsolutePath();
		String filePath = dirPath + "/" + fileName;
		return filePath;
	}
	public static String createTempPath(File tempFolderPath )
	{
		long dateTaken = System.currentTimeMillis();
		String filePath = genrateFilePath(String.valueOf(dateTaken), false, tempFolderPath);
		return filePath;
	}



	public static File getTempFolderPath()
	{
		File root = getApplicationFolder();
		File tempFolder = new File(root,TEMP_FOLDER_PATH +"_" +System.currentTimeMillis());
		return tempFolder;
	}
	
	/**
	 * 获取缓存文件路径
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public static File getDiskCacheDir(Context context, String cacheName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + cacheName);
	}
}
