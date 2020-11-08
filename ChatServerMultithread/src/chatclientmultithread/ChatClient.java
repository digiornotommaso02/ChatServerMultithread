/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientmultithread;
import messaggi.Messaggi;
import java.io.*; 
import java.util.*; 
import java.net.*;  

/**
 *
 * @author tommaso di giorno
 */
public class ChatClient {
    // Notifica
    private String notif = " [Benvenuto alla chat] ";
    // Porta
    private int porta;
    // Socket
    private Socket socket; 
    // Nome server
    private String NomeServer;
    //Nome Client
    private String NomeClient;
    // Input
    private ObjectInputStream inp;	
    // Output
    private ObjectOutputStream out;	
	
    // Costruttore ChatClient
    ChatClient(String NomeServer, int porta, String NomeClient) {
        this.NomeServer = NomeServer;
        this.porta = porta;
        this.NomeClient = NomeClient;
    }
    public String getNomeClient() {
        return NomeClient;// Ritorna il nome del Client
    }
    public void setNomeClient(String username) {
        this.NomeClient = username;// Setta il nome del Client
    }
    // Metodo utilizzato per inviare un messaggio al server
    void inviaMessaggio(Messaggi msg) {
        try {
            out.writeObject(msg);
	}
        catch(IOException e) {
            display("Exception writing to server: " + e);
	}
    }
    // Metodo che chiude i canali di Input e Output
    private void disconnettersi() {
	try { 
            if(inp != null) {
                inp.close();
            }
	}
	catch(Exception e) {}
	try {
            if(out != null) {
                out.close();
            }
        }
	catch(Exception e) {}
        try {
            if(socket != null) {
                socket.close();
            }
	}
	catch(Exception e) {}	
    }
    private void display(String msg) {
	System.out.println(msg);	
    }
    // Metodo che fa partire la chat
    public boolean start() {
        try {
            socket = new Socket(NomeServer, porta);
	} 
	catch(Exception ec) {}
	String mes = " Connessione effettuata. Nome e indirizzo: " + socket.getInetAddress() + " Porta: " + socket.getPort();
	display(mes);
	try { 
            inp  = new ObjectInputStream(socket.getInputStream());// Input
            out = new ObjectOutputStream(socket.getOutputStream());// Output
	}
	catch (IOException eIO) {}
	new ListenServer().start(); // Thread che viene utilizzato dal server per ascoltare il Client
	try {// Invia l'username al server sottoforma di stringa
            out.writeObject(NomeClient);
	}
	catch (IOException eIO) {}
	return true;
    }
    /*
    * Chat Main
    */
    public static void main(String[] args) {
        // Numero porta
        int portNumber = 1568;
        // Indirizzo server
        String serverAddress = "localhost";
        // Nome settato su Anonimo
        String userName = "Anonimo";
        Scanner scan = new Scanner(System.in);
        // Output per inserire il nome
        System.out.println("Inserisci il nome utente: ");
        userName = scan.nextLine();
        switch(args.length) {
            case 0:// Se non viene inserito niente
                break;
            case 1:// Se viene inserito solamente il nome del Client
                userName = args[0];
            case 2:// Se viene inserito il nome del Client e numero della porta
            try {
                portNumber = Integer.parseInt(args[1]);
            }
            catch(Exception e) {}
            case 3:// Se viene inserito il nome del Client, il numero della porta e l'indirizzo del server
            serverAddress = args[2];
	}
        // Oggetto Client
	ChatClient client = new ChatClient(serverAddress, portNumber, userName);
	// Il Client avvia la connessione con il server.
	if(!client.start()) {
            return;
        }
	System.out.println("Ciao! Benvenuto alla chat");
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Puoi scrivere @NomeCliente per inviare un messaggio al Client specificato");
	System.out.println("Puoi scrivere un messaggio per inviarlo in broadcast");
	System.out.println("Puoi scrivere partecipanti per vedere chi è connesso al canale");
	System.out.println("Puoi scrivere esci per disconnettersi dal server ");
        System.out.println("-------------------------------------------------------------------------");
        // Ciclo infinito che riceve i messaggi dei Client
	while(true) {
            System.out.print(" ");
	    // Viene letto il messaggio del Client
            String msg = scan.nextLine();
            // Fa disconnettere il Client dalla chat
            if(msg.equalsIgnoreCase("esci")) {
                client.inviaMessaggio(new Messaggi(Messaggi.esci, ""));
                break;
            }	
            // Fa vedere la lista dei Client che partecipano alla chat
            else if(msg.equalsIgnoreCase("partecipanti")) {
                client.inviaMessaggio(new Messaggi(Messaggi.partecipanti, ""));				
            }
            // Messaggio normale
            else { 
                client.inviaMessaggio(new Messaggi(Messaggi.message, msg));
	    }
	}
        // Chiude la lettura dei messaggi
	scan.close();
        // Disconnette il Client dalla chat
	client.disconnettersi();	
	}
        // Aspetta i messaggi
	class ListenServer extends Thread {
            public void run() {
                while(true) {
                    try {
		    // Legge i messaggi da input
                    String msg = (String) inp.readObject();
		    // Fa vedere il messaggio
                    System.out.println(msg);
                    }
                    catch(IOException e) {
                        display("Il server ha chiuso la connessione: ");
                        break;
                    }
                    catch(ClassNotFoundException e2) {}
                }
            }
        }
} 
