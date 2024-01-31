# Project Setup Guide

To run this program, follow these steps:

## Prerequisites
#### 1. Java Development Kit (JDK 17):
Download JDK 17 (Java SE Development Kit 17) from Oracle's official website.
#### 2. H2 Database (Version 2.2.224):
Download H2 Database from the official website.

## Database Configuration
H2 Database Setup:
After H2 Database installation, create a database with the following parameters:

URL: 
#### 'jdbc:h2:tcp://localhost//todo3'
Username: 
#### 'sa' (no password required)
To create a database with a different URL, use 'jdbc:h2:/[databasenameyouwant]'.
## Running the Program
#### Navigate to Program Directory:
Open a command prompt window and navigate to the directory where the program is saved.
#### Execute the JAR File:
Run the program by executing the JAR file in 'security2pro/out/artifacts/security2pro_jar/'. If the JAR file is named 'firstapp', use the following command:
#### java -jar firstapp.jar
## Initial User Setup
#### Default Users:
The app includes three default users with different roles.
#### Admin user:
username: 'admin', password: 'adminPassword'
#### Team Lead user:
username: 'teamLead', password: 'teamLeadPassword'
#### Team Member user:
username:'teamMember', password: 'teamMemberPassword'

## Additional Resources
#### Swagger UI:
Explore the API documentation using Swagger UI at 'http://localhost:8080/swagger-ui/index.html' ,
, not 'http://localhost:8080/swagger-ui/index.html/'. Make sure '/' is not added at the end of this URL.
