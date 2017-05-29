package pl.devone.ipark.activities;

import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import pl.devone.ipark.fragments.MainFragment;
import pl.devone.ipark.fragments.NavigationDrawerFragment;
import pl.devone.ipark.R;
import pl.devone.ipark.services.authentication.AuthenticationManager;
import pl.devone.ipark.services.http.RestClient;
import pl.devone.ipark.activities.helpers.CommonHelper;
import pl.devone.ipark.services.http.helpers.HttpHelper;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onCreateCheck();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mTitle = getTitle();
    }

    private void onCreateCheck() {
        if (!HttpHelper.isOnline(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.network_connection_error), Toast.LENGTH_LONG).show();
            CommonHelper.navigateLoginActivity(this);
        }
        if (!AuthenticationManager.isAppAuthenticated(getApplicationContext())) {
            CommonHelper.navigateLoginActivity(this);
        } else {
            RestClient.setAuthorizationHeader(CommonHelper.getUser(this).getAuthToken());
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                mTitle = getString(R.string.title_main);
                switchFragment(new MainFragment());
                break;
            case 1:
                AuthenticationManager.signOut(this);
                return;
            case 2:
                this.finish();
                System.exit(0);
        }
    }

    private void switchFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }
}
