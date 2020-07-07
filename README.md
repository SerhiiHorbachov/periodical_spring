
# PERIODICALS
Java external course final project using spring boot

Periodicals System. Administrator manages the periodicals catalogue. Reader(user) can subscribe to periodical by choosing a periodical from the catalogue. 
The system calculates the payment and registers Invoice.  

## Author
Serhii Horbachov

## Technologies
Java 11
PostgreSQL 
Springboot 
Hibernate
Thymeleaf

## Installation Instructions
1.Project is implemented using Java 11. Install if required.  
2.Download the project: clone via git (_https://github.com/keynod/periodical_spring.git_) or download project and unzip).
3.Install PostgreSQL database, with username=postgres, password=password. Or set your own credentials and adjust settings in application.properties 
4.Create database with name periodicals_db.
5.Use script (_src/main/resources/schema.sql_) to create tables and populate test data.  
6.To start application run 
>__mvn spring-boot:run__

Administrator workspace credentials: 
>login: admin.@mail.com
>password: password

To access User's workspace, register your own user or use predefined one: 
>login: user.@mail.com
>password: password

