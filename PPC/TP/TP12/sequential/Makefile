HEPTC=heptc
HEPTS=hepts
CC=gcc
PERVASIVES:="$(shell $(HEPTC) -where)/c"
TRACE?=1

EXES=sequential_always sequential_count_true sequential_count_succ_true sequential_bounds
all: $(EXES)

sequential_%: sequential_c/sequential_types.c \
	   sequential_c/sequential.c \
	   sequential_c/%_main.c
	$(CC) -o $@ -I sequential_c -I $(PERVASIVES) -Wno-parentheses-equality $^

sequential_c/sequential_types.c sequential_c/sequential.c: sequential.lus
	heptc -hepts -target c $<

sequential_c/%_main.c: sequential.lus
	heptc -s $* -hepts -target c $<
	mv sequential_c/_main.c $@

runtrace_always: sequential_always
	../runtrace.sh $^ trace_always-$(TRACE).txt 1

runtrace_count_true: sequential_count_true
	../runtrace.sh $^ trace_count_true-$(TRACE).txt 1

runtrace_count_succ_true: sequential_count_succ_true
	../runtrace.sh $^ trace_count_succ_true-$(TRACE).txt 1

runtrace_bounds: sequential_bounds
	../runtrace.sh $^ trace_bounds-$(TRACE).txt 2

# simulate: sequential.epci sequential
# 	$(HEPTS) -sig sequential.epci -node $(MAIN) -exec ./sequential

clean:
	-@rm -rf sequential_c Sequential.obc
	-@rm -f $(EXES) *.epci *.log *.mls
