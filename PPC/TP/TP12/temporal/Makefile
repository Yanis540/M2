HEPTC=heptc
HEPTLIB=$(shell heptc -where)
CC=gcc
PERVASIVES:="$(shell $(HEPTC) -where)/c"

EXES=temporal_implies temporal_after temporal_always_since temporal_once_since temporal_always_from_to
all: $(EXES)

temporal_%: temporal_c/temporal_types.c \
	   temporal_c/temporal.c \
	   temporal_c/%_main.c
	$(CC) -o $@ -I temporal_c -I $(PERVASIVES) -Wno-parentheses-equality $^

temporal_c/temporal_types.c temporal_c/temporal.c: temporal.lus
	heptc -hepts -target c $<

temporal_c/%_main.c: temporal.lus
	heptc -s $* -hepts -target c $<
	mv temporal_c/_main.c $@

runtrace_%: temporal_%
	../runtrace.sh $^ trace_$*.txt 1

# simulate: temporal.epci temporal
# 	$(HEPTS) -sig temporal.epci -node $(MAIN) -exec ./temporal

clean:
	-@rm -rf temporal_c Temporal.obc
	-@rm -f $(EXES) *.epci *.log *.mls
