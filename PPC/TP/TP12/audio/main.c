/*
    sudo apt-get install libsndfile1-dev    /   brew install libsndfile
    gcc -Wall -o basic basic.c -lsndfile
 */
#include <stdio.h>
#include <stdlib.h>
#include <sndfile.h>
#include "audio.h"

void show_info(FILE *fout, SF_INFO *info)
{
    fprintf(fout, "frames=%ld channels=%d samplerate=%d\n",
	    (long)info->frames, info->channels, info->samplerate);
    fprintf(fout, "format=%d sections=%d seekable=%d\n",
	    info->format, info->sections, info->seekable);
}

void copy_sf_info(SF_INFO *src, SF_INFO *dst)
{
    dst->frames     = src->frames;
    dst->samplerate = src->samplerate;
    dst->channels   = src->channels;
    dst->format     = src->format;
    dst->sections   = src->sections;
    dst->seekable   = src->seekable;
}

Audio__main_mem mem;

int main(int argc, char** argv)
{
    Audio__main_out _res;
    float ic1, ic2;
    SNDFILE *fin, *fout;
    SF_INFO info_in, info_out;
    float *buf_in, *buf_out;

    if (argc < 3) {
	fprintf(stderr, "usage: %s <input-path> <output-path>\n", argv[0]);
	return -1;
    }

    fin = sf_open(argv[1], SFM_READ, &info_in);
    if (fin == NULL) {
	fprintf(stderr, "could not open input file: %s\n", argv[1]);
	return -2;
    }

    show_info(stdout, &info_in);
    copy_sf_info(&info_in, &info_out);
    info_out.channels = 2;
    // info_out.format = SF_FORMAT_WAV | SF_FORMAT_PCM_32;

    fout = sf_open(argv[2], SFM_WRITE, &info_out);
    if (fin == NULL) {
	fprintf(stderr, "could not open output file: %s\n", argv[1]);
	sf_close(fin);
	return -2;
    }

    buf_in = calloc(info_in.frames, info_in.channels * sizeof(int));
    buf_out = calloc(info_in.frames, info_out.channels * sizeof(int));

    Audio__main_reset(&mem);
    for (sf_count_t i = 0; i < info_in.frames; ++i) {
	sf_readf_float(fin, buf_in, 1);

	ic1 = buf_in[0];
	ic2 = (info_in.channels == 1) ? buf_in[0] : buf_in[1];
	Audio__main_step(ic1, ic2, &_res, &mem);
	buf_out[0] = _res.oc1;
	buf_out[1] = _res.oc2;

	sf_writef_float(fout, buf_out, 1);
    }

    free(buf_in);
    free(buf_out);
    sf_close(fin);
    sf_close(fout);

    return 0;
}

