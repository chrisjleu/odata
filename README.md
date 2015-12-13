# OData
OData Service with [Olingo V2](https://olingo.apache.org/doc/odata2/index.html) and a UI that makes use of [(SAP) OpenUI5](http://openui5.org/). The main purpose is to explore what Open UI5 can do and the programming model that OLingo provides.

It is based on a [sample server application](https://olingo.apache.org/doc/odata2/sample-setup.html) but with some additions, namely the ability to create new elements (cars in this case).

This is a rapid prototype so naturally there are many untidy parts to it. For instance:

- No unit tests
- Incomplete functionality (only create implemented for cars)
- No form data validation
- Missing Javadoc
- In-memory data store
- Javascript is not factored out nicely
- and so on...

It would be a good idea to upgrade this to [OLingo V4](https://olingo.apache.org/doc/odata4/) as the APIs look to have changed quite a bit.

## Run

```
git clone https://github.com/chrisjleu/odata.git
cd odata
mvn tomcat7:run or mvn jetty:run
```

Another option is to run the jar itself (thanks to jetty-runner jar). This is useful for PaaS environments.

```
mvn package
java -jar target/dependency/jetty-runner.jar target/*.war
```

## Access
User Interface:

Car dashboard is deployed to the root context.

[http://localhost:8080/](http://localhost:8080/)

Service meta-data:

OData services, unlike traditional REST APIs, provide information about what the service looks like.

[http://localhost:8080/CarService.svc/$metadata](http://localhost:8080/my-car-service/CarService.svc/$metadata)

Queries:

curl -H 'accept: application/json' http://localhost:8080/CarService.svc/Cars | python -m json.tool


## Debug

Set the debug port to 4000:
```
export MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=n"
mvn jetty:run
```