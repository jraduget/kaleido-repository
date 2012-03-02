export MAVEN_OPT=-Xmx1024m

# site 
#---------------------------------------------
mvn clean site:site && mvn site:stage
# mvn site:deploy - does not work with maven-site 3 and svn scm  
cp -rf target/staging/* ./site/
find ./site/  -name '*.html' -exec dos2unix {} \;

# releasing : https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide
# signing gpg :  https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven
#---------------------------------------------
mvn clean deploy -Dgpg.keyname=${GPG_KEYNAME} -Dgpg.passphrase=${GPG_PASSPHRASE}
mvn release:clean
mvn release:prepare -Dgpg.keyname=${GPG_KEYNAME} -Dgpg.passphrase=${GPG_PASSPHRASE}
#mvn release:rollback
mvn release:perform -Dgpg.keyname=${GPG_KEYNAME} -Dgpg.passphrase=${GPG_PASSPHRASE}

# git branch and tagging part
#---------------------------------------------
# git branch kaleido-0.8.1
# git push --all
# git tag -a kaleido-0.8.1 -m "maven central release"
# git push --tags

# update child version after updating parent version
#---------------------------------------------
# mvn -N versions:update-child-modules
