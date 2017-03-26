package;

#if cpp
import cpp.Lib;
#elseif neko
import neko.Lib;
#else
import openfl.Lib;
#end

class ReplayKit
{	
	private static var initialized:Bool=false;
	private static var _isCancelled:Bool=false;
	private static var _previewDidOpened:Bool=false;
	private static var _previewDidClosed:Bool=false;
	
	#if ios
	private static var __initReplayKit:Bool->Void = function(askPreview:Bool){};
	private static var __replaykit_set_event_handle = Lib.load("replaykit","replaykit_set_event_handle", 1);
	#end
	#if android
	private static var __initReplayKit:Dynamic;
	#end
	private static var __recordingAvailable:Void->Bool = function(){ return false; };
	private static var __startRecording:Void->Void = function(){};
	private static var __isRecording:Void->Bool = function(){ return false; };
	private static var __stopRecording:Void->Void = function(){};
	
	public static function init(_askPreview:Bool)
	{
		if(!initialized){
		initialized = true;
		#if ios
		try{
			// CPP METHOD LINKING
			__initReplayKit = cpp.Lib.load("replaykit","init_replaykit",1);
			__recordingAvailable = cpp.Lib.load("replaykit","recording_available",0);
			__startRecording = cpp.Lib.load("replaykit", "start_recording", 0);
			__stopRecording = cpp.Lib.load ("replaykit", "stop_recording", 0);
			__isRecording = cpp.Lib.load ("replaykit", "is_recording", 0);
			
			__initReplayKit(_askPreview);
			__replaykit_set_event_handle(notifyListeners);
		}catch(e:Dynamic){
			trace("iOS INIT Exception: "+e);
		}
		#end
		#if android
		try{
			// JNI METHOD LINKING
			__initReplayKit = openfl.utils.JNI.createStaticMethod("com/byrobin/screencapture/ScreenCaptureEX", "initScreenCapture", "(Lorg/haxe/lime/HaxeObject;Z)V");
			__recordingAvailable = openfl.utils.JNI.createStaticMethod("com/byrobin/screencapture/ScreenCaptureEX", "isAvailable", "()Z" ,true);
			__startRecording = openfl.utils.JNI.createStaticMethod("com/byrobin/screencapture/ScreenCaptureEX", "startScreenCapture", "()V");
			__stopRecording = openfl.utils.JNI.createStaticMethod("com/byrobin/screencapture/ScreenCaptureEX", "stopScreenCapture", "()V");
			__isRecording = openfl.utils.JNI.createStaticMethod("com/byrobin/screencapture/ScreenCaptureEX", "isRecording", "()Z" ,true);
		
			__initReplayKit(new ReplayKit(), _askPreview);
		}catch(e:Dynamic){
			trace("Android INIT Exception: "+e);
		}
		#end
		}
	}
	
	public static function isRecordingAvailable():Bool
	{	
		return __recordingAvailable();
	}
	
	public static function startRecording()
	{
		__startRecording();
	}
	
	public static function stopRecording()
	{
		
		__stopRecording();
	}
	
	public static function isRecording():Bool
	{
		
		return __isRecording();
	}
	
	public static function isCancelled():Bool{
		
		if(_isCancelled){
			_isCancelled = false;
			return true;
		}
		
		return false;
	}
	
	public static function previewDidOpened():Bool{
		
		if(_previewDidOpened){
			_previewDidOpened = false;
			return true;
		}
		
		return false;
	}
	
	public static function previewDidClosed():Bool{
		
		if(_previewDidClosed){
			_previewDidClosed = false;
			return true;
		}
		
		return false;
	}
	
	///////Events Callbacks/////////////
	
	#if ios
	//Ads Events only happen on iOS.
	private static function notifyListeners(inEvent:Dynamic)
	{
		var event:String = Std.string(Reflect.field(inEvent, "type"));
		
		if(event == "iscancelled")
		{
			_isCancelled = true;
		}
		if(event == "previewdidopened")
		{
			_previewDidOpened = true;
		}
		if(event == "previewdidclosed")
		{
			_previewDidClosed = true;
		}
	}
	#end
	
	#if android
	private function new() {}
	
	public function onIsCancelled() 
	{
		_isCancelled = true;
	}
	
	public function onPreviewDidOpened() 
	{
		_previewDidOpened = true;
	}
	
	public function onPreviewDidClosed() 
	{
		_previewDidClosed = true;
	}
	#end


}