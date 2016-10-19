package com.napster.cedar.sample.library;

import java.io.Serializable;

public abstract class AppInfo implements Serializable{

    public abstract String getApiKey();
    public abstract String getSecret();
    public abstract String getRedirectUrl();

}
