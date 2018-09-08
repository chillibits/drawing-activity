# Android DrawingActivity

[![API](https://img.shields.io/badge/API-19%2B-red.svg?style=flat)](https://android-arsenal.com/api?level=19)
[![](https://jitpack.io/v/mrgames13/DrawingActivity.svg)](https://jitpack.io/#mrgames13/DrawingActivity)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-DrawingActivity-blue.svg?style=flat)](https://android-arsenal.com/details/1/7098)

A lightweight android library for including a drawing experience to your android app.

![DemoAnimation](DrawingActivity.gif)

# Installation

Until now, the library is only available in JitPack. Please add this code to your build.gradle file on project level:
```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
To load the library into your project use this code in the build.gradle file in the app module:
```gradle
  implementation 'com.github.mrgames13:DrawingActivity:1.0.1'
```
# Usage
To use the DrawingActivity include this in a event function:
```android
  DrawingActivityBuilder.getInstance(MainActivity.this)
                        .draw(REQ_DRAWING);
```
Thus `REQ_DRAWING` is a constant of type integer to specify the request code for the returning intent for the `onActivityResult` method.

You are able to catch the event of exiting the DrawingActivty by using the `onActivityResult` method:
```android
if(requestCode == REQ_DRAWING && resultCode == RESULT_OK && data != null) {
    String drawing_path = data.getStringExtra(DrawingActivity.DRAWING_PATH);
    Toast.makeText(this, drawing_path, Toast.LENGTH_LONG).show();
}
```
Replace the Toast with your 

© M&R Games 2018 (Designed and developed by Marc Auberer in 2018)
