cd ../../
git checkout main
git fetch
git pull
docker build -f mt-saga/Dockerfile . -t publicdevop2019/mt-saga:latest --no-cache
docker push publicdevop2019/mt-saga:latest