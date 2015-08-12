package com.team.http;

import com.team.http.AsyncHttpClient.HttpClientThread;


public interface OnCompleteListener {
	public void onComplete(HttpClientThread clientThread);
}
