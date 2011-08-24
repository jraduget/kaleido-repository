#!/bin/bash

# clean log, start domain if needed, and undeploy current version
cat /dev/null > /Users/jraduget/Developments/appserver/glassfishv3.0.1/glassfish/domains/kaleido/logs/server.log
asadmin start-domain kaleido 
asadmin undeploy kaleido-it-ear 
asadmin deploy --name=kaleido-it-ear ../kaleido-it-ear/target/kaleido-it-ear-0.8.1-SNAPSHOT.ear 


