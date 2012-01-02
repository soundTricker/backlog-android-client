package com.googlecode.stk.android.backlog.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.common.collect.Lists;
import com.googlecode.stk.android.backlog.Const;
import com.googlecode.stk.android.backlog.db.entity.Convertable;

public class Util {

	@SuppressWarnings("unchecked")
	public static <T extends Convertable> List<T> convertList(Object[] objects,Class<T> clazz) {
		
		if(objects == null) {
			return null;
		}
		
		try {
			
			List<T> results = Lists.newArrayList();
			for (Object object : objects) {
				T t = clazz.newInstance();
				t.set((Map<String,Object>) object);
				results.add(t);
			}
			
			return results;
		} catch (Exception e) {
			//TODO handle error
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * DBファイルをSDカードにコピーする AndroidManifest.xmlにWRITE_EXTERNAL_STORAGEを設定すること
	 *
	 * @param Context
	 *            context メソッド呼び出し元(Activity等)のContext
	 * @param String
	 *            dbName コピー元となるデータベースファイル名
	 * @return コピーに成功した場合true
	 * @throws IOException
	 *             なんかエラーが起きた場合にthrow
	 */
	public static boolean copyDb2Sd(Context context, String dbName)
			throws IOException {

		final String TAG = "copyDb2Sd";

		// 保存先(SDカード)のディレクトリを確保
		String pathSd = getSdPath(context);
		File filePathToSaved = new File(pathSd);
		if (!filePathToSaved.exists() && !filePathToSaved.mkdirs()) {
			throw new IOException("FAILED_TO_CREATE_PATH_ON_SD");
		}

		final String fileDb = context.getDatabasePath(dbName).getPath();
		final String fileSd = new StringBuilder().append(pathSd).append("/")
				.append(dbName).append(".").append(
						(new SimpleDateFormat("yyyyMMddHHmmss"))
								.format(new Date())).toString();

		Log.i(TAG, "copy from(DB): " + fileDb);
		Log.i(TAG, "copy to(SD)  : " + fileSd);

		FileChannel channelSource = new FileInputStream(fileDb).getChannel();
		FileChannel channelTarget = new FileOutputStream(fileSd).getChannel();

		channelSource.transferTo(0, channelSource.size(), channelTarget);

		channelSource.close();
		channelTarget.close();

		return true;
	}

	private static String getSdPath(Context context) {
		String pathSd = new StringBuilder().append(
				Environment.getExternalStorageDirectory().getPath())
				.append("/").append(context.getPackageName()).toString();
		return pathSd;
	}

	public static String writeBinaryFile2Sd(Context context, byte[] bytes,String appendDir, String fileName) throws IOException {
		// 保存先(SDカード)のディレクトリを確保
		String pathSd = getSdPath(context);
		final String fileSd = new StringBuilder().append(pathSd).append("/").append(appendDir).toString();
		File filePathToSaved = new File(fileSd);
		if (!filePathToSaved.exists() && !filePathToSaved.mkdirs()) {
			throw new IOException("FAILED_TO_CREATE_PATH_ON_SD");
		}

		File file = new File(filePathToSaved,fileName);

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			fos.write(bytes , 0 , bytes.length);
			Log.i(Const.TAG, "write file to " + file.getPath());
		} finally {
			close(fos);
		}

		return file.getPath();
	}

	public static void writeBinaryFile(Context context, byte[] bytes, String fileName) throws IOException {
		OutputStream os = null;

		try {
			os = context.openFileOutput(fileName, Context.MODE_PRIVATE);

			os.write(bytes, 0, bytes.length);

		} finally {
			close(os);
		}
	}
	public static void close(Closeable is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (IOException e2) {
		}
	}



}
