# odata
Experiments with odata

List all cars:

curl -H 'accept: application/json' http://localhost:8080/my-car-service/CarService.svc/Cars | python -m json.tool
