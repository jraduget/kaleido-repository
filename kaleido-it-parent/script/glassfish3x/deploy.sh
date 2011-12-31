#!/bin/bash

# current build version (TODO extract it from pom.xml)
KALEIDO_VERSION=0.8.1

# script base dir
BASE="$(cd -P -- $(dirname -- "$0"); pwd -P)"

if [ -z "$GLASSFISH_HOME" ]; then
	if [ -z "$1" ]; then
		echo "***************************************************************************"
		echo "To deploy the kaleidoFoundry integration tests, please:"
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
echo "Deploying kaleido integration tests $KALEIDO_VERSION :"
echo "-> from '$BASE'"
echo "-> to glassfish '$GLASSFISH_HOME' using domain kaleido"
echo "***************************************************************************"

# domain creation if needed
if $GLASSFISH_HOME/bin/asadmin list-domains | grep 'kaleido'
then echo 'kaleido domain already exists'
else
$GLASSFISH_HOME/bin/asadmin create-domain --adminport 4848 --instanceport 8380 --nopassword=true kaleido 
fi

# clean log, start domain if needed, and undeploy current version
> $GLASSFISH_HOME/glassfish/domains/kaleido/logs/server.log

$GLASSFISH_HOME/bin/asadmin start-database
$GLASSFISH_HOME/bin/asadmin start-domain kaleido 
$GLASSFISH_HOME/bin/asadmin undeploy --droptables=true kaleido-it-ear 

# resources creation 
echo 'start jms queue factory creation...'
$GLASSFISH_HOME/bin/asadmin create-jms-resource --restype=javax.jms.QueueConnectionFactory --description="kaleido integration test queue factory" jms/kaleidoQueueFactory

echo 'start jms queue creation...'
$GLASSFISH_HOME/bin/asadmin create-jms-resource --restype=javax.jms.Queue --description="kaleido integration test queue" jms/kaleidoQueue

# create jdbc datasource at each redeploy (derby in memory reset)
echo 'start datasource creation...'
$GLASSFISH_HOME/bin/asadmin delete-jdbc-resource jdbc/kaleido
$GLASSFISH_HOME/bin/asadmin delete-jdbc-connection-pool kaleidoIntPool
$GLASSFISH_HOME/bin/asadmin create-jdbc-connection-pool --datasourceclassname org.apache.derby.jdbc.ClientDataSource --restype javax.sql.XADataSource --property portNumber=1527:password=APP:user=APP:serverName=localhost:databaseName=kaleidoInt:connectionAttributes=\;create\\=true  kaleidoIntPool
$GLASSFISH_HOME/bin/asadmin create-jdbc-resource --connectionpoolid kaleidoIntPool jdbc/kaleido
$GLASSFISH_HOME/bin/asadmin list-jdbc-connection-pools
$GLASSFISH_HOME/bin/asadmin ping-connection-pool kaleidoIntPool

echo 'restart java database...'
$GLASSFISH_HOME/bin/asadmin stop-database
$GLASSFISH_HOME/bin/asadmin start-database

# ear deployment redeploy 
echo 'start deployment...'
$GLASSFISH_HOME/bin/asadmin stop-domain kaleido
$GLASSFISH_HOME/bin/asadmin start-domain kaleido 
$GLASSFISH_HOME/bin/asadmin deploy --name=kaleido-it-ear --dbvendorname=javadb --createtables=true  $BASE/../../../kaleido-it-ear/target/kaleido-it-ear-$KALEIDO_VERSION.ear
