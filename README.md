# OData
OData Service with Olingo V2 and a UI that makes use of SAP UI5. 

There are many untidy parts but the main purpose is to explore what UI5 can do and the programming model that OLingo provides.

It would be a good idea to upgrade this to OLingo V4.

## Run

```
git clone https://github.com/chrisjleu/odata.git
cd odata
mvn tomcat7:run or mvn jetty:run
```

## Access
UI:

http://localhost:8080/my-car-service/cars.html

Service meta-data:

[http://localhost:8080/my-car-service/CarService.svc/$metadata](http://localhost:8080/my-car-service/CarService.svc/$metadata)

Queries:

curl -H 'accept: application/json' http://localhost:8080/my-car-service/CarService.svc/Cars | python -m json.tool


## Debug

```
export MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=n"
```
