if [ $# -eq 0 ]
then
  cpt=1
else
  cpt=$1
fi

for ((i = 0; i <= cpt; i += 1))
do
  # check jar deploy on google download code
  curl http://kaleido-foundry.googlecode.com/files/kaleido-distrib-0.8.1-bundle.zip > /dev/null
  curl http://kaleido-foundry.googlecode.com/files/kaleido-distrib-0.8.1-SNAPSHOT-bin.zip > /dev/null
done

