cd ../../
git checkout main
git fetch
git pull
docker build -f mt-file/Dockerfile . -t publicdevop2019/mt-file:latest --no-cache
docker push publicdevop2019/mt-file:latest