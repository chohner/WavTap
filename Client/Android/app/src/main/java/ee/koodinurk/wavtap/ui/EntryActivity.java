package ee.koodinurk.wavtap.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import ee.koodinurk.wavtap.models.Config;
import ee.koodinurk.wavtap.ui.activities.MainActivity;

/**
 * @author Ako
 */
public class EntryActivity extends Activity
{
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        Config.initialize( this );

        Intent intent = new Intent( this, MainActivity.class );
        startActivity( intent );
        finish();
    }
}
