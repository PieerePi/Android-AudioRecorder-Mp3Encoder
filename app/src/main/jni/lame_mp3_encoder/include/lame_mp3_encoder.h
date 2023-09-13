#ifndef PHUKET_TOUR_STUDIO_LAME_MP3_ENCODER
#define PHUKET_TOUR_STUDIO_LAME_MP3_ENCODER

#include "lame/lame.h"

class LameMp3Encoder {
private:
    FILE *pcmFile;
    FILE *mp3File;
    lame_t lameClient;
    int inChannels;

public:
    LameMp3Encoder();

    ~LameMp3Encoder();

    int Init(const char *pcmFilePath, const char *mp3FilePath, int sampleRate, int channels,
             int bitRate);

    void Encode();

    void Destroy();
};

#endif //PHUKET_TOUR_STUDIO_LAME_MP3_ENCODER
