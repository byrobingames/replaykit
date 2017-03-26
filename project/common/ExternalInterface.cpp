#ifndef IPHONE
#define IMPLEMENT_API
#endif

#if defined(HX_WINDOWS) || defined(HX_MACOS) || defined(HX_LINUX)
#define NEKO_COMPATIBLE
#endif

#include <hx/CFFI.h>
#include "ReplayKitEx.h"
#include <stdio.h>

//--------------------------------------------------
// Change this to match your extension's ID
//--------------------------------------------------

using namespace replaykitex;

AutoGCRoot* replayKitEventHandle = 0;

#ifdef IPHONE

//--------------------------------------------------
// Glues Haxe to native code.
//--------------------------------------------------

static void replaykit_set_event_handle(value onEvent)
{
    replayKitEventHandle = new AutoGCRoot(onEvent);
}
DEFINE_PRIM(replaykit_set_event_handle, 1);

static value init_replaykit(value askPreview)
{
    replaykitInit(val_bool(askPreview));
    return alloc_null();
}
DEFINE_PRIM(init_replaykit, 1);
/////
static value recording_available()
{
    return alloc_bool(replayKitRecordingAvailable());
}
DEFINE_PRIM(recording_available, 0);
///////
static value start_recording()
{
    replayKitStartRecording();
    return alloc_null();
}
DEFINE_PRIM(start_recording, 0);
//////
static value is_recording()
{
    return alloc_bool(replayKitIsRecording());
}
DEFINE_PRIM(is_recording, 0);
//////
static value stop_recording()
{
    replayKitStopRecording();
    return alloc_null();
}
DEFINE_PRIM(stop_recording, 0);

#endif



//--------------------------------------------------
// IGNORE STUFF BELOW THIS LINE
//--------------------------------------------------

extern "C" void replaykitex_main()
{	
}
DEFINE_ENTRY_POINT(replaykitex_main);

extern "C" int replaykitex_register_prims()
{ 
    return 0; 
}
extern "C" void sendReplayKitEvent(const char* type)
{
    printf("Send Event: %s\n", type);
    value o = alloc_empty_object();
    alloc_field(o,val_id("type"),alloc_string(type));
    val_call1(replayKitEventHandle->get(), o);
}
