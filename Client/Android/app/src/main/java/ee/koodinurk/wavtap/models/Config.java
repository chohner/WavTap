/**
 *
 */
package ee.koodinurk.wavtap.models;

import android.content.Context;
import android.content.SharedPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ako
 */
public class Config
{
    private final static Logger log = LoggerFactory.getLogger( Config.class );

    public final static boolean DEBUG = false;

    private static final String CONFIG_FILE = "config.pref";

    private static final String KEY_USER_HOST = "keyUserHost";
    private static final String KEY_USER_PORT = "keyUserPort";

    protected static Config sUniqueInstance;

    protected final Context mContext;
    protected final SharedPreferences mSharedPreferences;

    protected Config( Context context )
    {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences( CONFIG_FILE, Context.MODE_PRIVATE );

    }

    public static synchronized void initialize( Context context )
    {
        if ( sUniqueInstance == null )
        {
            sUniqueInstance = new Config( context );
        }
    }

    public static synchronized Config getInstance()
    {
        if ( sUniqueInstance == null )
        {
            throw new IllegalStateException( Config.class.getSimpleName()
                    + " is not initialized, call initializeInstance(..) method first." );
        }
        return sUniqueInstance;
    }

    public boolean clear()
    {
        return mSharedPreferences.edit().clear().commit();
    }

    //region Preferences

    public String getHost()
    {
        return mSharedPreferences.getString( KEY_USER_HOST, "" );
    }

    public void setHost( final String host )
    {
        mSharedPreferences.edit().putString( KEY_USER_HOST, host ).commit();
    }

    public String getPort()
    {
        return mSharedPreferences.getString( KEY_USER_PORT, "32905" );
    }

    public void setPort( final String port )
    {
        mSharedPreferences.edit().putString( KEY_USER_PORT, port ).commit();
    }

    //endregion
}
