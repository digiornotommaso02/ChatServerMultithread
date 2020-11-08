/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatservermultithread;
import messaggi.Messaggi;
import java.io.*; 
import java.util.*; 
import java.net.*;

/**
 *
 * @author tommaso di giorno
 */
public class ChatServerMultithread {
    // ID univoco
    private static int ID; 
    // ArrayList dei clienti connessi
    private ArrayList<ThreadClient> Client;
    // Porta delle connessioni    
    private int port;
    // Verifica il funzionamento del server
    private boolean verifica;
    // Notifica
    private String notifica = " [Benvenuto alla chat] ";
    
        // Costruttore ChatServerMultithread
	public ChatServerMultithread(int port) {
            this.port = port;
            Client = new ArrayList<ThreadClient>();
	}
        // Invia un messaggio broadcast a tutti i Client connessi
	private synchronized boolean broadcast(String message) { 
            String[] string = message.split(" ",3);   
            boolean isPrivate = false;// Messaggio da Client a Client
            if(string[1].charAt(0) == '@') {
                isPrivate = true;
            }
	    // Messaggio per un singolo Client
            if(isPrivate == true) {
                String tocheck = string[1].substring(1, string[1].length());
		message = string[0] + string[2];
		String messageLf = message;
		boolean ricerca = false;// Variabile booleana per la ricerca del nome
		    // Ciclo che cerca il nome del destinatario del messaggio
                    for(int y = Client.size(); --y >= 0;) {
                        ThreadClient threadclient1 = Client.get(y);
                        String check = threadclient1.getNomeClient();
                            if(check.equals(tocheck)) {
				// 
				if(!threadclient1.messaggio(messageLf)) {
                                    Client.remove(y);// Rimuove il Client
                                    display("Il Client " + threadclient1.NomeClient + " è stato disconnesso");
                                }
				// Nome del Client trovato
				ricerca = true;
				break;
                            }
                    }
                    // Nome del Client non trovato
                    if(ricerca!=true){
			return false; 
                    }
            }
            // Messaggio per tutti i Client connessi alla chat(broadcast)
            else {
		String messageLf =  message;
		System.out.print(messageLf);// Messaggio
                // Ciclo che cerca il nome del Client da rimuovere
		for(int i = Client.size(); --i >= 0;) {
                    ThreadClient threadclient = Client.get(i);
			if(!threadclient.messaggio(messageLf)) {
                            Client.remove(i);// Rimuove il Client
                            display("Cliente " + threadclient.NomeClient + " è disconnesso");
			}
		}
            }
            return true;
	}
        // Messaggio di logout da parte del Client
	synchronized void remove(int id) {
	    String disconnectedClient = "";
		for(int i = 0; i < Client.size(); ++ i) // Ciclo che cerca il Cliente che ha sritto il messaggio di logout
                {
		    ThreadClient threadclient = Client.get(i);
		    if(threadclient.id == id) { // Ricerca dell'ID per rimuovere il Client
			disconnectedClient = threadclient.getNomeClient();
			Client.remove(i);// Rimuove il Client
			break;
		    }
		}
	    broadcast(notifica + disconnectedClient + " ha abbandonato la chat " + notifica);
	}
        // Metodo display
	private void display(String mes) {
		String Messaggio =  mes;
		System.out.println(Messaggio);
	}
        public void start() {
	    verifica = true;
	    try {
                ServerSocket serverSocket = new ServerSocket(port);// Socket del server
		while(verifica) { // Ciclo che aspetta che un client si connetta
                    display("Il Server sta aspettando la connessione sulla porta " + port );
                    Socket socket = serverSocket.accept();// Accetta la connessione del client
                    if(!verifica){
			break;
                    }
                    ThreadClient t = new ThreadClient(socket);// Thread che utilizza il Client per messaggiare
                    Client.add(t);// Aggiunge il Client all'ArrayList dei clienti	
                    t.start();// Fa partire il thread appena creato
	        }			
	    }
	    catch (IOException e) {}
	}
	/*
	 * Server Main
	 */ 
	public static void main(String[] args) {
            int portNumber = 1568;// Numero porta connessione
            switch(args.length) {
                case 0:
                    break;
		case 1:
		    try {
                        portNumber = Integer.parseInt(args[0]);
                    }
                    catch(Exception e) {}
            }
            // Crea e avvia l'oggetto del server ChatServerMultithread
            ChatServerMultithread server = new ChatServerMultithread(portNumber);
            server.start();// Lo avvia
	}
	// Istanza della classe utilizzata per ogni Client connesso
	class ThreadClient extends Thread {
            // Socket che serve per ricevere i messaggi del Client
            Socket socket;
            // Input
            ObjectInputStream inp;
            // Output
            ObjectOutputStream out;
            // ID unico per ogni Client che serve per disconnessione
            int id;
            // Nome Client
            String NomeClient;
            // Messaggio
            Messaggi mes;
            // Costruttore ThreadClient
            ThreadClient(Socket socket) {
                id = ++ ID;// id che cambia ogni volta
                this.socket = socket;
                    try {// Input e Output dei messaggi
                        out = new ObjectOutputStream(socket.getOutputStream());// Output
                        inp  = new ObjectInputStream(socket.getInputStream());// Input
                        NomeClient = (String) inp.readObject();// Legge il nome del Client
                        broadcast(NomeClient + " sta partecipando alla conversazione" );
		    }
		    catch (IOException e) {}
		    catch (ClassNotFoundException e) {}
            }
	    public String getNomeClient() {
                return NomeClient;// Ritorna il nome del Client
	    }
	    public void setNomeClient(String NomeClient) {
                this.NomeClient = NomeClient;// Setta il nome del Client
	    }
            // Ciclo che serve per inviare e leggere i messaggi dei Client
	    public void run() {
		boolean verifica = true;
		while(verifica) {
		    try {      
                        mes = (Messaggi) inp.readObject();
		    }
		    catch (IOException e) {}
		    catch(ClassNotFoundException e2) {}
	            String message = mes.getMessaggio();
                    // Tipi di messaggi
		    switch(mes.getTipo()) {
		        case Messaggi.message:
		        boolean confirmation =  broadcast(NomeClient + ": " + message);
			    if(confirmation == false) {
                                String msg = "Non esistono utenti di questo tipo";
                                messaggio(msg);
		            }
		        break;
			case Messaggi.esci:
                            display(NomeClient + ": disconnesso dalla chat");
                            verifica = false;
			break;
			case Messaggi.partecipanti:
                            messaggio("Lista dei Client connessi alla chat");
                            // Fornisce al Client la lista dei clienti attivi
                            for(int i = 0; i < Client.size(); ++ i) {
                                ThreadClient threadclient = Client.get(i);
                                messaggio((i + 1) + ") " + threadclient.NomeClient );
			    }
			break;
		    }
		}
		chiusura();
	    }
            // Classe per scrivere il messaggio
	    private boolean messaggio(String msg) {			
		if(!socket.isConnected()) {
                    chiusura();
                    return false;
		}
		try {
                    out.writeObject(msg);// Il messaggio viene inviato sulla chat
		}
		catch(IOException e) {}
		return true;
	    }
            // Classe che chiude input, output e socket
	    private void chiusura() {
		try {
                    if(out != null) {
                        out.close();
                    }
	        }
		catch(Exception e) {}
		try {
                    if(inp != null) {
                        inp.close();
                    }
                }
		catch(Exception e) {};
		try {
                    if(socket != null) {
                        socket.close();
                    }
		}
		catch (Exception e) {}
	    }
	}
}
