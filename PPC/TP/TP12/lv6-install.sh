#!/bin/bash

# Downloading and unpacking lustre-v6
wget http://www-verimag.imag.fr/DIST-TOOLS/SYNCHRONE/lustre-v6/pre-compiled/`arch`-`uname`-lv6-bin-dist.tgz
tar xvfz `arch`-`uname`-lv6-bin-dist.tgz
rm `arch`-`uname`-lv6-bin-dist.tgz

# Customizing the settings script
NEWTEXT=`echo "LV6_PATH=$(pwd)"; cat v6-tools.sh`
echo "$NEWTEXT" > v6-tools.sh
chmod +x v6-tools.sh

echo "type 'source ./v6-tools.sh' to add binaries to your PATH" 
