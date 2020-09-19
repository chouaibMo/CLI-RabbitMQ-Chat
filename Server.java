
import com.rabbitmq.client.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Server {
	private File file;
	private Connection connection;
    private Channel channel;

    private static final String EXCHANGE_ROOM = "generalTopic";

    private static final String EXCHANGE_SERVER = "serverTopic";
    private static final String QUEUE_SERVER = "serverQueue";

    private static String historique;

    public Server() throws IOException, TimeoutException {
    	file = new File("historique.txt");

    	if (! file.exists()){
              file.createNewFile();
              historique = "";
              updateHistorique("\n********************* CHAT HISTORY *********************"); 
        }
        else{
        	historique = loadHistorique();
        }

	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");

	    connection = factory.newConnection();
	    channel = connection.createChannel();

	    channel.exchangeDeclare(EXCHANGE_SERVER, "topic");
	    channel.queueDeclare(QUEUE_SERVER, false, false, false, null);

    }



    public static void main(String[] argv) throws Exception {

    	try {
            Server server = new Server();
	    	System.out.println("************************************************************");
	    	System.out.println("*                     IDS : RABBITMQ LAB                   *");
	    	System.out.println("*                        CHAT SERVER                       *");
	    	System.out.println("************************************************************\n");


	        //CallBack when a message is consumed;
	        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
		        String message = new String(delivery.getBody(), "UTF-8");
		        if(message.endsWith(", welcome to our chat server !")) {
		        	String[] params = message.split(" ");
		        	String endhistorique= "\n********************************************************\n";
		        	server.channel.basicPublish(EXCHANGE_SERVER, params[1], null, (historique+endhistorique).getBytes("UTF-8"));
		        }
		        else {
			        updateHistorique(message);
			        System.out.println(message);
		        }
	    	};



        	//Binding queue to our exchanges:
	        server.channel.queueBind(QUEUE_SERVER, EXCHANGE_ROOM, "");

	        //start consuming callback
        	server.channel.basicConsume(QUEUE_SERVER, true, deliverCallback, consumerTag -> { });

        	boolean connected = true;
        	while(connected){

        	}

    	}
        catch(Exception e){
        }//try end

    }//main end



    private String SendHistorique(String type, String message, String dest) throws IOException{
    	String res = "";
        try (BufferedReader b = new BufferedReader(new FileReader("historique.txt"))) {
            String str;
            Integer suiv;
            while((str=b.readLine())!=null){
                res=res+str+"\n";
                if(res.length() > 1000){
                    suiv = res.indexOf("\n");
                    res = res.substring(suiv+1);
                }
            }
        }
        return res;
    }


    private static String loadHistorique() throws IOException{
    	String res = "";
        try (BufferedReader b = new BufferedReader(new FileReader("historique.txt"))) {
            String str;
            Integer suiv;
            while((str=b.readLine())!=null){
                res=res+str+"\n";
                if(res.length() > 1000){
                    suiv = res.indexOf("\n");
                    res = res.substring(suiv+1);
                }
            }
        }
        return res;
	}

    private static void updateHistorique(String message)throws IOException{
    	try (PrintWriter p = new PrintWriter(new FileWriter("historique.txt",true))) {
    		historique = historique + "\n"+ message;
    		p.println(message);
    	}
    }


}