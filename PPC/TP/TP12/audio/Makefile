HEPTC=heptc
HEPTLIB=$(shell heptc -where)
CC=gcc

# * Requires libsndfile
#     sudo apt-get install libsndfile1-dev
#     brew install libsndfile
#
#   see: http://www.mega-nerd.com/libsndfile/api.html
#
# * Sound files:
# 	org1.ogg, org2.ogg, org3.ogg, org4.ogg:
# 	https://sound.eti.pg.gda.pl/student/eim/synteza/adamx/eindex.html

audio.exe: audio_c/audio.c main.c
	$(CC) -o $@ -I $(HEPTLIB)/c -I audio_c -Wno-parentheses-equality \
	    audio_c/audio_types.c audio_c/audio.c main.c -lsndfile

audio_c/audio.c: audio.lus main.c
	$(HEPTC) -target c -O audio.lus

clean:
	-@rm -rf audio_c
	-@rm -f audio.exe audio.epci audio.log audio.mls

