/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaggi;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author tommaso di giorno
 */
public class Messaggi implements Serializable {   
    public static final int partecipanti = 0;// Serve per far vedere ad un Client la lista dei Client connessi
    public static final int message = 1;// E' il messaggio che invia un singolo Client
    public static final int esci = 2;// E' il messaggio che il Client utilizza per disconnetersi dalla chat
    private int tipo;// Il tipo del messaggio
    private String messaggio;
        // Costruttore Messaggi
	public Messaggi(int type, String message) {        
            this.tipo = type;// Il tipo del messaggio
            this.messaggio = message;// Messaggio
	}
	public int getTipo() {
            return tipo;// Ritorna il tipo del messaggio
        }
	public String getMessaggio() { 
            return messaggio;// Ritorna il messaggio
        }
}
