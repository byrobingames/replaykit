## Stencyl ReplayKit for iOS9/Android 5.0 (Stencyl/Openfl)

For Stencyl 3.4 and above

Stencyl extension for “ReplayKit” for iOS . This extension allows you to easily integrate ReplayKit on your Stencyl game / application. (http://www.stencyl.com)

<span style="color:red;">ReplayKit is only available on the following devices running iOS 9 or above; iPad Air 2, iPad Mini 2, iPad Mini 3, iPad 5 Air, iPhone 5s, iPhone 6 and iPhone 6 Plus.

On Android  is ReplayKit only available on devices running Android 5.0 (Lollipop) or above.<br/>
For Android make sure you selected API 21 or higher in Mobile settings->version->Android Target version</span>

### Important!!

This Extension Required the Toolset Extension Manager [https://byrobingames.github.io](https://byrobingames.github.io)

![replaykittoolset](https://byrobingames.github.io/img/replaykit/replaykittoolset.png)

## Main Features

  * Recording video and share
  
## How to Install

To install this Engine Extension, go to the toolset (byRobin Extension Mananger) in the Extension menu of your game inside Stencyl.<br/>
Select the Engine Extension from the left menu and click on "Download"

If you not have byRobin Extension Mananger installed, install this first.
Go to: [https://byrobingames.github.io](https://byrobingames.github.io)

## Documentation and Block Examples

<span style="color:red;">For Android make sure you selected API 21 or higher in Mobile settings->version->Android Target version</span>

1)Use the Initialize ReplayKit block in your first (loading)scene in “when created” Event.<br/>
Use this block only once a session. If you want to ask the user to view the video or discard the video select YES, else select NO. If No the video will been viewed automatically after stop recording.<br/>
![initialize_replaykit-300x68](https://byrobingames.github.io/img/replaykit/initialize_replaykit-300x68.png)

2) EXAMPLE: Create an Actor “Start/Stop Record button”<br/>
![recordbutton](https://byrobingames.github.io/img/replaykit/recordbutton.png)

3)EXAMPLE: In the Events tab of the Actor create an boolean Attribute and set this to true when Actor is created.<br/>
![buttonwhencreated](https://byrobingames.github.io/img/replaykit/buttonwhencreated.png)

4)EXAMPLE: In the Events tab of the actor add an when the mouse is pressed on Self Event.
- Setup the Actor animation like example below.
- Put the Start Recording block between the if startRecording statement, when startRecording is true then set startRecording to false and switch to animation 1 (stop button) and Start Recoding.
- Put the Stop Recording in the otherwise block, set the startRecording to true again and switch back to animation 0 (start button) and Stop Recording.<br/>
![whenpressed-300x252](https://byrobingames.github.io/img/replaykit/whenpressed-300x252.png)

5)EXAMPLE: Create an scene where you want to create the Actor (Start/Stop button). In the Events tab add an when created event.
  - Put the replaykit available block in an if statement and put the create actor between it.<br/>
Because ReplayKit works only on iOS9 or above and Android 5.0 or above and because it is not available on all devices, you only want to show the Recording button on devices where the Replaykit is available.<br/>
If the ReplayKit is not available it will return false and the Actor will never be created.<br/>
![isReplayKitAvailable](https://byrobingames.github.io/img/replaykit/isReplayKitAvailable.png)

6) Test it and play with it. You can share the video when you preview the video. Apple has set an standard share message that cannot be modified.<br/>
Standard message when you testing is:<br/>
<strong>[app store app name] from [developer]<br/>
http://www.apple.com<br/></strong>
This message will be set to your game when you publish you game.<br/>

7)isRecoding block.
This boolean block turns true when recording start and turns to false when recording stops.<br/>
If the ReplayKit is not available it will return false and the Actor will never be created.<br/>
![replaykitisrecording](https://byrobingames.github.io/img/replaykit/replaykitisrecording.png)

## Version History

- 2016-04-06 (0.0.1) First release
- 2016-04-08 (0.0.2) Fix: Crash on iOS8 and lower.
- 2016-10-01 (0.0.3)<br/>
– Android 5.0> support (works only on device thats running Android 5 or higher<br/>
– Added is Cancelled boolean block<br/>
– Added ask for Preview option in Initialize block (ask user to view video after recording)<br/>
- 2016-10-03 (0.0.4) Update for iOS 10 startRecordingWithMicrophoneEnabled is Deprecated in iOS 10, need to call startRecordingWithHandler in iOS 10.
- 2017-03-19 (0.0.5) Added Gradle support for openfl4
- 2017-03-21 (0.0.6) Fix: Audio stops playing after dismiss Preview, Added previewController didOpened/didClosed boolean block. (for pause your gameaudio when preview didopened and resume when closed)
- 2017-05-16(0.0.7) Tested for Stencyl 3.5, Required byRobin Toolset Extension Manager

## Submitting a Pull Request

This software is opensource.<br/>
If you want to contribute you can make a pull request

Repository: [https://github.com/byrobingames/replaykit](https://github.com/byrobingames/replaykit)

Need help with a pull request?<br/>
[https://help.github.com/articles/creating-a-pull-request/](https://help.github.com/articles/creating-a-pull-request/)

## Donate

[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=HKLGFCAGKBMFL)<br />

## License

Author: Robin Schaafsma

The MIT License (MIT)

Copyright (c) 2014 byRobinGames [http://www.byrobin.nl](http://www.byrobin.nl)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
