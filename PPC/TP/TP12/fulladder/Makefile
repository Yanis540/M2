HEPTC=heptc
HEPTS=hepts
LESAR=lesar
PERVASIVES:="$(shell $(HEPTC) -where)/c"

TRACE?=1

EXES=fulladder_full_add fulladder_full_add_h
all: $(EXES)

fulladder_%: fulladder_c/fulladder_types.c \
	   fulladder_c/fulladder.c \
	   fulladder_c/%_main.c
	$(CC) -o $@ -I fulladder_c -I $(PERVASIVES) -Wno-parentheses-equality $^

fulladder_c/fulladder_types.c fulladder_c/fulladder.c: fulladder.lus
	heptc -hepts -target c $<

fulladder_c/%_main.c: fulladder.lus
	heptc -s $* -hepts -target c $<
	mv fulladder_c/_main.c $@

runtrace_full_add: fulladder_full_add
	../runtrace.sh $^ trace_fulladder-$(TRACE).txt 2

runtrace_full_add_h: fulladder_full_add_h
	../runtrace.sh $^ trace_fulladder-$(TRACE).txt 2

runtrace_equivalence: fulladder_equivalence
	../runtrace.sh $^ trace_fulladder-$(TRACE).txt 1

# simulate: fulladder.epci fulladder
# 	$(HEPTS) -sig fulladder.epci -node $(MAIN) -exec ./fulladder

check: fulladder.lus
	$(LESAR) $< equivalence -diag

clean:
	-@rm -rf fulladder_c Fulladder.obc fulladder_*
	-@rm -f $(EXES) *.epci *.log *.mls
