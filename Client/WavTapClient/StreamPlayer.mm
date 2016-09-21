//
//  StreamPlayer.m
//  WavTapClient
//
//  Created by Ako Tulu on 08/09/16.
//  Copyright © 2016 Koodinurk Ltd. All rights reserved.
//

#import "StreamPlayer.h"

@interface StreamPlayer()
{
    BOOL _isPlaying;
}
@property (nonatomic) AudioUnit audioUnit;
@property (nonatomic) AudioTimeStamp workTimeStamp;
@property (nonatomic) AudioBuffer workBuf;
@property (readwrite) NSUInteger packetSize;

@end

static OSStatus OutputRenderCallback (void *inRefCon,
                                      AudioUnitRenderActionFlags	* ioActionFlags,
                                      const AudioTimeStamp * inTimeStamp,
                                      UInt32 inOutputBusNumber,
                                      UInt32 inNumberFrames,
                                      AudioBufferList * ioData)
{
    StreamPlayer *self = (__bridge StreamPlayer*)inRefCon;
    
    ioData->mBuffers[0] = self.workBuf;

    return noErr;
}

@implementation StreamPlayer

- (instancetype)initWithOutputFormat:(AudioStreamBasicDescription)outputFormat;
{
    self = [super init];
    
    if (!self) {
        return nil;
    }
    
    _isPlaying = NO;
    _packetSize = outputFormat.mBytesPerFrame * 1024;
    
    _workBuf.mDataByteSize = outputFormat.mBytesPerFrame * 1024;
    _workBuf.mData = malloc(_workBuf.mDataByteSize);
    
    // create a component description
    AudioComponentDescription desc;
    desc.componentType = kAudioUnitType_Output;
    desc.componentSubType = kAudioUnitSubType_RemoteIO;
    desc.componentManufacturer = kAudioUnitManufacturer_Apple;
    desc.componentFlags = 0;
    desc.componentFlagsMask = 0;
    
    // use the description to find the component we're looking for
    AudioComponent defaultOutput = AudioComponentFindNext(NULL, &desc);
    
    // create an instance of the component and have our _audioUnit property point to it
    CheckError(AudioComponentInstanceNew(defaultOutput, &_audioUnit), "AudioComponentInstanceNew Failed");
    
    // set the audio format on the input scope (kAudioUnitScope_Input) of the output bus (0) of the output unit - got that?
    CheckError(AudioUnitSetProperty(_audioUnit, kAudioUnitProperty_StreamFormat, kAudioUnitScope_Input, 0, &outputFormat, sizeof(outputFormat)),
               "AudioUnitSetProperty StreamFormat Failed");
    
    // set up a render callback struct consisting of our output render callback (above) and a reference to self (so we can access our outputDataSource reference from within the callback)
    AURenderCallbackStruct callbackStruct;
    callbackStruct.inputProc = OutputRenderCallback;
    callbackStruct.inputProcRefCon = (__bridge void*)self;
    
    // add the callback struct to the output unit (again, that's to the input scope of the output bus)
    CheckError(AudioUnitSetProperty(_audioUnit, kAudioUnitProperty_SetRenderCallback, kAudioUnitScope_Input, 0, &callbackStruct, sizeof(callbackStruct)),
               "AudioUnitSetProperty SetRenderCallback Failed");
    
    // initialize the unit
    CheckError(AudioUnitInitialize(_audioUnit), "AudioUnitInitializeFailed");
    
    return self;
}

- (BOOL)isStreaming
{
    return _isPlaying;
}

- (void)startStream
{
    _isPlaying = YES;
    
    CheckError(AudioOutputUnitStart(_audioUnit), "Audio Output Unit Failed To Start");
}

- (void)stopStream
{
    _isPlaying = NO;
    
    CheckError(AudioOutputUnitStop(_audioUnit), "Audio Output Unit Failed To Stop");
}

- (void)play:(NSData *)data
{
    memcpy(_workBuf.mData, [data bytes], [data length]);
}

void CheckError(OSStatus error, const char *operation)
{
    if (error == noErr) return;
    
    char errorString[20];
    // see if it appears to be a 4-char code
    *(UInt32 *)(errorString + 1) = CFSwapInt32HostToBig(error);
    if (isprint(errorString[1]) && isprint(errorString[2]) &&
        isprint(errorString[3]) && isprint(errorString[4])) {
        errorString[0] = errorString[5] = '\'';
        errorString[6] = '\0';
    } else {
        // No, format it as an integer
        fprintf(stderr, "Error: %s (%s)\n", operation, errorString);
        exit(1);
    }
}

@end

