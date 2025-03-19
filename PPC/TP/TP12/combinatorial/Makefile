HEPTC=heptc
HEPTS=hepts
LESAR=lesar
PERVASIVES:="$(shell $(HEPTC) -where)/c"

TRACE?=1

# make clean
# make runtrace_max
# make runtrace_abs
# make runtrace_saturate

EXES=combinatorial_max combinatorial_abs combinatorial_saturate
all: $(EXES)

combinatorial_%: combinatorial_c/combinatorial_types.c \
	   combinatorial_c/combinatorial.c \
	   combinatorial_c/%_main.c
	$(CC) -o $@ -I combinatorial_c -I $(PERVASIVES) -Wno-parentheses-equality $^

combinatorial_c/combinatorial_types.c combinatorial_c/combinatorial.c: combinatorial.lus
	heptc -hepts -target c $<

combinatorial_c/%_main.c: combinatorial.lus
	heptc -s $* -hepts -target c $<
	mv combinatorial_c/_main.c $@

runtrace_%: combinatorial_%
	../runtrace.sh $^ trace_$*-$(TRACE).txt 1

# simulate: combinatorial.epci combinatorial
# 	$(HEPTS) -sig combinatorial.epci -node $(MAIN) -exec ./combinatorial

clean:
	-@rm -rf combinatorial_c Combinatorial.obc combinatorial_*
	-@rm -f $(EXES) *.epci *.log *.mls
