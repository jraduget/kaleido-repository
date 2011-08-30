#!/bin/bash

echo "***************************************************************************"
echo "glassfish base dir: $GLASSFISH_HOME"
echo "***************************************************************************"

# clean log, start domain if needed, and undeploy current version
cat /dev/null > $GLASSFISH_HOME/glassfish/domains/kaleido/logs/server.log
$GLASSFISH_HOME/bin/asadmin start-domain kaleido 
$GLASSFISH_HOME/bin/asadmin undeploy kaleido-it-ear 
$GLASSFISH_HOME/bin/asadmin deploy --name=kaleido-it-ear ../kaleido-it-ear/target/kaleido-it-ear-0.8.1-SNAPSHOT.ear 


