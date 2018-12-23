package com.zzteck.cardect.btprinter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtils {

	public static final String FILESYMBOL = "filesymbol";
	public static final String FILENM = "filenm";
	public static final String FILETYPE = "filetype";

	public static final int FILETYPE_DIR = 1;
	public static final int FILETYPE_FILE = 0;

	// public List<Map<String, Object>> formatlist(String path) {
	// List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	// File currentpath = new File(path);
	// FileListFilter filter = new FileListFilter();
	// filter.setOnlyDir(true);
	// for (String filenm : currentpath.list(filter)) {
	// Map<String, Object> map = new HashMap<String, Object>();
	// map.put(FILESYMBOL, R.drawable.collections_collection);
	// map.put(FILENM, filenm);
	// map.put(FILETYPE, FILETYPE_DIR);
	// list.add(map);
	// }
	//
	// filter.setOnlyDir(false);
	// for (String filenm : currentpath.list(filter)) {
	// Map<String, Object> map = new HashMap<String, Object>();
	// map.put(FILESYMBOL, R.drawable.blank);
	// map.put(FILENM, filenm);
	// map.put(FILETYPE, FILETYPE_FILE);
	// list.add(map);
	// }
	// return list;
	// }

	/**
	 * ファイルコピー
	 * 
	 * @param srcFile
	 * @param dstFile
	 * @param rewrite
	 */
	public static void copyFile(File srcFile, File dstFile, Boolean rewrite) {
		if (!srcFile.exists()) {
			return;
		}
		if (!srcFile.isFile()) {
			return;
		}
		if (!srcFile.canRead()) {
			return;
		}
		if (!dstFile.getParentFile().exists()) {
			dstFile.getParentFile().mkdirs();
		}
		if (dstFile.exists() && rewrite) {
			dstFile.delete();
		}
		try {
			FileInputStream fosfrom = new FileInputStream(srcFile);
			FileOutputStream fosto = new FileOutputStream(dstFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
		} catch (Exception ex) {

		}
	}
}
