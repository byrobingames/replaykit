package com.byrobin.screencapture;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
/**
 * Created by robinschaafsma on 10-04-16.
 */
public class VideoPreview extends Activity {
    
    private static final String TAG = "VideoPreviewActivity";
    
    protected FrameLayout videoPlaceholder;
    protected int layoutResource;
    protected VideoView videoHolder;
    protected MediaController mediaController;
	
	public Uri videoFile;
	public String appName = "::APP_TITLE::";
    
    boolean show = true;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
        layoutResource = R.layout.activity_preview_video_fullscreen;
        
        initVideo();
    }
    
    protected void initVideo()
    {	
        setContentView(layoutResource);
        
        videoPlaceholder = ((FrameLayout)findViewById(R.id.videoPlaceholder));
        
        if(videoHolder == null){
            
            videoHolder = new VideoView(this);
            //setContentView(videoHolder);
            mediaController = new MediaController(this){
                @Override
                public void hide() {
                    // TODO Auto-generated method stub
                    if(show){
                        super.show();
                    }else{
                        super.hide();
                    }
                }
            };
            //videoHolder.setMediaController(new MediaController(getActivity()));
            videoHolder.setMediaController(mediaController);
			
        	String appNameFinal = appName.replace(' ','_');
            videoFile = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment
                                                                                    .DIRECTORY_MOVIES) + "/" + appNameFinal + ".mp4");//Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
            videoHolder.setVideoURI(videoFile);
            videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    //jumpMain(); //jump to the next Activity
                }
            });
            
            videoHolder.start();
            
            videoHolder.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //((VideoView)v).stopPlayback();
                    //jumpMain();
                    
                    return true;
                }
            });
        }
        
        videoPlaceholder.addView(videoHolder);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        
        Log.d(TAG, "onConfigurationChanged (newConfig = " + newConfig.toString() + ")");
        
        if (videoHolder != null)
        {
            // Remove the VideoView from the old placeholder
            videoPlaceholder.removeView(videoHolder);
        }
        
        super.onConfigurationChanged(newConfig);
        
        // Reinitialize the UI
        initVideo();
    }
    
    public void onCancelPressed(View view) {
        show = false;
        videoHolder.stopPlayback();
        mediaController.hide();
        finish();
		overridePendingTransition(R.anim.stay, R.anim.slide_down);
        
        ScreenCaptureEX.screenCaptureCallback.call("onPreviewDidClosed", new Object[] {});
    }
    
    public void onSharePressed(View view) {
        String appNameFinal = appName.replace(' ','_');
		
        File filePatch =  new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/" + appNameFinal + ".mp4");
        Uri fileUri = Uri.fromFile(filePatch);
        String msg = "I'am playing ::APP_TITLE::";
        String url = "https://play.google.com/store/apps/details?id=::APP_PACKAGE::";
        
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/mp4");
        intent.putExtra(Intent.EXTRA_TEXT, msg + "\n\n" + url);
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        startActivity(Intent.createChooser(intent, "Share via.."));
    }
    
    
}
