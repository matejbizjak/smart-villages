docker run -d --rm --name smart-villages-user-service --network smartvillageapp -p 8081:8081 --pull always matejbizjak/smart-villages-user-service:latest

docker run -d --rm --name smart-villages-central-service --network smartvillageapp -p 8082:8082 --pull always matejbizjak/smart-villages-central-service:latest

docker run -d --rm --name smart-villages-solar-service --network smartvillageapp -p 8083:8083 --pull always matejbizjak/smart-villages-solar-service:latest
docker run -d --rm --name smart-villages-solar-iot-1 --network smartvillageapp --pull always matejbizjak/smart-villages-solar-iot 520cb7af-b986-4415-ba90-ba3c1a589a40 password password2

docker run -d --rm --name smart-villages-notification-service --network smartvillageapp -p 8084:8084 --pull always matejbizjak/smart-villages-notification-service:latest

docker run -d --rm --name smart-villages-vehicle-service --network smartvillageapp -p 8085:8085 --pull always matejbizjak/smart-villages-vehicle-service:latest

docker run -d --rm --name smart-villages-charger-service --network smartvillageapp -p 8086:8086 --pull always matejbizjak/smart-villages-charger-service:latest
docker run -d --rm --name smart-villages-charger-iot-1 --network smartvillageapp --pull always matejbizjak/smart-villages-charger-iot 520cb7af-b986-4415-ba90-111c1a589a40 password password2

docker run -d --rm --name smart-villages-house-service --network smartvillageapp -p 8087:8087 --pull always matejbizjak/smart-villages-house-service:latest
docker run -d --rm --name smart-villages-house-iot-1 --network smartvillageapp --pull always matejbizjak/smart-villages-house-iot 520cb7af-b986-4415-ba90-111011589a40 password password2