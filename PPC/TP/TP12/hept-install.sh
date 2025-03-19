#!/bin/bash

# Initializing opam and switch
opam init -a
opam switch create heptagon 4.12.0

# Installing heptagon
opam install -y lablgtk heptagon

