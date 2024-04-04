# Dictionary Query System - COMP90015 Assignment 1

## Project Overview
This project is the assignment 1 for the Distributed Systems (COMP90015) at the University of Melbourne. The goal is to develop a dictionary query program based on TCP and multi-threading, which is divided into front-end and back-end parts.

## Features
- **TCP Networking**: Uses TCP protocol to ensure the reliability of data transmission.
- **Multithreading**: Supports multiple clients querying simultaneously, improving query efficiency.
- **Frontend-Backend Separation**: Clearly distinguishes between the frontend user interface and backend service logic, making the system structure clear.

## Technology Stack
- Programming language: Java
- Network communication: TCP
- Multithreading
- Frontend framework: JavaFX
- Build tool: Maven
  
## Quick Start
After cloning the project, use Maven to run the following commands separately:
```
mvn clean compile assembly:single -Pclient
mvn clean compile assembly:single -Pserver
```
After running one command, copy the generated jar file before running the second command.

For the server jar, run the following command:
```
java -jar [jar file name] [port]
```
For the client jar, due to a bug in JavaFX when using Maven to build, we need to download the suitable SDK from the JavaFX official website first, and then run the following command to start:
```
java --module-path [path to SDK lib directory] --add-modules javafx.controls,javafx.fxml -jar [jar file name] [port] [path to database file]
```
The above command includes default values for the port and database file; if the directory structure is the same as the GitHub repository, they can be omitted.

### Known Issues
1. The frontend's text input box does not handle spaces and enters well, which can affect the query and addition of words.
2. The functionality to add multiple meanings at the same time has only been implemented on the frontend, not yet on the backend.
