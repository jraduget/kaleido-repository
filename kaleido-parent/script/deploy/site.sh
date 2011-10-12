#!/bin/bash

cp -rf ../../target/site/* ../../../kaleido-site/site/
find ../../../kaleido-site/site/  -name '*.html' -exec dos2unix {} \;
