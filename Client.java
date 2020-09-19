
import com.rabbitmq.client.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Client implements AutoCloseable{

	private Connection connection;
    private Channel channel;
    private static String username;

    private static final String EXCHANGE_ROOM = "generalTopic";
    private static final String EXCHANGE_PRIVATE = "directTopic";
    private static final String EXCHANGE_SERVER = "serverTopic";
    private static final String SERVER_QUEUE = "serverQueue";



    public Client() throws IOException, TimeoutException {
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");

	    connection = factory.newConnection();
	    channel = connection.createChannel();

	    channel.exchangeDeclare(EXCHANGE_ROOM, "topic");
	    channel.exchangeDeclare(EXCHANGE_PRIVATE, "direct");

	    channel.exchangeDeclare(EXCHANGE_SERVER, "topic");
    }



    public static void main(String[] argv) throws Exception {

    	try (Client client = new Client()) {

	    	System.out.println("************************************************************");
	    	System.out.println("*                     IDS : RABBITMQ LAB                   *");
	    	System.out.println("*                      CHAT APPLICATION                    *");
	    	System.out.println("************************************************************\n");

	    	//Reading username:
	    	Scanner s = new Scanner(System.in);
	        System.out.print("type your name : ");
	        client.username = s.nextLine();
	        
	        //CallBack when a message is consumed;
	        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
		        String message = new String(delivery.getBody(), "UTF-8");
		        System.out.println(message);
	    	};

	    	//random queue name :
        	String queueName = client.channel.queueDeclare().getQueue(); 

        	//Binding queue to our exchanges:
	        client.channel.queueBind(queueName, EXCHANGE_ROOM, "");
	        client.channel.queueBind(queueName, EXCHANGE_PRIVATE, username);

	        client.channel.queueBind(queueName, EXCHANGE_SERVER, username);

	        //start consuming callback
        	client.channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

        	//notify other clients:
        	String welcome = welcomeMessage(username);
        	client.channel.basicPublish(EXCHANGE_ROOM, "", null, welcome.getBytes("UTF-8"));


        	boolean connected = true;
	        while(connected){
	            String msg = s.nextLine();
	            if(msg.startsWith("--")){
	                switch(msg.charAt(2)){

	                	//PRIVATE:
	                    case 'p':
	                        String[] params = msg.split(" ", 3);
	                        if(params.length < 3)
	                            System.out.println("Usage: --p <Name> <Message>");	                        
	                        else 
	                            client.sendMessage("private", params[2], params[1]);
	                        break;

	                    //BROADCAST:
	                    case 'b':
	                    	String[] param = msg.split(" ", 2);
	                        if(param.length < 2)
	                            System.out.println("Usage: --b <Message>");	                        
	                        else
				                client.sendMessage("broadcast", param[1], "");				            
	                    	break;

	                    //QUIT:
	                    case 'q':
	                        client.sendMessage("quit", "", "");
	                        connected = false;
	                        break;

	                    //HELP:
	                    case 'h':
	                        helpDisplay();
	                        break;

	                    //UNKNOWN:
	                    default:
	                        System.out.println("command ("+ msg.split(" ")[0] +") not found\n");
	                        helpDisplay();
	                        break;
	                }
	            } 
	            //BROADCAST:
	            else {
	            	System.out.println("command ("+ msg.split(" ")[0] +") not found\n");
	            	helpDisplay();
	            }
	        }
    	}//try end

    }//main end

	@Override
	public void close() throws Exception {
        channel.close();
        connection.close();
		
	}


    
    private void sendMessage(String type, String message, String dest) throws IOException{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time = sdf.format(date);
		
		String finalMessage;
		switch(type){
			case "private":
				finalMessage = "[" + time + "] " + "Private message from " + username + ": " + message;
				channel.basicPublish(EXCHANGE_PRIVATE, dest, null, finalMessage.getBytes());
				System.out.println("[" + time + "] " + "Private message to " + dest + ": " + message);
			break;

			case "broadcast":
				finalMessage = "[" + time + "] " + username + ": " + message;
				channel.basicPublish(EXCHANGE_ROOM, "", null, finalMessage.getBytes("UTF-8"));
			break;

			case "quit":
		        String leaveMsg = "[" + time + "] " + username + " left the room!";
                channel.basicPublish(EXCHANGE_ROOM, "", null, leaveMsg.getBytes("UTF-8"));
			break;
		}
    }
    
    
    private static String welcomeMessage(String username){
    	return ("Hi "+username+" , welcome to our chat server !");
    }


    private static void helpDisplay(){
        System.out.println("******************** HELP ********************");
        System.out.println("*  --b <message>        : Broadcast message  *");
        System.out.println("*  --p <name> <message> : Private message    *");
        System.out.println("*  --q                  : Leave the chat     *");
        System.out.println("*  --h                  : Display help       *");
        System.out.println("**********************************************\n");
    }

}