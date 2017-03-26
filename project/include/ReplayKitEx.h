#ifndef REPLAYKITEX
#define REPLAYKITEX

namespace replaykitex {
    
    void replaykitInit(bool _askPreview);
    bool replayKitRecordingAvailable();
    void replayKitStartRecording();
    void replayKitStopRecording();
    bool replayKitIsRecording();
}

#endif
