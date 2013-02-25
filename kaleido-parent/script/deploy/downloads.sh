if [ $# -eq 0 ]
then
  cpt=1
else
  cpt=$1
fi

for ((i = 0; i <= cpt; i += 1))
do
  # check jar deploy on google download code
  curl http://kaleido-foundry.googlecode.com/files/kaleidofoundry-0.9.0-SNAPSHOT-Rev806-bundle.zip > /dev/null
done

