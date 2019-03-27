#!/bin/bash

if [ "$1" == "" ]; then
  echo "install libraries: copy, set links and ldconfig"
  echo "usage: install-libs.sh <destdir>"
  exit 1
fi
echo "install libraries to path '$1'"

for word in $(ls -l lib*.so* | grep '^-')
do
  if [ "${word:0:3}" == "lib" ]; then
    libfile=$word
    libminor=${libfile%.[0-9]*}
    libmajor=${libminor%.[0-9]*}
    libname=${libmajor%.[0-9]*}
    echo "$libname ==> $libmajor ==> $libfile"

    cp $libfile $1
    ln -sf $1/$libfile  $1/$libmajor
    ln -sf $1/$libmajor $1/$libname
  fi
done

ldconfig $1

