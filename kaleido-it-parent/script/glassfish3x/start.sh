#!/bin/bash

echo "***************************************************************************"
echo "glassfish base dir: $GLASSFISH_HOME"
echo "***************************************************************************"

$GLASSFISH_HOME/bin/asadmin start-database
$GLASSFISH_HOME/bin/asadmin start-domain kaleido
 
