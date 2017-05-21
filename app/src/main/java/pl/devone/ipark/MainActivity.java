package pl.devone.ipark;

import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import pl.devone.ipark.authentication.AuthenticationManager;
import pl.devone.ipark.http.utils.ConnectionUtils;
import pl.devone.ipark.utils.ActivityUtils;

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

        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void onCreateCheck() {
        if (!ConnectionUtils.isOnline(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.network_connection_error), Toast.LENGTH_LONG).show();
            navigateLoginActivity();
        }
        if (!AuthenticationManager.isAppAuthenticated(getApplicationContext())) {
            navigateLoginActivity();
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

    private void switchFragment(Fragment fragment){
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    private void navigateLoginActivity() {
        ActivityUtils.navigateActivity(this, LoginActivity.class, true);
    }

}
