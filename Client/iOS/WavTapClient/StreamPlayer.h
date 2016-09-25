//
//  StreamPlayer.h
//  WavTapClient
//
//  Created by Ako Tulu on 08/09/16.
//  Copyright © 2016 Koodinurk Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AudioToolbox/AudioToolbox.h>

@interface StreamPlayer : NSObject

@property (readonly) NSUInteger packetSize;

- (instancetype)initWithOutputFormat:(AudioStreamBasicDescription)outputFormat;

- (BOOL)isStreaming;

- (void)startStream;
- (void)stopStream;

- (void)play:(NSData *)data;

@end
