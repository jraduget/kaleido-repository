export MAVEN_OPT=-Xmx1024m

# update version of test integration modules
#---------------------------------------------
cd kaleido-integration-test
mvn versions:set -DnewVersion=0.9.1-SNAPSHOT
mvn -N versions:update-child-modules
git commit -m "release XXX prepare" .

# site 
#---------------------------------------------
mvn clean site:site && mvn site:stage
# mvn site:deploy - does not work with maven-site 3 and svn scm  
cp -rf target/staging/* ./site/
find ./site/  -name '*.html' -exec dos2unix {} \;

# releasing : https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide
# signing gpg :  https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven
# sonatype :
#	nexus for maven central : https://oss.sonatype.org/index.html#welcome
#   staging repo url : https://oss.sonatype.org/index.html#stagingRepositories
# usefull : 
# 	http://www.coding-stories.com/2010/09/02/signer-les-jars-avec-maven/
# 	https://confluence.sakaiproject.org/display/REL/Maven+release+plugin+cheat+sheet
#	http://www.sonatype.com/people/2010/01/how-to-generate-pgp-signatures-with-maven/
#---------------------------------------------
#mvn clean deploy -P kaleido-gpg,javaee5
mvn release:clean
mvn release:prepare
#mvn release:rollback
mvn release:perform -P kaleido-gpg


# git branch and tagging part
#---------------------------------------------
# git branch kaleido-0.9.1
# git push --all
# git tag -a kaleido-0.9.1 -m "maven central release"
# git push --tags

