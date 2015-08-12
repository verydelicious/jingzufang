package com.jingzufang.onthread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class HttpThreadInt extends Thread{
	private static final String TAG = "onthread";
	private String url;
	private String jsonstr;
	private Bitmap bitmap = null;
	public HttpThreadInt(String host, String add){
		this.url = host + add + "?";
	}
	public HttpThreadInt(String url){
		this.url = url;
	}
	@Override
	public void run() {
			doGet();
			if(doJson(jsonstr)){
				Log.i(TAG, "解析成功");
			}
			else{
				Log.i(TAG, "解析失败");
			}
	}
	public String doGet(){
		try {
			Log.i(TAG, "url===>"+url);
			URL httpUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
			connection.setRequestMethod("GET");
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String str;
			while((str = br.readLine())  != null){
				sb.append(str);
			}
			jsonstr = sb.toString();
			return sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public boolean doJson(String json){
		try {
			if(json==null){
				return false;
			}
			Log.i(TAG, "json===>" + json);
			JSONObject root = new JSONObject(json);
			int error = root.getInt("error");
			if(error == 0){
				return true;
			}
			else{
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	public String getJson(){
		return jsonstr;
	}
	public String getUrl(){
		return url;
	}
	public void setUrl(String url){
		this.url = url;
	}
	public Bitmap getBit(){
		return bitmap;
	}
	public String uploadFile(String path)
	  {
	    String end = "\r\n";
	    String twoHyphens = "--";
	    String boundary = "******";
	    try
	    {
	      URL urls = new URL(url);
	      HttpURLConnection httpURLConnection = (HttpURLConnection) urls
	          .openConnection();
	      // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
	      // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
	      httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
	      // 允许输入输出流
	      httpURLConnection.setDoInput(true);
	      httpURLConnection.setDoOutput(true);
	      httpURLConnection.setUseCaches(false);
	      // 使用POST方法
	      httpURLConnection.setRequestMethod("POST");
	      httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
	      httpURLConnection.setRequestProperty("Charset", "UTF-8");
	      httpURLConnection.setRequestProperty("Content-Type",
	          "multipart/form-data;boundary=" + boundary);
	 
	      DataOutputStream dos = new DataOutputStream(
	          httpURLConnection.getOutputStream());
	      dos.writeBytes(twoHyphens + boundary + end);
	      dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
	          + path.substring(path.lastIndexOf("/") + 1)
	          + "\""
	          + end);
	      dos.writeBytes(end);
	 
	      FileInputStream fis = new FileInputStream(path);
	      byte[] buffer = new byte[8192]; // 8k
	      int count = 0;
	      // 读取文件
	      while ((count = fis.read(buffer)) != -1)
	      {
	        dos.write(buffer, 0, count);
	      }
	      fis.close();
	 
	      dos.writeBytes(end);
	      dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
	      dos.flush();
	 
	      InputStream is = httpURLConnection.getInputStream();
	      InputStreamReader isr = new InputStreamReader(is, "utf-8");
	      BufferedReader br = new BufferedReader(isr);
	      String result = br.readLine();
	      Log.i(TAG, result);
	      jsonstr = result;
	      dos.close();
	      is.close();
	      return result;
	    } catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    return null;
	  }
	public boolean dlTX(){
		try {
			Log.i(TAG, "url--->" + url);
			URL httpurl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) httpurl.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream is = connection.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
			return true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
