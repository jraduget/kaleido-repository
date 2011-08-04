export KALEIDO_WORKSPACE=...

# maven site deploy
cp -rf $KALEIDO_WORKSPACE/kaleido-parent/* /Users/jraduget/Developments/workspaces/eclipse/kaleidofoundry/kaleido-site/site/

# check jar deploy on google download code
curl http://kaleido-foundry.googlecode.com/files/kaleido-core-0.8.0-SNAPSHOT.jar > /dev/null
curl http://kaleido-foundry.googlecode.com/files/kaleido-core-0.8.0-SNAPSHOT-sources.jar > /dev/null
curl http://kaleido-foundry.googlecode.com/files/kaleido-core-0.8.0-SNAPSHOT-javadoc.jar > /dev/null

# check jar deploy on google download code
curl http://kaleido-foundry.googlecode.com/files/kaleido-core-0.8.1-SNAPSHOT.jar > /dev/null
curl http://kaleido-foundry.googlecode.com/files/kaleido-core-0.8.1-SNAPSHOT-sources.jar > /dev/null
curl http://kaleido-foundry.googlecode.com/files/kaleido-core-0.8.1-SNAPSHOT-javadoc.jar > /dev/null