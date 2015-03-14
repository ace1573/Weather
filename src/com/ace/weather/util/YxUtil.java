package com.ace.weather.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.ace.weather.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 工具�?
 */
public final class YxUtil {
	private static final String TAG = "YxUtil";

	// 显示Toast提示
	private static Toast toast;

	private static Handler handler = new Handler();

	public static void showToast(Context mContext, String text) {
		if (toast == null) {
			toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);
		}

		handler.post(new Runnable() {

			@Override
			public void run() {
				toast.show();
			}
		});
	}

	// 字符不为空判�?
	public static boolean isNull(String string) {
		if (string == null || string.equals("") || string.equals("null")) {
			return true;
		}

		return false;
	}

	/**
	 * 安装App
	 */
	public static void installApp(Context context, File apkFile) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");

		context.startActivity(intent);
		// YxCfgManager.getYxCfgInstance().write(YxAppCfg.IS_FIRST, true);
	}

	/**
	 * 判断是否有网�?
	 * 
	 * @param mContext
	 * 
	 * @return boolean
	 */
	public static boolean isNetworkAvailable(Context mContext) {
		boolean result = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (cm != null) {
				NetworkInfo ni = cm.getActiveNetworkInfo();
				if (ni != null) {
					result = ni.isAvailable();
				}
			}
		} catch (Exception e) {
		}

		return result;

	}

	public static String getAppVersionName(Context context) {
		String appVersion = "";
		PackageManager packageManager = context.getPackageManager();

		try {
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			appVersion = packInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return appVersion;
	}

	private static void showUpdateDialog(final Activity context, final String str, final String version, final String url) {
		try {
			new AlertDialog.Builder(context).setTitle("发现新版本客户端").setMessage("是否现在升级？升级内容为：\n" + str).setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/*
					 * SystemProp sp = new SystemProp();
					 * sp.setSystemPropActivity(context); sp.update(version,
					 * url);
					 */
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {

				}
			}).show();
		} catch (Exception e) {

		}
	}

	public static String BASE_DIR = null;

	public static String getAvailableStoragePath(Context context) {
		if (BASE_DIR == null) {
			try {
				BASE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
				StorageManager sStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

				Class<?> smClazz = sStorageManager.getClass();
				Method listMethod = smClazz.getDeclaredMethod("getVolumeList");
				Object vlObject = listMethod.invoke(sStorageManager);

				if (vlObject.getClass().isArray()) {
					String state = null;
					String path = null;
					// Class svClazz =
					// Class.forName("android.os.storage.StorageVolume");
					Object svObject = Array.get(vlObject, 1);
					if (svObject != null) {
						Method pathMethod = svObject.getClass().getMethod("getPath");
						path = (String) pathMethod.invoke(svObject);

						Method stateMethod = smClazz.getMethod("getVolumeState", new Class[] { String.class });
						state = (String) stateMethod.invoke(sStorageManager, path);
					}

					if (path != null && state != null && state.equals(Environment.MEDIA_MOUNTED)) {
						BASE_DIR = path;
					} else {
						BASE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
					}
				}
			} catch (Exception e) {
				BASE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
			}

			return BASE_DIR;
		} else {
			return BASE_DIR;
		}

	}

	public static boolean isCurrentStorageMounted(Context context) {
		try {
			String availPath = getAvailableStoragePath(context);
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			// if (currentapiVersion >= android.os.Build.VERSION_CODES.){
			// 当前可用的存储器为SD�?
			if (Environment.getExternalStorageDirectory().getAbsolutePath().equals(availPath)) {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					return true;
				}
			}
			// 当前可用的存储器为内置存储卡
			else {
				StorageManager sStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
				Class<?> smClazz = sStorageManager.getClass();
				Method listMethod = smClazz.getDeclaredMethod("getVolumeList");
				Object vlObject = listMethod.invoke(sStorageManager);

				if (vlObject.getClass().isArray()) {
					String state = null;
					String path = null;
					// Class svClazz =
					// Class.forName("android.os.storage.StorageVolume");
					Object svObject = Array.get(vlObject, 1);
					if (svObject != null) {
						Method pathMethod = svObject.getClass().getMethod("getPath");
						path = (String) pathMethod.invoke(svObject);

						Method stateMethod = smClazz.getMethod("getVolumeState", new Class[] { String.class });
						state = (String) stateMethod.invoke(sStorageManager, path);
					}
					if (path != null && state != null && state.equals(Environment.MEDIA_MOUNTED)) {
						return true;
					}
				}
			}
			// }else{
			// return
			// android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
			// }
		} catch (Exception e) {
			Log.e("", e.getMessage());
		}

		return false;
	}

	/**
	 * 计算字符串长度（中文�?个字符）
	 * 
	 * @param str
	 * @return
	 */
	public static int getStrLen(String str) {
		int len = 0;
		if (str != null && !"".equals(str)) {
			try {
				str = new String(str.getBytes("GBK"), "ISO8859_1");
				len = str.length();
			} catch (UnsupportedEncodingException e) {
			}
		}
		return len;
	}

	public static void setViewParams(View v, int dpX, int dpY) {
		Context context = v.getContext();
		int pxX = (int) (0.5F + dpX * context.getResources().getDisplayMetrics().density);
		int pxY = (int) (0.5F + dpY * context.getResources().getDisplayMetrics().density);

		LayoutParams params = v.getLayoutParams();
		params.height = pxX;
		params.width = pxY;
		v.setLayoutParams(params);
	}

}
