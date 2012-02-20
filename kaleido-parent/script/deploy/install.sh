-- update child version after updating parent version
mvn -N versions:update-child-modules

-- releasing
-- https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-8.ReleaseIt

mvn release:clean
mvn release:prepare
mvn release:rollback