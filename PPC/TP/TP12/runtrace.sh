#!/bin/sh

# 2022 T. Bourke & B. Pesin

if [ $# -lt 2 ]; then
    1>&2 printf 'usage: %s <executable> <trace-file> [num outputs]\n' "$0"
    exit 0
fi

EXE=$1
TRACE=$2
NUMOUT=${3:-1}

ramasser() {

    COUNTER=0
    while IFS= read -r line; do

	if [ $COUNTER -gt 0 ]; then
	    printf '\t%s' "$line"
	else
	    printf '%s' "$line"
	fi

	COUNTER=$(( ($COUNTER + 1) % $NUMOUT ))

	if [ $COUNTER -eq 0 ]; then
	    printf '\n'
	fi
    done

    if [ $(( $COUNTER % $NUMOUT )) -ne 0 ]; then
	printf '\n'
    fi

}

"./$EXE" <"$TRACE" | ramasser | pr -m -t "$TRACE" -

