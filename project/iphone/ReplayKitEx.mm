/**
 *
 * Stencyl Extension, Create by Robin Schaafsam
 * wwww.byrobingames.com
 *
 **/

#include <ReplayKitEx.h>
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import <ReplayKit/ReplayKit.h>

#include <inttypes.h>
#import <AVFoundation/AVFoundation.h>
#include <OpenAL/al.h>
#include <OpenAL/alc.h>

using namespace replaykitex;

extern "C" void sendReplayKitEvent(char* event);

static ALCcontext *alcContext = nil;
static BOOL isAmbient = false;


#define SYSTEM_VERSION_EQUAL_TO(v)                  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedSame)

#define SYSTEM_VERSION_GREATER_THAN(v)              ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedDescending)

#define SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedAscending)

#define SYSTEM_VERSION_LESS_THAN(v)                 ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedAscending)

#define SYSTEM_VERSION_LESS_THAN_OR_EQUAL_TO(v)     ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedDescending)

@interface StencylReplayKit : NSObject <RPPreviewViewControllerDelegate, RPScreenRecorderDelegate>
{
    RPScreenRecorder* recorder;
    UIViewController *preview;
    
    BOOL availability;
    BOOL askPreview;
}
@property (nonatomic, assign) BOOL availability;
@property (nonatomic, assign) BOOL askPreview;

- (id)initReplayKit;
- (void)startRecording;
- (void)stopRecording;
- (BOOL)recording;

@end

//----------//----------//----------//----------//----------//----------//

@implementation StencylReplayKit

@synthesize availability;
@synthesize askPreview;

- (id)initReplayKit{
    self = [super init];
    if ( self != nil ) {
        recorder = [RPScreenRecorder sharedRecorder];
        [recorder setDelegate:self];
        
    }
    return self;
}

// =========================================================================
// MARK: - RPScreenRecorderDelegate
// called after stopping the recording
- (void)screenRecorder:(RPScreenRecorder *)screenRecorder didStopRecordingWithError:(NSError *)error previewViewController:(RPPreviewViewController *)previewViewController {
    NSLog(@"Stop recording");
    if (error != nil)
    {
        NSString *screenRecordingError = [error description];
        NSLog(@"Stop recording error: %@", screenRecordingError);
    }
    
    //[screenRecorder discardRecordingWithHandler:nil];
}

// called when the recorder availability has changed
- (void)screenRecorderDidChangeAvailability:(RPScreenRecorder *)screenRecorder {
    availability = [screenRecorder isAvailable];
    NSString * availabilityString = (availability) ? @"true" : @"false";
    NSLog(@"Availablility: %@",availabilityString);
}

// =========================================================================
// MARK: - RPPreviewViewControllerDelegate

// called when preview is finished
- (void)previewControllerDidFinish:(RPPreviewViewController*)previewController {
    NSLog(@"Preview finish");

    [previewController dismissViewControllerAnimated:YES completion:^(void)
     {
         [recorder discardRecordingWithHandler:^(void)
          {
              __block RPPreviewViewController* previewController = NULL;
          }];
         if(previewController != NULL){
              __block RPPreviewViewController* previewController = NULL;
         }
         
         UIWindow *window = [UIApplication sharedApplication].keyWindow;
         [preview.view removeFromSuperview];
         [window makeKeyAndVisible];
         
         sendReplayKitEvent("previewdidclosed");
         
         
        // NSLog(@"Interruption ended!");
         //AVAudioSession *session = [AVAudioSession sharedInstance];
         //[session setActive: YES error: nil];
         
         // The ambient property gets lots when the session is deactivated, so that needs to be set again
        // if (isAmbient) {
         //    UInt32 category = kAudioSessionCategory_AmbientSound;
          //   AudioSessionSetProperty ( kAudioSessionProperty_AudioCategory, sizeof (category), &category );
         //}
         
         // Reactivate the current audio session
         //AudioSessionSetActive(YES);
         
         // Restore OpenAL context
         //alcMakeContextCurrent(alcContext);
         
         // finally, 'unpause' the context
         //alcProcessContext(alcContext);
         
     }];
}
// =========================================================================

- (void)startRecording {
    
    // start recording when running iOS 9 and higher and running under 10
    if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"9.0") && SYSTEM_VERSION_LESS_THAN(@"10.0")){
         [recorder startRecordingWithMicrophoneEnabled:YES handler:^(NSError* error){ //Deprecated in iOS 10
             if (error != nil)
             {
                 NSString *startRecordingError = [error description];
                 NSLog(@"Failed start recording: %@", startRecordingError);
                 sendReplayKitEvent("iscancelled");
             }
         }];
    }
    
    // start recording when running iOS 10 or higher
    if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"10.0")){
        [recorder startRecordingWithHandler:^(NSError* error){ // Function not available in iOS 9
            if (error != nil)
            {
                NSString *startRecordingError = [error description];
                NSLog(@"Failed start recording: %@", startRecordingError);
                sendReplayKitEvent("iscancelled");
            }
        }];
        
    }
    
    
    NSLog(@"Start recording");
}

