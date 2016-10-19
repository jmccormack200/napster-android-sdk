package com.napster.cedar.sample.library;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.napster.cedar.Napster;
import com.napster.cedar.NapsterError;
import com.napster.cedar.sample.library.login.NapsterLoginDialogFragment;
import com.napster.cedar.sample.library.login.NapsterLoginCallback;
import com.napster.cedar.session.AuthToken;
import com.napster.cedar.session.SessionCallback;
import com.napster.cedar.session.SessionManager;

public abstract class MainActivity extends ActionBarActivity {

    private Napster napster;
    private SessionManager sessionManager;
    NapsterLoginDialogFragment loginDialog;
    AppInfo appInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        NapsterSampleApplication app = (NapsterSampleApplication) getApplication();
        napster = app.getNapster();
        appInfo = app.getAppInfo();
        sessionManager = app.getSessionManager();
        replaceActiveFragment();
	}

    protected abstract Fragment getFragment();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        menu.findItem(R.id.menu_item_login).setVisible(!sessionManager.isSessionOpen());
        menu.findItem(R.id.menu_item_logout).setVisible(sessionManager.isSessionOpen());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_login) {
            login();
            return true;
        } else if (id == R.id.menu_item_logout) {
            logout();
            invalidateOptionsMenu();
            return true;
        }
        return false;
    }

    private void login() {
        String loginUrl = napster.getLoginUrl(appInfo.getRedirectUrl());
        loginDialog = NapsterLoginDialogFragment.newInstance(loginUrl, appInfo);
        loginDialog.setLoginCallback(loginCallback);
        loginDialog.show(getSupportFragmentManager(), "login");
    }

    private void logout() {
        sessionManager.closeSession();
        onLogout();
    }

    private void replaceActiveFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, getFragment()).commit();
    }

    protected void refreshActiveFragment() {
        getSupportFragmentManager().beginTransaction().detach(getFragment()).attach(getFragment()).commit();
    }

    protected void removeActiveFragment() {
        getSupportFragmentManager().beginTransaction().detach(getFragment()).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(loginDialog != null) {
            loginDialog.dismiss();
        }
    }

    protected abstract void onLogin();
    protected abstract void onLogout();

    NapsterLoginCallback loginCallback = new NapsterLoginCallback() {
        @Override
        public void onLoginSuccess(AuthToken authToken) {
            sessionManager.openSession(authToken, new SessionCallback() {
                @Override
                public void onSuccess() {
                    loginDialog.dismiss();
                    invalidateOptionsMenu();
                    onLogin();
                }

                @Override
                public void onError(NapsterError error) {
                    loginDialog.dismiss();
                    invalidateOptionsMenu();
                }
            });
        }

        @Override
        public void onLoginError(String url, Throwable e) {
            Toast.makeText(MainActivity.this, getString(R.string.login_error), Toast.LENGTH_LONG).show();
            loginDialog.dismiss();
        }

    };

}
