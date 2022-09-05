docker network create -d bridge smartvillageapp

docker run -d --name=roach1 --hostname=roach1 --net=smartvillageapp -p 26257:26257 -p 8080:8080  -v "//c/Users/Matej/cockroach-data/roach1:/cockroach/cockroach-data"  cockroachdb/cockroach:v22.1.6 start --insecure --join=roach1,roach2,roach3

docker run -d --name=roach2 --hostname=roach2 --net=smartvillageapp -v "//c/Users/Matej/cockroach-data/roach2:/cockroach/cockroach-data"  cockroachdb/cockroach:v22.1.6 start --insecure --join=roach1,roach2,roach3

docker run -d --name=roach3 --hostname=roach3 --net=smartvillageapp -v "//c/Users/Matej/cockroach-data/roach3:/cockroach/cockroach-data"  cockroachdb/cockroach:v22.1.6 start --insecure --join=roach1,roach2,roach3

:: docker exec -it roach1 ./cockroach init --insecure

:: docker exec -it roach1 ./cockroach sql --insecure

:: CREATE DATABASE smart_village;

:: exit