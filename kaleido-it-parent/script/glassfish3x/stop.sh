#!/bin/bash

if [ -z "$GLASSFISH_HOME" ]; then
	if [ -z "$1" ]; then
		echo "***************************************************************************"
		echo "To stop the kaleidoFoundry integration package, please:"
		echo "***************************************************************************"	
		echo "-> define GLASSFISH_HOME variable like : export GLASSFISH_HOME=/opt/glassfishv3.1.1"	
		echo "-> or use: ./stop.sh /opt/glassfishv3.1.1"
		exit 1
	fi	
	
	if [ "$1" ]; then
		GLASSFISH_HOME=$1
	fi
fi

$GLASSFISH_HOME/bin/asadmin stop-domain kaleido
$GLASSFISH_HOME/bin/asadmin stop-database 
