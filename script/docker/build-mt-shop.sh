cd ../../
git checkout main
git fetch
git pull
docker build -f mt-shop/Dockerfile . -t publicdevop2019/mt-shop:latest --no-cache
docker push publicdevop2019/mt-shop:latest