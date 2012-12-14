#!/bin/bash

if [ -z "$GLASSFISH_HOME" ]; then
	if [ -z "$1" ]; then
		echo "***************************************************************************"
		echo "To start the kaleidoFoundry integration package, please:"
		echo "***************************************************************************"	
		echo "-> define GLASSFISH_HOME variable like : export GLASSFISH_HOME=/opt/glassfishv3.1.1"	
		echo "-> or use: ./start.sh /opt/glassfishv3.1.1"
		exit 1
	fi	
	
	if [ "$1" ]; then
		GLASSFISH_HOME=$1
	fi
fi

$GLASSFISH_HOME/bin/asadmin start-database
$GLASSFISH_HOME/bin/asadmin start-domain kaleido
 
