cd ../../
git checkout main
git fetch
git pull
docker build -f mt-payment/Dockerfile . -t publicdevop2019/mt-payment:latest --no-cache
docker push publicdevop2019/mt-payment:latest