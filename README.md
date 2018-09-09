# Android DrawingActivity

[![](https://jitpack.io/v/mrgames13/DrawingActivity.svg)](https://jitpack.io/#mrgames13/DrawingActivity)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-DrawingActivity-blue.svg?style=flat)](https://android-arsenal.com/details/1/7098)
[![API](https://img.shields.io/badge/API-19%2B-red.svg?style=flat)](https://android-arsenal.com/api?level=19)

A lightweight android library for including a drawing experience to your android app.

![1](https://mrgames-server.de/files/github/1_small.png) ![2](https://mrgames-server.de/files/github/2_small.png) ![3](https://mrgames-server.de/files/github/3_small.png)

# Installation

Up to now, the library is only available in JitPack. Please add this code to your build.gradle file on project level:
```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
To load the library into your project use this code in the build.gradle file within the app module:
```gradle
  implementation 'com.github.mrgames13:DrawingActivity:1.0.4'
```
# Usage
To use the DrawingActivity, include this code in a event function:
```android
  DrawingActivityBuilder.getInstance(MainActivity.this)
                        .draw(REQ_DRAWING);
```
Thus `REQ_DRAWING` is a constant of type integer to specify the request code for the returning intent for the `onActivityResult` method.

You are able to catch the event of finishing the drawing and exiting the DrawingActivty by using the `onActivityResult` method like that:
```android
if(requestCode == REQ_DRAWING && resultCode == RESULT_OK && data != null) {
    String drawing_path = data.getStringExtra(DrawingActivity.DRAWING_PATH);
    Toast.makeText(this, drawing_path, Toast.LENGTH_LONG).show();
}
```
Replace the Toast with your own code, processing the returned image (Loading the image into your app, sharing it, cropping it, etc.).

You can customize the appearance of the DrawingActivity using following arguments when building the Activity with `DrawingActivityBuilder`:

Method | Description
-------|------------
enableToast(boolean enabled) | Enables or disables the toast on Activity startup (You can see the toast on the animated demo above).
setTitle(String title) | This method sets the title in the toolbar at the top of the DrawingActivity.
setDefaultUtility(int utility) | You can set the default utility which will be selected at the startup of the Activity. You have to pass an integer argument to this method. The utility constants can be accessed by using e.g. `DrawingActivity.UTILITY_PENCIL` or `DrawingActivity.UTILITY_AIR_BRUSH`.

This library uses following third party libraries:
* [DrawingView](https://github.com/Raed-Mughaus/DrawingView)
* [SlidingUpPanel](https://github.com/umano/AndroidSlidingUpPanel)
* [ColorPickerPreference](https://github.com/attenzione/android-ColorPickerPreference)
* [Android FilePicker](https://github.com/DroidNinja/Android-FilePicker)
* [Glide](https://github.com/bumptech/glide)


Thank you for using the DrawingActivity!

© M&R Games 2018 (Designed and developed by Marc Auberer in 2018)
