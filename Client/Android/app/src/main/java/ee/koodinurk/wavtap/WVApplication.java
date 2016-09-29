package ee.koodinurk.wavtap;

import android.app.Application;
import android.content.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WVApplication extends Application
{
    private final static Logger log = LoggerFactory.getLogger( WVApplication.class );

    private static Context sContext;
    private static boolean sActivityVisible;

    public static Context getContext()
    {
        return sContext;
    }

    public static boolean isActivityVisible()
    {
        return sActivityVisible;
    }

    public static void activityResumed()
    {
        sActivityVisible = true;
    }

    public static void activityPaused()
    {
        sActivityVisible = false;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        sContext = this;

        //SystemClock.sleep( TimeUnit.SECONDS.toMillis(3));
    }
}
