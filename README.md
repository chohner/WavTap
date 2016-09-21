## WavTap

Capture whatever your mac is playing to a .wav file on your Desktop—as simply as a screenshot or use **iOS app** to listen audio on local network. 

![](screenshot.png)

(This is alpha software. It will cause your computer to catch fire. 🔥)

#### Installation

##### El Capitan 

**Turning Off Rootless System Integrity Protection in OS X El Capitan 10.11 +** due to Apple's [System Integrity Protection] (https://en.wikipedia.org/wiki/System_Integrity_Protection). 

1. Reboot the Mac and hold down Command + R keys simultaneously after you hear the startup chime, this will boot OS X into Recovery Mode
2. When the “OS X Utilities” screen appears, pull down the ‘Utilities’ menu at the top of the screen instead, and choose “Terminal”
3. Type the following command into the terminal then hit return:
```shell
csrutil disable; reboot
```
4. You’ll see a message saying that System Integrity Protection has been disabled and the Mac needs to restart for changes to take effect, and the Mac will then reboot itself automatically, just let it boot up as normal


##### Yosemite

As of Yosemite, Apple bans drivers that haven't received explicit approval from Apple. The only workaround I'm aware of is to set a system flag to [globally allow **all** unsigned kernel extensions](http://apple.stackexchange.com/questions/163059/how-can-i-disable-kext-signing-in-mac-os-x-10-10-yosemite). This means WavTap *will not work* unless you've enabled `kext-dev-mode`, using something like this:

`
sudo nvram boot-args=kext-dev-mode=1
`

Yes, [this sucks](https://www.gnu.org/philosophy/can-you-trust.html).

Once that's done, run the
`
sudo make install
`

#### Uninstallation

`sudo make uninstall` removes everything

#### Broadcast

Default port is defined in Info.plist as property WTBroadcastPort: 32905

##### Nerd Corner

WavTap began as a fork of [Soundflower](https://github.com/Cycling74/Soundflower). thanks to [Cycling '74](http://cycling74.com), [tap](http://github.com/tap), [ma++ ingalls](http://sfsound.org/matt.html), and everyone else who's contributed to it.
