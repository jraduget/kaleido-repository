
# How to publish to maven Central repository

## Create an access token
Nexus maven central is accessible from : https://oss.sonatype.org

Create an access token using : https://oss.sonatype.org/#profile;User%20Token

Reference documentation : https://central.sonatype.org/publish/generate-token/

## GitHub Actions Deployment & Release workflows configuration

Github actions workflows are defined in the `.github/workflows` directory. The following files are used for deployment and release:
- .github/workflows/build.yml
- .github/workflows/release.yml

For maven central deployment, two variables are used in the workflows, to be defined in the GitHub secrets:
```yaml
server-username: OSS_SONATYPE_USERNAME
server-password: OSS_SONATYPE_PASSWORD
```

## Define access token in GitHub secrets

1. Go to your GitHub repository
2. Click on "Settings"
3. Click on "Secrets and variables" in the left sidebar
4. Click on "Actions"
5. Create a new secret "OSS_SONATYPE_USERNAME" and "OSS_SONATYPE_PASSWORD" with the values of your access token




# Launch your Release

```
git flow release start <version>

mvn versions:set -DnewVersion=<version>

git commit -m "Release <version>"

git flow release finish <version>

```


