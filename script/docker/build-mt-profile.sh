cd ../../
git checkout main
git fetch
git pull
docker build -f mt-profile/Dockerfile . -t publicdevop2019/mt-profile:latest --no-cache
docker push publicdevop2019/mt-profile:latest