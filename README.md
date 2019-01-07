## PARKING SPACES 
    Simple Rest API project for managing parking spaces

##### Technological stack
* Spring Boot
* Java8
* Maven
* Junit
* in memory H2 Database
#####User stories
* drivers can create their own accounts
* drivers can register and delete their vehicles (but can have at least one)
* drivers can start and stop their parkometer
* driver can see his parking history
* driver can see how much he have to pay for every parking

* parking operator can check if vehicle started parkometer
* parking owner can check how much he earn any given day
###Annotations
###### Parking rates 

|Driver type| First hour  | Second hour |  3rd and each next hour
|-------------| ------------- | ------------- |----------
|Regular|1 PLN| 2 PLN  |1.5x more than thehour before|
|Disabled|free | 2 PLN |1.2x more than thehour before|

Every driver type has specified his parking rates, and every driver accout has assigned
driver type to it, so technically defining new parking rates for any kind of drivers is simple as insert new row to DRIVER_TYPE, e.g.

|Driver type| First hour  | Second hour |  3rd and each nexthourRegular
|-------------| ------------- | ------------- |----------
|Owner|0 PLN| 0 PLN  |  0|
###### Currency
User currency is just a foreign key in users databse table to row in table CURRENCY, so if the parking owner will be ready 
to introduce new currency he can do it in 1 minute or less
