#!/bin/bash

# -> create a site directory in the tags/$version/     and import mvn site content to it

# deprecated
cp -rf ../../target/site/* ../../../kaleido-site/site/
find ../../../kaleido-site/site/  -name '*.html' -exec dos2unix {} \;
