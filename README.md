# Android DrawingActivity
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/88a25db5ab6c4c44b440814778de4236)](https://app.codacy.com/gh/ChilliBits/drawing-activity?utm_source=github.com&utm_medium=referral&utm_content=ChilliBits/drawing-activity&utm_campaign=Badge_Grade_Dashboard)
![Android CI](https://github.com/ChilliBits/drawing-activity/workflows/Android%20CI/badge.svg)
[![](https://jitpack.io/v/ChilliBits/drawing-activity.svg)](https://jitpack.io/#ChilliBits/drawing-activity)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-DrawingActivity-blue.svg?style=flat)](https://android-arsenal.com/details/1/7098)
[![API](https://img.shields.io/badge/API-19%2B-red.svg?style=flat)](https://android-arsenal.com/api?level=19)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

A lightweight android library for including a drawing experience to your Android app.

![1](https://chillibits.com/github-media/DrawingActivity/1_small.png)
![2](https://chillibits.com/github-media/DrawingActivity/2_small.png)
![3](https://chillibits.com/github-media/DrawingActivity/3_small.png)

## Installation
Up to now, the library is only available on JitPack. Please add this code to your build.gradle file on project level:
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
implementation 'com.github.ChilliBits:drawing-activity:2.0.7'
```

## Usage
To use the DrawingActivity, include this code in an event function:
```kotlin
DrawingActivityBuilder.getInstance(this@MainActivity)
	.draw(REQ_DRAWING)
```
Thus `REQ_DRAWING` is a constant of type integer to specify the request code of the returning intent for the `onActivityResult` method.

If you want to have a look onto a implemented example, view the [MainActivity.kt](https://github.com/ChilliBits/splash-screen/blob/master/app/src/main/java/com/chillibits/splashscreenexample/MainActivity.kt) or the [MainActivity.java](https://github.com/ChilliBits/splash-screen/blob/master/app/src/main/java/com/chillibits/splashscreenexample/MainActivityJava.java) file.

You are able to catch the event of finishing the drawing and closing the DrawingActivty by using the `onActivityResult` method like that:
```kotlin
if(requestCode == REQ_DRAWING && resultCode == RESULT_OK && data != null) {
    val drawingPath = data.getStringExtra(DrawingActivity.DRAWING_PATH)
    Toast.makeText(this, drawingPath, Toast.LENGTH_LONG).show()
}
```
Replace the Toast with your own code, processing the returned image (loading the image into your app, sharing it, cropping it, etc.).

You can customize the appearance of the DrawingActivity using following arguments when building the Activity with `DrawingActivityBuilder`:

| Method                         | Description                                                                                                                                                                                                                                        |
|--------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| enableToast(boolean enabled)   | Enables or disables the toast on Activity startup (You can see the toast on the animated demo above).                                                                                                                                              |
| setTitle(String title)         | Sets the title in the toolbar at the top of the DrawingActivity.                                                                                                                                                                                   |
| setDefaultUtility(int utility) | Sets the default utility which will be selected on Activity startup. You have to pass an int argument to this method. The utility constants can be accessed by using e.g. `DrawingActivity.UTILITY_PENCIL` or `DrawingActivity.UTILITY_AIR_BRUSH`. |

## Credits
This library uses following third party libraries:
*   [DrawingView](https://github.com/Raed-Mughaus/DrawingView) (Repo does not exist anymore)
*   [SlidingUpPanel](https://github.com/umano/AndroidSlidingUpPanel)
*   [ColorPickerPreference](https://github.com/attenzione/android-ColorPickerPreference)
*   [Android FilePicker](https://github.com/DroidNinja/Android-FilePicker)
*   [Glide](https://github.com/bumptech/glide)

## Contributions
If you want to contribute to this library, feel free to open a pr! We're going to merge it asap.

Thank you for using the DrawingActivity!

© ChilliBits 2018-2021 (Designed and developed by Marc Auberer)