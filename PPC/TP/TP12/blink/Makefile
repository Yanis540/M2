HEPTC=heptc
HEPTLIB=$(shell heptc -where)
CC=gcc
MAIN?=main
NUMSAMPLES?=25

.PHONY: run

run: blink.exe
	./$< | head -n $(NUMSAMPLES)

blink.exe: blink_c/_main.c
	$(CC) -Wno-parentheses-equality \
	    -o $@ -I $(HEPTLIB)/c -I . blink_c/blink_types.c \
	    blink_c/blink.c blink_c/_main.c

blink_c/_main.c: blink.lus
	$(HEPTC) -target c -s $(MAIN) $<

clean:
	-@rm -rf blink_c
	-@rm -f blink.mls blink.epci blink.obc
	-@rm -f blink.exe blink.log blink.out
