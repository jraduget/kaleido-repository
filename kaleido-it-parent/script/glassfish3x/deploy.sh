#!/bin/bash

# clean log, start domain if needed, and undeploy current version
cat /dev/null > /Users/jraduget/Developments/appserver/glassfishv3.0.1/glassfish/domains/kaleido/logs/server.log
asadmin start-domain kaleido 
asadmin undeploy --droptables=true kaleido-it-ear 

# resources creation 
echo 'start jms queue factory creation...'
if  asadmin ping-connection-pool jms/kaleidoQueueFactory
then echo 'jms/kaleidoQueueFactory resource already exists'
else asadmin create-jms-resource --restype=javax.jms.QueueConnectionFactory --description="kaleido integration test queue factory" jms/kaleidoQueueFactory
fi

echo 'start jms queue creation...'
if asadmin --user admin list-jms-resources | grep 'jms/kaleidoQueue'
then echo 'jms/kaleidoQueue resource already exists'
else asadmin create-jms-resource --restype=javax.jms.Queue --description="kaleido integration test queue" jms/kaleidoQueue
fi

# create jdbc datasource at each redeploy (derby in memory reset)
echo 'start datasource creation...'
asadmin delete-jdbc-resource jdbc/kaleido
asadmin delete-jdbc-connection-pool kaleidoIntPool
asadmin create-jdbc-connection-pool --datasourceclassname org.apache.derby.jdbc.ClientDataSource --restype javax.sql.XADataSource --property portNumber=1527:password=APP:user=APP:serverName=localhost:databaseName=kaleidoInt:connectionAttributes=\;create\\=true  kaleidoIntPool
asadmin create-jdbc-resource --connectionpoolid kaleidoIntPool jdbc/kaleido
asadmin list-jdbc-connection-pools
asadmin ping-connection-pool kaleidoIntPool

echo 'restart java database...'
asadmin stop-database
asadmin start-database

# ear deployment redeploy 
echo 'start deployment...'
asadmin stop-domain kaleido
asadmin start-domain kaleido
asadmin deploy --name=kaleido-it-ear --dbvendorname=javadb --createtables=true  ../kaleido-it-ear/target/kaleido-it-ear-0.8.1-SNAPSHOT.ear 


