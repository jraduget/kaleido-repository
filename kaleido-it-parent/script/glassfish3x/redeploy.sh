#!/bin/bash

# current build version (TODO extract it from pom.xml)
KALEIDO_VERSION=0.8.2-SNAPSHOT

# script base dir
BASE="$(cd -P -- $(dirname -- "$0"); pwd -P)"

if [ -z "$GLASSFISH_HOME" ]; then
	if [ -z "$1" ]; then
		echo "***************************************************************************"
		echo "To redeploy the kaleidoFoundry integration tests, please:"
		echo "***************************************************************************"	
		echo "-> define GLASSFISH_HOME variable like : export GLASSFISH_HOME=/opt/glassfishv3.1.1"	
		echo "-> or use: ./deploy.sh /opt/glassfishv3.1.1"
		exit 1
	fi	
	
	if [ "$1" ]; then
		GLASSFISH_HOME=$1
	fi
fi

echo "***************************************************************************"
echo "Re deploying kaleido integration tests $KALEIDO_VERSION :"
echo "-> from '$BASE'"
echo "-> to glassfish '$GLASSFISH_HOME' using domain kaleido"
echo "***************************************************************************"

# clean log, start domain if needed, and undeploy current version
cat /dev/null > $GLASSFISH_HOME/glassfish/domains/kaleido/logs/server.log
$GLASSFISH_HOME/bin/asadmin start-domain kaleido 
$GLASSFISH_HOME/bin/asadmin undeploy kaleido-it-ear 
$GLASSFISH_HOME/bin/asadmin deploy --name=kaleido-it-ear $BASE/../../../kaleido-it-ear/target/kaleido-it-ear-$KALEIDO_VERSION.ear 


