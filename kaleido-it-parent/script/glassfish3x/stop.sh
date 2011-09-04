#!/bin/bash

echo "***************************************************************************"
echo "glassfish base dir: $GLASSFISH_HOME"
echo "***************************************************************************"

$GLASSFISH_HOME/bin/asadmin stop-domain kaleido
$GLASSFISH_HOME/bin/asadmin stop-database 
