import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * The BigTwoClient class implements the NetworkGame interface. It is used to
 * model a Big Two game client that is responsible for establishing a connection
 * and communicating with the Big Two game server.
 */
public class BigTwoClient implements NetworkGame {
	private BigTwo game;
	private BigTwoGUI gui;
	private ArrayList<CardGamePlayer> playerList;
	private Socket sock;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Thread t;
	private int playerID;
	private String playerName;
	private String serverIP;
	private int serverPort;

	/**
	 * a Constructor for creating a Big Two client.
	 * 
	 * @param game reference to a BigTwo object associated with this client
	 * @param gui  reference to a BigTwoGUI object associated with the BigTwo object
	 */
	public BigTwoClient(BigTwo game, BigTwoGUI gui) {
		this.game = game;
		this.gui = gui;
		this.playerList = game.getPlayerList();
		this.playerName = JOptionPane.showInputDialog("Your name:");
		if (this.playerName == null || this.playerName.isBlank())
			this.playerName = "Unnamed";
		this.serverIP = "127.0.0.1";
		this.serverPort = 2396;
		connect();
	}

	/**
	 * a method for getting the playerID (i.e., index) of the local player
	 * 
	 * @return ID of player
	 */
	@Override
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * a method for setting the playerID (i.e., index) of the local player.
	 * 
	 * @param playerID ID of player
	 */
	@Override
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	/**
	 * a method for getting the name of the local player.
	 * 
	 * @return name of player
	 */
	@Override
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * a method for setting the name of the local player.
	 * 
	 * @param playerName name of player
	 */
	@Override
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	/**
	 * a method for getting the IP address of the game server.
	 * 
	 * @return ip address
	 */
	@Override
	public String getServerIP() {
		return serverIP;
	}

	/**
	 * a method for setting the IP address of the game server.
	 * 
	 * @param serverIP ip address
	 */
	@Override
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	/**
	 * a method for getting the TCP port of the game server.
	 * 
	 * @return server port number
	 */
	@Override
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * a method for getting the TCP port of the game server.
	 * 
	 * @param serverPort server port number
	 */
	@Override
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * a method for making a socket connection with the game server. Upon successful
	 * connection, it should (i) create an ObjectOutputStream for sending messages
	 * to the game server; (ii) create a new thread for receiving messages from the
	 * game server.
	 */
	@Override
	public void connect() {
		if (sock == null || sock.isClosed()) {
			try {
				sock = new Socket(serverIP, serverPort);
				ois = new ObjectInputStream(sock.getInputStream());
				oos = new ObjectOutputStream(sock.getOutputStream());
				t = new Thread(new ServerHandler());
				t.start();
			} catch (UnknownHostException e) {
				gui.printMsg("Cannot connect to server.");
				e.printStackTrace();
			} catch (IOException e) {
				gui.printMsg("Cannot connect to server.");
				e.printStackTrace();
			}
		} else
			gui.printMsg("You are already connected.");
	}

	/**
	 * a method for parsing the messages received from the game server. This method
	 * should be called from the thread responsible for receiving messages from the
	 * game server. Based on the message type, different actions will be carried out
	 * (please refer to the general behavior of the client described in the previous
	 * section).
	 * 
	 * @param message message from server
	 */
	@Override
	public void parseMessage(GameMessage message) {
		if (message.getType() == CardGameMessage.PLAYER_LIST) {
			gui.printMsg("Connected to server at " + serverIP + ':' + Integer.toString(serverPort));
			setPlayerID(message.getPlayerID());
			String[] playerNames = (String[]) message.getData();
			for (int i = 0; i < BigTwo.MAX_PLAYER_NUM; i++)
				playerList.get(i).setName(playerNames[i]);
			sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, playerName));
		} else if (message.getType() == CardGameMessage.JOIN) {
			playerList.get(message.getPlayerID()).setName((String) message.getData());
			if (message.getPlayerID() == playerID)
				sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
			else
				gui.printMsg(playerList.get(message.getPlayerID()).getName() + " joined the game.");
			gui.repaint();
		} else if (message.getType() == CardGameMessage.FULL) {
			gui.printMsg("The server is full and cannot join the game");
			try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (message.getType() == CardGameMessage.QUIT) {
			gui.printMsg(playerList.get(message.getPlayerID()).getName() + " quited the game.");
			playerList.get(message.getPlayerID()).setName(null);
			gui.disable();
			if (!game.endOfGame()) {
				game.sharpCut();
				sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
			}
			gui.repaint();
		} else if (message.getType() == CardGameMessage.READY) {
			gui.printMsg(playerList.get(message.getPlayerID()).getName() + " is ready.");
		} else if (message.getType() == CardGameMessage.START) {
			gui.printMsg("All players are ready. Game starts.");
			game.start((Deck) message.getData());
		} else if (message.getType() == CardGameMessage.MOVE) {
			game.checkMove(message.getPlayerID(), (int[]) message.getData());
		} else if (message.getType() == CardGameMessage.MSG) {
			gui.printChat((String) message.getData());
		}
	}

	/**
	 * a method for sending the specified message to the game server. This method
	 * should be called whenever the client wants to communicate with the game
	 * server or other clients.
	 * 
	 * @param message message to send
	 */
	@Override
	public void sendMessage(GameMessage message) {
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			gui.printMsg("Cannot connect to server.");
			e.printStackTrace();
		}
	}

	private class ServerHandler implements Runnable {

		@Override
		public void run() {
			try {
				while (sock != null && !sock.isClosed()) {
					CardGameMessage message = (CardGameMessage) ois.readObject();
					if (message != null)
						parseMessage(message);
				}
			} catch (ClassNotFoundException e) {
				gui.printMsg("Cannot connect to server.");
				e.printStackTrace();
			} catch (IOException e) {
				gui.printMsg("Cannot connect to server.");
				e.printStackTrace();
			}
		}

	}
}
