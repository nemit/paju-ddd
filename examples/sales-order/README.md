# Example modules

Repository contains experimental example modules demonstrating use of the ddd-core as part of application or microservice 
using layered architecture and ports & adapters design pattern. 

## Domain layer

Simple Sales Order domain model using ddd-core. 

## Infrastructure layer

Infrastructure layer contains repository implementation for the ddd-model and port implementations. 
Intent is to run either spark or spring, not both. They're alternative approaches for port implementation.

Module | Description
------ | -----------
jdbi-statestore | PostgreSQL repository for the Sales Order domain model. Implemented using jbdi3 library. Contains docker for the database which must be running for the                   ports to function properly. 
boot and spark-api | Port exposing simple REST-API and websocket. In this example boot contains runtime for the spark-api. You can run the port with main function in [SalesOrderSparkApp.kt](boot/src/main/kotlin/io/paju/salesorder/SalesOrderSparkApp.kt)
spring-api | Port exposing HAL based REST-API to the domain model. Implemented with Spring MVC and Spring HATEOS. You can run the port with main function in [SpringRestPort.kt](spring-api/src/main/kotlin/io/paju/salesorder/infrastructure/ports/SpringRestPort.kt)

## Application layer

Missing from the current example, this would sit between infrastructure port and the domain layer and take care of for 
example transactions, application level security, metrics and provide a common application API towards different ports. 

