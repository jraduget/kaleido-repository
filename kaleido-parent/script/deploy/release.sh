# update child version after updating parent version
mvn -N versions:update-child-modules

# signing gpg 
# https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven

# releasing
# https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide

mvn clean deploy -Dgpg.passphrase=thephrase
mvn release:clean
mvn release:prepare
mvn release:rollback
mvn release:perform -Darguments=-Dgpg.passphrase=thephrase