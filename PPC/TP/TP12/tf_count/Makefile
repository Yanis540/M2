HEPTC=heptc
HEPTLIB=$(shell heptc -where)
CC=gcc
MAIN?=tf_count
TRACE?=1

TEST_FILE=trace_tf_count-$(TRACE).txt
NUMSAMPLES?=$(shell wc -l <$(TEST_FILE))



.PHONY: run

run: tf_count.exe
	@cat trace.txt | ./$< | head -n $(NUMSAMPLES) \
		| pr -w 40 -m -t $(TEST_FILE) -

tf_count.exe: tf_count_c/_main.c
	$(CC) -Wno-parentheses-equality \
	    -o $@ -I $(HEPTLIB)/c -I . tf_count_c/tf_count_types.c \
	    tf_count_c/tf_count.c tf_count_c/_main.c

tf_count_c/_main.c: tf_count.lus
	$(HEPTC) -target c -s $(MAIN) $<

clean:
	-@rm -rf tf_count_c
	-@rm -f tf_count.mls tf_count.epci Tf_count.obc
	-@rm -f tf_count.exe tf_count.log tf_count.out

