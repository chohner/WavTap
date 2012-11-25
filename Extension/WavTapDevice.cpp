#include "WavTapDevice.hpp"
#include "WavTapEngine.hpp"
#include <IOKit/audio/IOAudioControl.h>
#include <IOKit/audio/IOAudioLevelControl.h>
#include <IOKit/audio/IOAudioToggleControl.h>
#include <IOKit/audio/IOAudioDefines.h>
#include <IOKit/IOLib.h>

#define super IOAudioDevice

OSDefineMetaClassAndStructors(WavTapDevice, IOAudioDevice)

const SInt32 WavTapDevice::kVolumeMax = 99;
const SInt32 WavTapDevice::kGainMax = 99;

bool WavTapDevice::initHardware(IOService *provider) {
  if (!super::initHardware(provider)) {
    return false;
  }
  setDeviceName("WavTap");
  setDeviceShortName("WavTap");
  setManufacturerName("WavTap");
  if (!createAudioEngines()){
    return false;
  }
  return true;
}

bool WavTapDevice::createAudioEngines() {
  OSArray *audioEngineArray = OSDynamicCast(OSArray, getProperty(AUDIO_ENGINES_KEY));
  OSCollectionIterator *audioEngineIterator;
  OSDictionary *audioEngineDict;
  if (!audioEngineArray) {
    IOLog("WavTapDevice[%p]::createAudioEngine() - Error: no AudioEngine array in personality.\n", this);
    return false;
  }
  audioEngineIterator = OSCollectionIterator::withCollection(audioEngineArray);
  if (!audioEngineIterator) {
    IOLog("WavTapDevice: no audio engines available.\n");
    return true;
  }
  while ((audioEngineDict = (OSDictionary*)audioEngineIterator->getNextObject())) {
    WavTapEngine *audioEngine = NULL;
    if (OSDynamicCast(OSDictionary, audioEngineDict) == NULL)
      continue;
    audioEngine = new WavTapEngine;
    if (!audioEngine)
      continue;
    if (!audioEngine->init(audioEngineDict))
      continue;
    initControls(audioEngine);
    activateAudioEngine(audioEngine);
    audioEngine->release();
  }
  audioEngineIterator->release();
  return true;
}

#define addControl(control, handler) \
  if (!control) {\
    IOLog("WavTap failed to add control.\n");  \
    return false; \
  } \
  control->setValueChangeHandler(handler, this); \
  audioEngine->addDefaultAudioControl(control); \
  control->release();

bool WavTapDevice::initControls(WavTapEngine* audioEngine) {
  IOAudioControl *control = NULL;
  for (UInt32 channel=0; channel <= NUM_CHANS; channel++) {
    mVolume[channel] = mGain[channel] = kVolumeMax;
    mMuteOut[channel] = mMuteIn[channel] = false;
  }
  const char *channelNameMap[NUM_CHANS+1] = {
    kIOAudioControlChannelNameAll,
    kIOAudioControlChannelNameLeft,
    kIOAudioControlChannelNameRight,
    kIOAudioControlChannelNameCenter,
    kIOAudioControlChannelNameLeftRear,
    kIOAudioControlChannelNameRightRear,
    kIOAudioControlChannelNameSub
  };
  for (UInt32 channel=7; channel <= NUM_CHANS; channel++)
    channelNameMap[channel] = "Unknown Channel";
  for (unsigned channel=0; channel <= NUM_CHANS; channel++) {
    control = IOAudioLevelControl::createVolumeControl(WavTapDevice::kVolumeMax, 0, WavTapDevice::kVolumeMax, (-40 << 16) + (32768), 0, channel, channelNameMap[channel], channel, kIOAudioControlUsageOutput);
    addControl(control, (IOAudioControl::IntValueChangeHandler)volumeChangeHandler);
    control = IOAudioLevelControl::createVolumeControl(WavTapDevice::kGainMax, 0, WavTapDevice::kGainMax, 0, (40 << 16) + (32768), channel, channelNameMap[channel], channel, kIOAudioControlUsageInput);
    addControl(control, (IOAudioControl::IntValueChangeHandler)gainChangeHandler);
  }
  control = IOAudioToggleControl::createMuteControl(false, kIOAudioControlChannelIDAll, kIOAudioControlChannelNameAll, 0, kIOAudioControlUsageOutput);
  addControl(control, (IOAudioControl::IntValueChangeHandler)outputMuteChangeHandler);
  control = IOAudioToggleControl::createMuteControl(false, kIOAudioControlChannelIDAll, kIOAudioControlChannelNameAll, 0, kIOAudioControlUsageInput);
  addControl(control, (IOAudioControl::IntValueChangeHandler)inputMuteChangeHandler);
  return true;
}

IOReturn WavTapDevice::volumeChangeHandler(IOService *target, IOAudioControl *volumeControl, SInt32 oldValue, SInt32 newValue) {
  IOReturn result = kIOReturnBadArgument;
  WavTapDevice *audioDevice = (WavTapDevice *)target;
  if (audioDevice)
    result = audioDevice->volumeChanged(volumeControl, oldValue, newValue);
  return result;
}

IOReturn WavTapDevice::volumeChanged(IOAudioControl *volumeControl, SInt32 oldValue, SInt32 newValue) {
  if (volumeControl)
    mVolume[volumeControl->getChannelID()] = newValue;
  return kIOReturnSuccess;
}

IOReturn WavTapDevice::outputMuteChangeHandler(IOService *target, IOAudioControl *muteControl, SInt32 oldValue, SInt32 newValue) {
  IOReturn result = kIOReturnBadArgument;
  WavTapDevice *audioDevice = (WavTapDevice*)target;
  if (audioDevice)
    result = audioDevice->outputMuteChanged(muteControl, oldValue, newValue);
  return result;
}

IOReturn WavTapDevice::outputMuteChanged(IOAudioControl *muteControl, SInt32 oldValue, SInt32 newValue) {
  if (muteControl)
    mMuteOut[muteControl->getChannelID()] = newValue;
  return kIOReturnSuccess;
}

IOReturn WavTapDevice::gainChangeHandler(IOService *target, IOAudioControl *gainControl, SInt32 oldValue, SInt32 newValue) {
  IOReturn result = kIOReturnBadArgument;
  WavTapDevice *audioDevice = (WavTapDevice *)target;
  if (audioDevice)
    result = audioDevice->gainChanged(gainControl, oldValue, newValue);
  return result;
}

IOReturn WavTapDevice::gainChanged(IOAudioControl *gainControl, SInt32 oldValue, SInt32 newValue) {
  if (gainControl)
    mGain[gainControl->getChannelID()] = newValue;
  return kIOReturnSuccess;
}

IOReturn WavTapDevice::inputMuteChangeHandler(IOService *target, IOAudioControl *muteControl, SInt32 oldValue, SInt32 newValue) {
  IOReturn result = kIOReturnBadArgument;
  WavTapDevice *audioDevice = (WavTapDevice*)target;
  if (audioDevice)
    result = audioDevice->inputMuteChanged(muteControl, oldValue, newValue);
  return result;
}

IOReturn WavTapDevice::inputMuteChanged(IOAudioControl *muteControl, SInt32 oldValue, SInt32 newValue) {
  if (muteControl)
    mMuteIn[muteControl->getChannelID()] = newValue;
  return kIOReturnSuccess;
}
