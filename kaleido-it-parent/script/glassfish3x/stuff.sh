# ear deployment samples
# http://download.oracle.com/docs/cd/E19226-01/820-7695/gbxjk/index.html
# http://download.oracle.com/docs/cd/E18930_01/html/821-2433/deploy-1.html#deploy-1

# proxy setting if needed
asadmin create-jvm-options "-Dhttp.proxyHost=<proxyHost>"
asadmin create-jvm-options "-Dhttp.proxyPort=<proxyPort>"

# queue factory used for integration test
asadmin create-jms-resource --restype=javax.jms.QueueConnectionFactory --description="kaleido integration test queue factory" jms/kaleidoQueueFactory    
asadmin ping-connection-pool jms/kaleidoQueueFactory

# queue used for integration test
asadmin create-jms-resource --restype=javax.jms.Queue --description="kaleido integration test queue" jms/kaleidoQueue
asadmin --user admin list-jms-resources

# databases datasources
asadmin create-jdbc-connection-pool --datasourceclassname org.apache.derby.jdbc.ClientDataSource --restype javax.sql.XADataSource --property portNumber=1527:password=APP:user=APP:serverName=localhost:databaseName=kaleidoInt:connectionAttributes=\;create\\=true  kaleidoIntPool
asadmin create-jdbc-resource --connectionpoolid kaleidoIntPool jdbc/kaleido
asadmin list-jdbc-connection-pools
asadmin ping-connection-pool kaleidoIntPool

# mail
# http://weblogs.java.net/blog/felipegaucho/archive/2010/03/04/glassfish-v3-resources-administration-cli-tool-asadmin

# logging levels
asadmin create-jvm-options -Declipselink.logging.level=FINE
asadmin create-jvm-options -Declipselink.logging.level=INFO

# deploy / undeploy commands
asadmin deploy --name=kaleido-it-ear ../kaleido-it-ear/target/kaleido-it-ear-0.8.1-SNAPSHOT.ear
asadmin deploy --name=kaleido-it-ear --dbvendorname=javadb --createtables=true  ../kaleido-it-ear/target/kaleido-it-ear-0.8.1-SNAPSHOT.ear
asadmin deploy --name=kaleido-it-ear --dbvendorname=javadb --dropandcreatetables=true  ../kaleido-it-ear/target/kaleido-it-ear-0.8.1-SNAPSHOT.ear 
asadmin undeploy --droptables=true kaleido-it-ear 
asadmin undeploy kaleido-it-ear 




