#!/bin/bash

cp -rf ../../../target/site/apidocs/* ../../../kaleido-site/site/apidocs/
find ../../../../kaleido-site/site/apidocs/  -name '*.html' -exec dos2unix {} \;
