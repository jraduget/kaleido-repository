export MAVEN_OPT=-Xmx1024m

# update child version after updating parent version
# mvn -N versions:update-child-modules

# site and google code upload
mvn clean site:site && mvn site:stage
# mvn site:deploy - does not work with maven-site 3 and svn scm  

# signing gpg 
# https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven

# releasing
# https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide

mvn clean deploy -Dgpg.keyname=${GPG_KEYNAME} -Dgpg.passphrase=${GPG_PASSPHRASE}
mvn release:clean
mvn release:prepare -Dgpg.keyname=${GPG_KEYNAME} -Darguments=-Dgpg.passphrase=${GPG_PASSPHRASE}
#mvn release:rollback
mvn release:perform -Dgpg.keyname=${GPG_KEYNAME} -Darguments=-Dgpg.passphrase=${GPG_PASSPHRASE}



