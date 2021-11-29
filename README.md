Title: TransUnion TruValidate Device Risk Android SDK Library

# TRUVALIDATE DEVICE RISK ANDROID SDK LIBRARY

## What is TruValidate Device Risk?
**FraudForce is now Device Risk. Our device-based products, such as Device Risk and Device-Based Authentication (formerly ClearKey), are critical components of our fraud and identity solutions; the new names make it easy to quickly understand our extensive capabilities. We have united these solutions under the TransUnion TruValidate brand. We have taken care not to update anything that might affect your implementations; as a result you'll still see legacy names in some places.**

## Overview

Follow these steps to implement the TruValidate Device Risk SDK for Android.

## About Mobile Integration

TransUnion identifies devices through information collected by the Device Risk SDK run on an end-user’s mobile device. The Device Risk SDK inspects the device to generate a blackbox that contains all device information available. This blackbox must then be transmitted to your servers to be used in a risk check.

The Device Risk SDK integrates with native and hybrid apps. Hybrid apps mix native code with content that runs inside a web view.

## Android Integration Files and Requirements


|                                 |                                                                                                                   |
|---------------------------------|-------------------------------------------------------------------------------------------------------------------|
| **SDK Filename**                | fraudforce-lib-release-4.3.2.aar                                                                                  |
| **Version**                     | 4.3.2                                                                                                             |
| **Package**                     | com.iovation.mobile.android.FraudForce                                                                            |
| **Android SDK Dependencies**    | Android SDK 5.0 or higher (SDK level 21)                                                                          |
| **Library Dependencies**        | None                                                                                                              |
| **Required Permissions**        | None                                                                                                              |
| **Optional Permissions**        | BLUETOOTH (up to Android 11), BLUETOOTH_CONNECT (starting on Android 12), CAMERA, ACCESS\_WIFI\_STATE,            |
|                                 | READ\_PHONE\_STATE, ACCESS\_FINE\_LOCATION, ACCESS\_BACKGROUND\_LOCATION,                                         |
|                                 | GET\_ACCOUNTS, ACCESS\_NETWORK\_STATE                                                                             |
| **Supported NDK Architectures** | x86, x86_64, arm64-v8a, armeabi-v7a                                                                               |

