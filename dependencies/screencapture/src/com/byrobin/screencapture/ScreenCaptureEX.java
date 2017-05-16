/*
 *
 * Created by Robin Schaafsma
 * www.byrobingames.com
 * copyright
 */

package com.byrobin.screencapture;

import android.app.Activity;
import android.app.*;
import android.content.*;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;


import org.haxe.extension.Extension;
import org.haxe.lime.HaxeObject;

///permisions
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.Manifest;


public class ScreenCaptureEX extends Extension {


	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
    private static ScreenCaptureEX _self = null;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String TAG = "ScreenCaptureEx";
    
    private static final String STATE_RESULT_CODE = "result_code";
    private static final String STATE_RESULT_DATA = "result_data";
    
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    
    private int mScreenDensity;
    
    private int mResultCode;
    private Intent mResultData;
    
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mMediaProjectionManager;
    
    private MediaRecorder mMediaRecorder;
    
    private int DISPLAY_WIDTH;
    private int DISPLAY_HEIGHT;
    
    public static boolean _isAvailable = false;
    public static boolean _isRecording = false;
    public static boolean _askPreview = false;
    public static boolean _isCancelled = false;
    
    protected static HaxeObject screenCaptureCallback;

	//////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int REQUEST_ALL = 100;
    private static String[] PERMISSIONS_ALL = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};
    
    private static final int REQUEST_RECORD_AUDIO = 101;
    private static String[] PERMISSIONS_RECORD_AUDIO = {Manifest.permission.RECORD_AUDIO};
    
    private static final int REQUEST_WRITE_EXTERNAL = 102;
    private static String[] PERMISSIONS_WRITE_EXTERNAL = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    
	//////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        _self = this;
        
        if (savedInstanceState != null) {
            if(Build.VERSION.SDK_INT >= 21) {
                mResultCode = savedInstanceState.getInt(STATE_RESULT_CODE);
                mResultData = savedInstanceState.getParcelable(STATE_RESULT_DATA);
            }
        }
    }
    
    public static void initScreenCapture(HaxeObject cb, boolean askPreview) {
        
        screenCaptureCallback = cb;
        _askPreview = askPreview;
        
        Extension.mainActivity.runOnUiThread
        (
         new Runnable()
         {
            public void run()
            {
                if(Build.VERSION.SDK_INT >= 21) {
                    _isAvailable = true;
                    DisplayMetrics metrics = new DisplayMetrics();
                    Extension.mainActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    _self.mScreenDensity = metrics.densityDpi;
                    _self.DISPLAY_WIDTH = metrics.widthPixels;
                    _self.DISPLAY_HEIGHT = metrics.heightPixels;
                    _self.mMediaProjectionManager = (MediaProjectionManager)
                    Extension.mainActivity.getSystemService(Extension.mainContext.MEDIA_PROJECTION_SERVICE);
                }else{
                    _isAvailable = false;
                }
			}
		 });
    }
    
    //@Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        if (mResultData != null) {
            outState.putInt(STATE_RESULT_CODE, mResultCode);
            outState.putParcelable(STATE_RESULT_DATA, mResultData);
        }
    }
    
    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Log.i(TAG, "User cancelled");
                //Toast.makeText(mainActivity, "Screen Capture denied.", Toast.LENGTH_SHORT).show();
                screenCaptureCallback.call("onIsCancelled", new Object[] {});
                //return;
            }else{
				Log.i(TAG, "Starting screen capture");
            	initRecorder();
            	mResultCode = resultCode;
            	mResultData = data;
            	setUpMediaProjection();
            	setUpVirtualDisplay();
            	_isRecording = true;
			}
        }
        return super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (mMediaRecorder != null) {
            stopScreenCapture();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        tearDownMediaProjection();
    }
    
    private void setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
    }
    
    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }
    
    public static void startScreenCapture() {
        
        if(!hasPermissions(PERMISSIONS_WRITE_EXTERNAL)){
            ActivityCompat.requestPermissions(mainActivity, PERMISSIONS_ALL, REQUEST_ALL);
            return;
        }
            
        Extension.mainActivity.runOnUiThread
            (
             new Runnable()
             {
                public void run()
                {
                    if(Build.VERSION.SDK_INT >= 21) {
        
                        if (_self.mMediaProjection != null) {
                            _self.initRecorder();
                            _self.setUpVirtualDisplay();
                            _isRecording = true;
                        } else if (_self.mResultCode != 0 && _self.mResultData != null) {
                            _self.initRecorder();
                            _self.setUpMediaProjection();
                            _self.setUpVirtualDisplay();
                            _isRecording = true;
                        } else {
                            Log.i(TAG, "Requesting confirmation");
                            // This initiates a prompt dialog for the user to confirm screen projection.
                            Extension.mainActivity.startActivityForResult(
                                   _self.mMediaProjectionManager.createScreenCaptureIntent(),
                                   REQUEST_MEDIA_PROJECTION);
                        }
                    }
                }
            });
    }
    
    private void setUpVirtualDisplay() {
        
        Log.i(TAG, "setUpVirtualDisplay");
        
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                                                                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                                                                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                                                                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                                                                /*Handler*/);
        
        mMediaRecorder.start();
        Log.i(TAG, "mMediaRecorder.start");
    }
    
    public static void stopScreenCapture() {
        if (_self.mVirtualDisplay == null) {
            return;
        }
            
        Extension.mainActivity.runOnUiThread
        (
            new Runnable()
            {
                public void run()
                {
                    if(Build.VERSION.SDK_INT >= 21) {
                        _isRecording = false;
                        _self.mVirtualDisplay.release();
                        _self.mVirtualDisplay = null;
        
                        _self.mMediaRecorder.stop();
                        _self.mMediaRecorder.reset();
                        _self.mMediaRecorder.release();
                        _self.mMediaRecorder = null;
                        _self.tearDownMediaProjection();
                        
                        if(_askPreview){
                            Dialog dialog = new AlertDialog.Builder(mainActivity).setTitle("Recording").setMessage("Do you wish to discard of view your gameplay recording?").setPositiveButton
                            (
                             "View",
                             new DialogInterface.OnClickListener()
                             {
                                public void onClick(DialogInterface dialog, int whichButton)
                                {
                                    _self.openPreview();
                                }
                            }
                             ).setNegativeButton
                            (
                             "Discard",
                             new DialogInterface.OnClickListener()
                             {
                                public void onClick(DialogInterface dialog, int whichButton)
                                {
                                    //Do nothing go back to mainActivity
                                }
                            }
                             ).create();
                            
                            dialog.show();
                            
                        }else{
                            _self.openPreview();
                        }
                        
                    }
                }
            });
    }
    
    private void initRecorder() {
        
            
        try {
            Log.i(TAG, "start initRecorder");
            String appName = "::APP_TITLE::";
            String appNameFinal = appName.replace(' ','_');
            mMediaRecorder = new MediaRecorder();
            
            if(hasPermissions(PERMISSIONS_RECORD_AUDIO)){
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            }
            
            //mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setOutputFile(Environment
                                            .getExternalStoragePublicDirectory(Environment
                                                                            .DIRECTORY_MOVIES) + "/" + appNameFinal + ".mp4");
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            if(hasPermissions(PERMISSIONS_RECORD_AUDIO)){
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            }
            //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "IOException: " + e);
            }
    }
    
    public void openPreview() {
        
        screenCaptureCallback.call("onPreviewDidOpened", new Object[] {});
        
        Intent intent = new Intent(mainActivity, VideoPreview.class);
        Extension.mainActivity.startActivity(intent);
		Extension.mainActivity.overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }
         
    public static boolean isAvailable(){
            
        return _isAvailable;
    }
         
        
    public static boolean isRecording(){
            
        return _isRecording;
    }
    
    
    //public static boolean isCancelled(){
        
    //    return _isCancelled;
    //}
    
    public static boolean hasPermissions(String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= 23 && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(mainActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    
}
