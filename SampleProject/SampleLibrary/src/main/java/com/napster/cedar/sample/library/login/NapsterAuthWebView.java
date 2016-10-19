package com.napster.cedar.sample.library.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.napster.cedar.sample.library.AppInfo;
import com.napster.cedar.session.AuthToken;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NapsterAuthWebView extends WebView {

    private AppInfo appInfo;

	@SuppressLint("SetJavaScriptEnabled")
	public NapsterAuthWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.getSettings().setJavaScriptEnabled(true);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
	}

	public NapsterAuthWebView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NapsterAuthWebView(Context context) {
        this(context, null);
	}

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

	public void loadLogin(final String loginUrl, final NapsterLoginCallback callback) {
		setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView webView, final String url) {
                boolean shouldRedirect = url.startsWith(appInfo.getRedirectUrl());
                if(shouldRedirect) {
                    onRedirectUrl(url, callback);
                }
				return shouldRedirect;
			}
		});
		loadUrl(loginUrl);
	}

	private void onRedirectUrl(final String url, final NapsterLoginCallback callback) {
		Uri uri = Uri.parse(url);
		String code = uri.getQueryParameter(AuthenticationService.QUERY_CODE);
		if (callback != null && code == null) {
			callback.onLoginError(url, null);
		}

		Authentication auth = new Authentication(appInfo);
		auth.swapCodeForToken(code, new Callback<AuthToken>() {
			@Override
			public void success(AuthToken authToken, Response response) {
				if (callback != null) {
					callback.onLoginSuccess(authToken);
				}
			}

			@Override
			public void failure(RetrofitError error) {
				if (callback != null) {
					callback.onLoginError(url, error.getCause());
				}
			}
		});
	}

}