LUCIOLE=luciole
HEPTC=heptc
HEPTLIB=$(shell heptc -where)
CC=gcc

help:
	@printf 'make adder4.exe      - compilation of adder4.lus\n'
	@printf 'make arrays.exe      - compilation of arrays.exe\n'

# adder4-lustre: adder4.lus
# 	$(LUCIOLE) $< add4

arrays.exe: arrays_c/_main.c
	$(CC) -o $@ -I $(HEPTLIB)/c -Wno-parentheses-equality \
	    arrays_c/arrays_types.c arrays_c/arrays.c arrays_c/_main.c

arrays_c/_main.c: arrays.lus
	$(HEPTC) -target c -s main arrays.lus

adder4.exe: adder4_c/_main.c
	$(CC) -o $@ -I $(HEPTLIB)/c -Wno-parentheses-equality \
	    adder4_c/adder4_types.c adder4_c/adder4.c adder4_c/_main.c

# add -O for optimized arrays
adder4_c/_main.c: adder4.lus
	$(HEPTC) -target c -s add4 adder4.lus

clean:
	-@rm -rf adder4_c arrays_c
	-@rm -f adder4.exe adder4.epci adder4.log adder4.mls
	-@rm -f arrays.exe arrays.epci arrays.log arrays.mls
	-@rm -f add4.ec

