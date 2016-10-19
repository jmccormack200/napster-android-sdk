package com.napster.cedar.sample.library.login;

import com.napster.cedar.session.AuthToken;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface AuthenticationService {

	public static final String QUERY_CLIENT_ID = "client_id";
	public static final String QUERY_CLIENT_SECRET = "client_secret";
	public static final String QUERY_RESPONSE_TYPE = "response_type";
	public static final String QUERY_GRANT_TYPE = "grant_type";
	public static final String QUERY_REDIRECT_URI = "redirect_uri";
	public static final String QUERY_CODE = "code";

	public static final String RESPONSE_TYPE_CODE = "code";
	public static final String GRANT_TYPE_CODE = "authorization_code";

	@FormUrlEncoded
	@POST("/oauth/access_token")
	public void authenticate(@Field(QUERY_CLIENT_ID) String clientId,
                             @Field(QUERY_CLIENT_SECRET) String clientSecret,
                             @Field(QUERY_RESPONSE_TYPE) String responseType,
                             @Field(QUERY_GRANT_TYPE) String grantType,
                             @Field(QUERY_REDIRECT_URI) String redirectUri,
                             @Field(QUERY_CODE) String code, Callback<AuthToken> callback);

}
