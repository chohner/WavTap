package ee.koodinurk.wavtap.ui.activities;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ee.koodinurk.wavtap.R;
import ee.koodinurk.wavtap.models.AudioStreamBasicDescription;
import ee.koodinurk.wavtap.models.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class MainActivity extends BaseActivity
{
    private final static Logger log = LoggerFactory.getLogger( MainActivity.class );

    @BindView(R.id.et_host)
    protected EditText mHostEditText;

    @BindView(R.id.et_port)
    protected EditText mPortEditText;

    @BindViews({ R.id.et_host, R.id.et_port })
    protected List< View > mTouchViews;

    protected Socket mSocket;

    protected DataInputStream mDataInputStream;

    protected int getLayoutId()
    {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        mHostEditText.setText( Config.getInstance().getHost() );
        mPortEditText.setText( Config.getInstance().getPort() );
    }

    //region Button actions

    @OnClick(R.id.btn_connect)
    protected void onConnectClicked()
    {
        log.info( "onConnectClicked" );

        final String host = mHostEditText.getText().toString();
        final String port = mPortEditText.getText().toString();

        if ( host.isEmpty() || port.isEmpty() )
        {
            Toast.makeText( this, R.string.ERROR_SRV_CREDS_MISSING, Toast.LENGTH_LONG ).show();
        }
        else
        {
            new Thread( new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        mSocket = new Socket( host, Integer.parseInt( port ) );
                        mDataInputStream = new DataInputStream( mSocket.getInputStream() );

                        byte[] message = new byte[ 40 ];
                        mDataInputStream.readFully( message, 0, message.length );

                        AudioStreamBasicDescription asbd = new AudioStreamBasicDescription( message );

                        Config.getInstance().setHost( host );
                        Config.getInstance().setPort( port );

                        int bufferSize = AudioTrack.getMinBufferSize( asbd.getSampleRate(), asbd.getChannelsPerFrame(), AudioFormat.ENCODING_PCM_FLOAT );

                        AudioTrack mAudioTrack = new AudioTrack( AudioManager.STREAM_MUSIC, asbd.getSampleRate(), asbd.getChannelsPerFrame(), AudioFormat.ENCODING_PCM_FLOAT, bufferSize, AudioTrack.MODE_STREAM );
                        mAudioTrack.play();

                        byte[] data = new byte[ asbd.getBytesPerFrame() * 1024 ];

                        while ( true )
                        {
                            mDataInputStream.readFully( data, 0, data.length );
                            mAudioTrack.write( data, 0, data.length );
                        }
                    }
                    catch ( final Exception e )
                    {
                        log.error( e.getLocalizedMessage() );

                        new Handler( Looper.getMainLooper() ).post( new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText( MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG ).show();

                                ButterKnife.apply( mTouchViews, ENABLED, true );
                            }
                        } );
                    }
                }
            } ).start();

            ButterKnife.apply( mTouchViews, ENABLED, false );
        }
    }

    @OnClick(R.id.btn_disconnect)
    protected void onDisconnectClicked()
    {
        log.info( "onDisconnectClicked" );

        ButterKnife.apply( mTouchViews, ENABLED, true );
    }

    //endregion
}
