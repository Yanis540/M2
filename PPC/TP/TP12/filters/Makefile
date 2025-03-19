HEPTC=heptc
HEPTLIB=$(shell heptc -where)
GNUPLOT=gnuplot
CC=gcc
MAIN?=system
NUMSAMPLES?=55

plot: fir1.gplot fir1.out
	$(GNUPLOT) --persist $<

fir1.out: fir1.exe
	@echo "# n	y" > $@
	@./$< | head -n $(NUMSAMPLES) \
	      | sed -e 's/=> \(.*\) => \(.*\) => \(.*\)/\1	\2	\3/' \
	    >> $@

fir1.exe: fir1_c/_main.c mathext.o
	$(CC) -Wno-parentheses-equality \
	    -o $@ -I $(HEPTLIB)/c -I . mathext.o fir1_c/fir1_types.c \
	    fir1_c/fir1.c fir1_c/_main.c -lm

fir1_c/_main.c: fir1.lus mathext.epci
	$(HEPTC) -target c -s $(MAIN) $<

mathext.epci: mathext.epi
	$(HEPTC) $<

mathext.o: mathext.c mathext_types.h
	$(CC) -Wall -c -o $@ $<

clean:
	-@rm -rf fir1_c
	-@rm -f mathext.epci mathext.o
	-@rm -f fir1.mls fir1.epci Fir1.obc
	-@rm -f fir1.exe fir1.log fir1.out

