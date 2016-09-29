package ee.koodinurk.wavtap.ui.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import butterknife.ButterKnife;
import ee.koodinurk.wavtap.WVApplication;

/**
 * @author Ako
 */
public abstract class BaseActivity extends AppCompatActivity
{
    protected final ButterKnife.Setter< View, Boolean > ENABLED = new ButterKnife.Setter< View, Boolean >()
    {
        @Override
        public void set( View view, Boolean value, int index )
        {
            view.setEnabled( value );
        }
    };
    protected final ButterKnife.Setter< View, Boolean > VISIBILITY = new ButterKnife.Setter< View, Boolean >()
    {
        @Override
        public void set( View view, Boolean value, int index )
        {
            view.setVisibility( value ? View.VISIBLE : View.GONE );
        }
    };

    @Override
    protected void onCreate( final Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        setContentView( getLayoutId() );

        ButterKnife.bind( this );
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        WVApplication.activityResumed();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        WVApplication.activityPaused();
    }

    protected abstract int getLayoutId();

    public boolean isInternetAvailable()
    {
        ConnectivityManager connectivityManager
                = ( ConnectivityManager ) getSystemService( Context.CONNECTIVITY_SERVICE );

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
