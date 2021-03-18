<h1 align=center> CLI chat application using RabbitMQ </h1>

<p align=center>
  <img src="https://image.flaticon.com/icons/png/512/226/226777.png" width="150" height="150"/>
  <img src="https://cdn.worldvectorlogo.com/logos/rabbitmq.svg" width="130" height="130"/>
</p>

## Description
a minimal command-line chat application written in JAVA and using RabbitMQ.

## Features
- Distributed architecture (client-server).
- messages history (for broadcasted messages).
- Private and broadcast messages.
- User-friendly and simple usage.

## Requirements 
-  **RabbitMQ server** : https://www.rabbitmq.com
-  **Java 8 or higher**: https://www.oracle.com/fr/java/technologies/javase-downloads.html

## How to run
Before starting the application, run your rabbitMQ server    
```sh
$ rabbitmq-server   
```   

then, start the application server with the provided shell script   
```sh
$ ./startServer.sh  
```   

now you can run as many client as you want using the provided shell script   
```sh
$ ./startClient.sh  
``` 

## How to use
```sh
   --b <message>        : to broadcast message to all connected clients    
   --p <name> <message> : to send a private message to a specific client     
   --q                  : to leave the chat    
   --h                  : to display help   
```