> __NOTE__ Android 12 introduced the BLUETOOTH_CONNECT permission, protected at the dangerous level. Refer to the [official Android documentation](https://developer.android.com/about/versions/12/features/bluetooth-permissions) on how to include it.

> __NOTE__ Regarding Android 11 background location changes: The Device Risk SDK neither requires nor requests location when the application is in a background state.

> __NOTE__ If the permissions listed are not required by the application, the values collected using those permissions will be ignored. The permissions are not required to obtain a usable blackbox, but they do help obtain some unique device information.

> __NOTE__ Android 10 introduced the ACCESS_BACKGROUND_LOCATION permission, protected at the dangerous level as is the case for ACCESS_FINE_LOCATION. Refer to the official Android documentation for when to incorporate this permission.

Version 4.3.2 of the TruValidate Device Risk SDK for Android supports Android 5.0 or higher.

## Installing the Device Risk SDK for Android

1.  Download iovation-android-sdk-4.3.2.zip from here: [iovation Mobile SDK for Android](https://github.com/iovation/deviceprint-SDK-Android). 

2.  Unzip iovation-android-sdk-4.3.2.zip.

3.  Depending on your IDE, do one of the following:

	- In __Maven__, deploy the AAR file to your local Maven repository, using maven-deploy. For more information, see [Guide to installing 3rd party JARs](http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html).

	- If you are using __Gradle__, add the *fraudforce-lib-release-4.3.2.aar* file to your application module's libs directory. Then, edit the *build.gradle* file in order to add the libs directory as a flat-file repository to the `buildscript` and `repository` sections. This makes the fraudforce-lib-release-4.3.2.aar file accessible to Gradle.

		```
        buildscript {
            repositories {
                flatDir {
                    dirs 'libs'
                }
            }
        }

        repositories {
            flatDir {
                dirs 'libs'
            }
        }
		```
		Also in the application module's `build.gradle` file, make sure that fraudforce-lib-release-4.3.2 is included as a dependency:
	
		```
        dependencies {
            ...
            implementation(name:'fraudforce-lib-release-4.3.2', ext:'aar')
        }
		```

        Alternatively, you can include the dependency without exposing your libs folder as a repository by declaring it in the module's `build.gradle` file as follows:

        ```
        dependencies {
            ...
            implementation('libs/fraudforce-lib-release-4.3.2.aar')
        }
		```
		
		Save the `build.gradle` file.
	
## Integrating into Native Apps

> __NOTE__ If you are using an older version of the Android Gradle Plugin and encounter UnsatisfiedLinkErrors in the course of your testing, you may need to add the following to your ProGuard configuration:
    ```
    -keep public class com.iovation.mobile.android.details.RP {
        public native java.lang.String a();
        public native java.lang.String b();
    }
    ```

To integrate into native apps:

1. In your Application class, import the `FraudForceManager` and `FraudForceConfiguration` objects.

    ```
    import com.iovation.mobile.android.FraudForceConfiguration;
    import com.iovation.mobile.android.FraudForceManager;
    ```

2. Create a configuration object with your subscriber key, and enable or disable network calls to TransUnion servers. Entering the subscriber key is strongly recommended for all integrations, and it is required for network connections.

    > __NOTE__ Please review the Network Calls section of this document before enabling network calls.
    
    > __IMPORTANT__ Please contact your TransUnion customer success team representative to receive your subscriber key.

    ```
    FraudForceConfiguration configuration = new FraudForceConfiguration.Builder()
        .subscriberKey([YOUR-SUBSCRIBER-KEY-HERE])
        .enableNetworkCalls(true) // Defaults to false if left out of configuration
        .build();
    ```

3. Initialize the FraudForceManager class using the generated FraudForceConfiguration object, and the application context.

    ```
    FraudForceManager fraudForceManager = FraudForceManager.getInstance();
    fraudForceManager.initialize(configuration, context);
    ```

4. Call the `refresh()` method in the same Activity/Fragment/ViewModel where `getBlackbox()` will be called. The integrating application only needs to call this method on the Fragments where the `getBlackbox()` method will be called.

    > __NOTE__: This method calls updates the geolocation and network information, if enabled.

    > __NOTE__: As with initialization, pass the application context when refreshing.

    ```
    FraudForceManager.getInstance().refresh(context);
    ```

4. To generate the blackbox, call the getBlackbox(Context context) function on an instance of FraudForceManager. This method is a **blocking** call so it is **recommended** to call it on a background thread/coroutine.

    ```
    String blackbox = FraudForceManager.getInstance().getBlackbox(context);
    ```

## Integrating into Hybrid Apps

### Hybrid App Workflow Overview

Integrate into hybrid apps by implementing the following workflow for collecting and sending blackboxes:

1.  An HTML page loads in a WebView.

2.  The user submits a transaction on the HTML page by submitting a form or completing another action. 

3.  This calls the `inject_bb` function, which creates a hidden iframe that calls the `iov://` URL. The iframe then deletes itself.

4.  The `shouldOverrideUrlLoading` function inside of the WebView object in Java is called. This function detects the `iov://blackbox/fill#dom_id` URL.The `dom_id` is the ID of the object on the HTML page where the blackbox will be written, such as a hidden form field.

5.  The `shouldOverrideUrlLoading` function runs JavaScript that automatically injects the blackbox into that object.

### Implementing Hybrid App Support

1. In your Application class, import the `FraudForceManager` and `FraudForceConfiguration` objects.

    ```
    import com.iovation.mobile.android.FraudForceConfiguration;
    import com.iovation.mobile.android.FraudForceManager;
    ```

2. Create a configuration object with your subscriber key, and enable or disable network calls to TransUnion servers. Entering the subscriber key is strongly recommended for all integrations, and it is required for network connections.

    > __NOTE__ Please review the Network Calls section of this document before enabling network calls.

    ```
    FraudForceConfiguration configuration = new FraudForceConfiguration.Builder()
        .subscriberKey([YOUR-SUBSCRIBER-KEY-HERE])
        .enableNetworkCalls(true) // Defaults to false if left out of configuration
        .build();
    ```

3. Initialize the FraudForceManager class using the generated FraudForceConfiguration object, and the application context.

    ```
    FraudForceManager fraudForceManager = FraudForceManager.getInstance();
    fraudForceManager.initialize(configuration, context);
    ```

4.  In your WebView Activity's `onCreate()` function, set your WebView's `shouldOverrideUrlLoading()` function, as well as the `onPageStarted()` function.

	```
	wv.setWebViewClient(new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            FraudForceManager.getInstance().refresh(getContext());
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String[] ref = url.split("#");
            if (url.startsWith("iov://") && ref.length > 1 && ref[1] != null) {
            String injectedJavascript="javascript:(function() { " +
                "document.getElementById('" + ref[1] + "').value = '"
                + FraudForceManager.getInstance().getBlackbox(wv.getContext())
                + "';})()";
                wv.loadUrl(injectedJavascript);
                return true;
            }
            return false;
        }
    });
    ```

5.  On your HTML page, include a javascript function called `inject_bb` that injects an iframe with a call to the `iov://` URL.

		function inject_bb(id) {  
			var iframe = document.createElement('IFRAME');  
			iframe.setAttribute('src', 'iov://blackbox/fill#' + id);  
			iframe.name="ioOut";  
			document.documentElement.appendChild(iframe);  
			iframe.parentNode.removeChild(iframe);  
			iframe = null;  
		}

6.  You must inject the blackbox into a DOM object for collection. To do this, call the `inject_bb` function with the ID of the DOM object, which will automatically call the `shouldOverrideUrlLoading()` function. For example, set `ID` to a hidden form field where the blackbox will be stored. When the form containing the field is submitted, the blackbox is returned to your server back-end, and can then be sent to TransUnion to evaluate along with the transaction.

## Network Calls

The SDK includes the ability to make a network call to TransUnion TruValidate's service. This enables additional functionality in the Device Risk SDK, including:

 - Collect additional network information
 - Configuration updates to root detection
 - Collect information on potential high-risk applications on the device

 By default this functionality is disabled and will need to be enabled in the configuration object. Usage of this feature requires a subscriber key be provided. Please contact your TransUnion client representative to acquire a subscriber key.

## Compiling The Sample App in Android Studio

1 In Android Studio, select File | Open or click **Open Existing Android Studio Project** from the quick-start screen.

2. From the directory where you unzipped fraudforce-lib-release-4.3.2.zip or cloned the repo, open the **android-studio-sample-app** directory.

3. In the project navigation view, open `src/main/java/com/iovation/mobile/android/sample/MainActivity.java`

4. Right-click the file editing view and select _Run Main Activity_.

    - **IMPORTANT!** If the option to run the module does not appear, select FILE -> PROJECT STRUCTURE and open the Modules panel. From there, set the Module SDK drop-down to your target Android SDK version.

	Alternatively, you can right-click on the build.gradle file, and select **Run 'build'**.

5. Select either an attached physical device, or an Android virtual device to run the app on. The app should now compile and launch.

6. When the app compiles successfully, you will see a view with a button that allows you to display a blackbox.

## Changelog

### 4.3.2
- Adjusted root detection.

### 4.3.1
- Update target and compilation SDK versions to 31.
- Adjusted collection details.
- Compatible with the new bluetooth changes/permissions in Android 12. 
- Fixed crashes on devices running below SDK version 24.

### 4.3.0

- Minimum supported Android version updated, from 16 to 21.
- Update target and compilation SDK versions to 30.
- Bug fixes for NPEs sometimes encountered during asynchronous calls to initialize and/or refresh.

### 4.2.0

- Several obfuscation-related updates/fixes, including preservation of base package.

- Update target SDK to 29

### 4.1.1
- Updated compileSdkVersion to 29.

- Behavioral changes for apps targeting API 29.

### 4.1.0

- Adjusted recognition details.

### 4.0.0

- Enhanced support for Android 9.0 Pie.

- Further improvements to location data collection process.

- Resolved Google Play alerts regarding encryption methods.

### 3.1.0

- Support for Android 9.0 Pie.

- Fix for issue related to the Turkish character set.

- Improved location data collection process.

### 3.0.1

- Improved blackbox data collection performance.

- More efficient network calls to iovation.

### 3.0.0

- New API objects, `FraudForceConfiguration` and `FraudForceManager`. Prior API objects have been removed.

- Compatibility with Android 8.0.

- Introduced network calls back to iovation service for additional functionality.

- Dropped support for Android API level 8 through API level 15.

### 2.3.3

- Compatibility with Android 7.0.

### 2.3.2

- Improved error handling.

### 2.3.1

- Improved permission checking.

- Fixes crash with invalid locale.

- Fixes error with Bluetooth permission.

- More robust error handling.

### 2.3.0

- Compatibility with Android 6.0 permission system.

- Enhanced recognition with additional details added in Android 6.0.

- Enhanced geolocation services with mock location detection capabilities.

- Enhanced carrier detection and home carrier detection.

### 2.2.0

- Compatibility with older versions of Android build-tools.

### 2.1.0

- Enhanced recognition with the collection of additional details.

### 2.0.1

- Fix bug with permissions that would crash applications.

### 2.0.0

- New API method, `getBlackbox`, handles low-priority asynchronous collection of device data. The `ioBegin` method remains for backwards compatibility.

- Expanded the android-studio-sample-app with a WebView integration example.

- Added WebView integration instructions.
