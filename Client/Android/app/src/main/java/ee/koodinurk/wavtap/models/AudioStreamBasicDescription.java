package ee.koodinurk.wavtap.models;

import java.util.Arrays;

/*
▿ AudioStreamBasicDescription
  - mSampleRate : 44100.0
  - mFormatID : 1819304813
  - mFormatFlags : 9
  - mBytesPerPacket : 8
  - mFramesPerPacket : 1
  - mBytesPerFrame : 8
  - mChannelsPerFrame : 2
  - mBitsPerChannel : 32
  - mReserved : 0
 */

/**
 * @author Ako
 */
public class AudioStreamBasicDescription
{
    private final int mSampleRate; // 64-bit floating point type Double
    private final int mFormatID;  // 32-bit unsigned integer value
    private final int mFormatFlags;
    private final int mBytesPerPacket;
    private final int mFramesPerPacket;
    private final int mBytesPerFrame;
    private final int mChannelsPerFrame;
    private final int mBitsPerChannel;
    private final int mReserved;

    public AudioStreamBasicDescription( byte[] b )
    {
        mSampleRate = ( int ) toDouble( Arrays.copyOfRange( b, 0, 8 ) );
        mFormatID = toInt( Arrays.copyOfRange( b, 8, 12 ) );
        mFormatFlags = toInt( Arrays.copyOfRange( b, 12, 16 ) );
        mBytesPerPacket = toInt( Arrays.copyOfRange( b, 16, 20 ) );
        mFramesPerPacket = toInt( Arrays.copyOfRange( b, 20, 24 ) );
        mBytesPerFrame = toInt( Arrays.copyOfRange( b, 24, 28 ) );
        mChannelsPerFrame = toInt( Arrays.copyOfRange( b, 28, 32 ) );
        mBitsPerChannel = toInt( Arrays.copyOfRange( b, 32, 36 ) );
        mReserved = toInt( Arrays.copyOfRange( b, 36, 40 ) );
    }

    public int getSampleRate()
    {
        return mSampleRate;
    }

    public int getFormatID()
    {
        return mFormatID;
    }

    public int getFormatFlags()
    {
        return mFormatFlags;
    }

    public int getBytesPerPacket()
    {
        return mBytesPerPacket;
    }

    public int getFramesPerPacket()
    {
        return mFramesPerPacket;
    }

    public int getBytesPerFrame()
    {
        return mBytesPerFrame;
    }

    public int getChannelsPerFrame()
    {
        return mChannelsPerFrame;
    }

    public int getBitsPerChannel()
    {
        return mBitsPerChannel;
    }

    public int getReserved()
    {
        return mReserved;
    }

    private static int toInt( byte[] data )
    {
        if ( data == null || data.length != 4 )
        {
            return 0x0;
        }
        return ( 0xff & data[ 0 ] ) << 0 |
                ( 0xff & data[ 1 ] ) << 8 |
                ( 0xff & data[ 2 ] ) << 16 |
                ( 0xff & data[ 3 ] ) << 24;
    }

    private static long toLong( byte[] data )
    {
        if ( data == null || data.length != 8 )
        {
            return 0x0;
        }
        return ( 0xffl & data[ 0 ] ) << 0 |
                ( 0xffl & data[ 1 ] ) << 8 |
                ( 0xffl & data[ 2 ] ) << 16 |
                ( 0xffl & data[ 3 ] ) << 24 |
                ( 0xffl & data[ 4 ] ) << 32 |
                ( 0xffl & data[ 5 ] ) << 40 |
                ( 0xffl & data[ 6 ] ) << 48 |
                ( 0xffl & data[ 7 ] ) << 56;
    }

    private static double toDouble( byte[] data )
    {
        if ( data == null || data.length != 8 )
        {
            return 0x0;
        }
        return Double.longBitsToDouble( toLong( data ) );
    }
}
