package com.napster.cedar.sample.library.login;

import com.napster.cedar.session.AuthToken;

public interface NapsterLoginCallback {

	public void onLoginSuccess(AuthToken authToken);

	public void onLoginError(String url, Throwable e);

}
