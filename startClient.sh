

export CPATH=.:amqp-client-5.8.0.jar:slf4j-simple-1.7.30.jar:slf4j-api-1.7.30.jar;

rm *.class;

javac -cp $CPATH Client.java;
java -cp $CPATH Client;