- (void)stopRecording {
    
    // end recording
    [recorder stopRecordingWithHandler:^(RPPreviewViewController* previewViewController, NSError* error){
        if (error != nil)
        {
            NSString *stopRecordingError = [error description];
            NSLog(@"Failed stop recording: %@", stopRecordingError);
        }
        
        if (askPreview){
            
            UIAlertController * alert=   [UIAlertController
                                          alertControllerWithTitle:@"Recording"
                                          message:@"Do you wish to discard or view your gameplay recording?"
                                          preferredStyle:UIAlertControllerStyleAlert];
            
            UIAlertAction* discard = [UIAlertAction
                                 actionWithTitle:@"Discard"
                                 style:UIAlertActionStyleDefault
                                 handler:^(UIAlertAction * action)
                                 {
                                     [alert dismissViewControllerAnimated:YES completion:nil];
                                     [recorder discardRecordingWithHandler:^(void)
                                      {
                                          __block RPPreviewViewController* previewViewController = NULL;
                                      }];
                                     
                                     if(previewViewController != NULL){
                                         __block RPPreviewViewController* previewViewController = NULL;
                                     }
                                 }];
            
            UIAlertAction* view = [UIAlertAction
                                     actionWithTitle:@"View"
                                     style:UIAlertActionStyleDefault
                                     handler:^(UIAlertAction * action)
                                     {
                                         [alert dismissViewControllerAnimated:YES completion:nil];
                                         
                                         [previewViewController setPreviewControllerDelegate:self];
                                         [previewViewController setModalPresentationStyle:UIModalPresentationFullScreen];
                                         
                                         UIWindow* window = [UIApplication sharedApplication].keyWindow;
                                         preview = [[UIViewController alloc] init];
                                         [window addSubview: preview.view];
                                         [preview presentViewController:previewViewController animated:YES completion:^(void)
                                          {
                                              sendReplayKitEvent("previewdidopened");
                                              [[UIApplication sharedApplication] setStatusBarHidden:YES];
                                              
                                              //needed for gameaudio else game audio will stop after dismiss previewcontroller
                                              AVAudioSession *session = [AVAudioSession sharedInstance];
                                              
                                              NSError *setCategoryError = nil;
                                              
                                              [session setCategory:AVAudioSessionCategoryAmbient
                                                            withOptions:AVAudioSessionCategoryOptionMixWithOthers
                                                             error:&setCategoryError];
                                              
                                          }];
                                         
                                     }];
            
            [alert addAction:discard];
            [alert addAction:view];
            
            [[[[UIApplication sharedApplication] keyWindow] rootViewController] presentViewController:alert animated:YES completion:nil];
            
        }else{
            [previewViewController setPreviewControllerDelegate:self];
            
            [previewViewController setModalPresentationStyle:UIModalPresentationFullScreen];
            
            UIWindow* window = [UIApplication sharedApplication].keyWindow;
            preview = [[UIViewController alloc] init];
            [window addSubview: preview.view];
            [preview presentViewController:previewViewController animated:YES completion:^(void)
             {
                 sendReplayKitEvent("previewdidopened");
                 [[UIApplication sharedApplication] setStatusBarHidden:YES];
                 
                 
                 AVAudioSession *session = [AVAudioSession sharedInstance];
                 
                 NSError *setCategoryError = nil;
                 
                 //needed for gameaudio else game audio will stop after dismiss previewcontroller
                 [session setCategory:AVAudioSessionCategoryAmbient
                          withOptions:AVAudioSessionCategoryOptionMixWithOthers
                                error:&setCategoryError];
             }];
        }
        
    }];
    
    NSLog(@"Stop recording");
}

- (BOOL)recording
{
    NSLog(@"isrecording");
    if (recorder == nil)
    {
        NSString *isRecordingError = [NSString stringWithUTF8String:"Failed to get Screen Recorder"];
        NSLog(@"Failed to get recorder: %@", isRecordingError);
        return NO;
    }
    return [recorder isRecording];
}

@end

//----------//----------//----------//----------//----------//----------//

namespace replaykitex

{
    static StencylReplayKit* _replayKit;
    
    void replaykitInit(bool _askPreview)
    {
        if (SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"9.0")) {
            
            if (_replayKit == nil){
                
                _replayKit = [[StencylReplayKit alloc] init];
            }
            
            if(_askPreview){
                _replayKit.askPreview = YES;
            }else{
                _replayKit.askPreview = NO;
            }
            
            [_replayKit initReplayKit];
        }else{
            NSLog(@"Running on iOS8 or lower, ReplayKit not available");
        }
    }
    
    bool replayKitRecordingAvailable()
    {
        if (_replayKit == nil)
        {
            return false;
        }
        
        return _replayKit.availability == YES;
    }
    
    
    void replayKitStartRecording()
    {
        if (_replayKit == nil){
            
            _replayKit = [[StencylReplayKit alloc] init];
            [_replayKit initReplayKit];
        }
        
       [_replayKit startRecording];
    }
    
    void replayKitStopRecording()
    {
        if (_replayKit != nil)
        {
            [_replayKit stopRecording];
        }
    }
    
    bool replayKitIsRecording()
    {
        if (_replayKit == nil)
        {
            return false;
        }
        
        return [_replayKit recording] == YES;
    }
    
}
