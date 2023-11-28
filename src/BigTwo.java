import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * The BigTwo class implements the CardGame interface and is used to model a Big
 * Two card game. It has private instance variables for storing the number of
 * players, a deck of cards, a list of players, a list of hands played on the
 * table, an index of the current player, and a user interface.
 */
public class BigTwo implements CardGame {
	/**
	 * The number of player in a Big Two game is 4.
	 */
	public final static int MAX_PLAYER_NUM = 4;
	/**
	 * The number of cards held per player in a Big Two game is 13.
	 */
	public final static int MAX_CARD_NUM = 13;
	private Deck deck;
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable;
	private int currentPlayerIdx;
	private BigTwoGUI gui;
	private BigTwoClient client;

	/**
	 * a constructor for creating a Big Two card game. It creates 4 players and add
	 * them to the player list; and creates a BigTwoUI object for providing the user
	 * interface.
	 */
	public BigTwo() {
		playerList = new ArrayList<CardGamePlayer>();
		handsOnTable = new ArrayList<Hand>();
		for (int i = 0; i < MAX_PLAYER_NUM; i++) {
			CardGamePlayer temp = new CardGamePlayer(null);
			playerList.add(temp);
		}
		gui = new BigTwoGUI(this);
		client = new BigTwoClient(this, gui);
		gui.setClient(client);
	}

	/**
	 * a method for getting the number of players.
	 * 
	 * @return an int specifying the number of players
	 */
	public int getNumOfPlayers() {
		int count = 0;
		for (int i = 0; i < MAX_PLAYER_NUM; i++)
			if (playerList.get(i).getName() != null)
				count++;
		return count;
	}

	/**
	 * a method for retrieving the deck of cards being used.
	 * 
	 * @return a deck of cards
	 */
	public Deck getDeck() {
		return this.deck;
	}

	/**
	 * a method for retrieving the list of players.
	 * 
	 * @return a list of players
	 */
	public ArrayList<CardGamePlayer> getPlayerList() {
		return this.playerList;
	}

	/**
	 * a method for retrieving the list of hands played on the table.
	 * 
	 * @return a list of hands played on the table
	 */
	public ArrayList<Hand> getHandsOnTable() {
		return this.handsOnTable;
	}

	/**
	 * a method for retrieving the index of the current player.
	 * 
	 * @return an integer specifying the index of the current player.
	 */
	public int getCurrentPlayerIdx() {
		return this.currentPlayerIdx;
	}

	/**
	 * a method for starting/restarting the game with a given shuffled deck of
	 * cards. It (i) remove all the cards from the players as well as from the
	 * table; (ii) distribute the cards to the players; (iii) identify the player
	 * who holds the Three of Diamonds; (iv) set both the currentPlayerIdx of the
	 * BigTwo object and the activePlayer of the BigTwoUI object to the index of the
	 * player who holds the Three of Diamonds; (v) call the repaint() method of the
	 * BigTwoUI object to show the cards on the table; and (vi) call the
	 * promptActivePlayer() method of the BigTwoUI object to prompt user to select
	 * cards and make his/her move.
	 * 
	 * @param deck given shuffled deck of cards
	 */
	public void start(Deck deck) {
		// remove all players' cards
		for (int i = 0; i < BigTwo.MAX_PLAYER_NUM; i++)
			playerList.get(i).removeAllCards();
		// remove all cards on table
		handsOnTable.clear();
		// distribute deck
		for (int i = 0; i < deck.size(); i++)
			playerList.get(i % MAX_PLAYER_NUM).addCard(deck.getCard(i));
		// identify player with Three of Diamonds and set as current and active
		for (int i = 0; i < MAX_PLAYER_NUM; i++)
			playerList.get(i).sortCardsInHand();
		for (int i = 0; i < MAX_PLAYER_NUM; i++)
			if (playerList.get(i).getCardsInHand().contains(new BigTwoCard(0, 2))) {
				currentPlayerIdx = i;
				gui.setActivePlayer(currentPlayerIdx);
			}
		gui.promptActivePlayer();
	}

	/**
	 * a method for making a move by a player with the specified index using the
	 * cards specified by the list of indices. This method should be called from the
	 * BigTwoUI after the active player has selected cards to make his/her move.
	 * 
	 * @param playerIdx an integer representing player ID
	 * @param cardIdx   an array of integers representing the indices of cards
	 *                  chosen
	 */
	public void makeMove(int playerIdx, int[] cardIdx) {
		client.sendMessage(new CardGameMessage(CardGameMessage.MOVE, playerIdx, cardIdx));
	}

	/**
	 * a method for checking a move made by a player. This method should be called
	 * from the makeMove() method.
	 * 
	 * @param playerIdx an integer representing player ID
	 * @param cardIdx   an array of integers representing the indices of cards
	 *                  chosen
	 */
	public synchronized void checkMove(int playerIdx, int[] cardIdx) {
		CardList cards = playerList.get(playerIdx).play(cardIdx);
		// player chose to pass
		if (cards == null) {
			// no hands on table: cannot pass
			if (handsOnTable.isEmpty()) {
				gui.printMsg("Not a legal move!!!");
			}
			// last hand on table is self: cannot pass
			else if (handsOnTable.get(handsOnTable.size() - 1).getPlayer() == playerList.get(playerIdx)) {
				gui.printMsg("Not a legal move!!!");
			}
			// pass
			else {
				currentPlayerIdx = (currentPlayerIdx + 1) % MAX_PLAYER_NUM;
				gui.setActivePlayer(currentPlayerIdx);
				gui.printMsg("{Pass}");
			}
		}
		// player chose a hand
		else {
			Hand hand = composeHand(playerList.get(playerIdx), cards);
			// hand is not valid
			if (hand == null) {
				gui.printMsg("Not a legal move!!!");
			}
			// last hand on table is others: need follow pattern and beat
			else if (!handsOnTable.isEmpty()
					&& handsOnTable.get(handsOnTable.size() - 1).getPlayer() != playerList.get(playerIdx)
					&& (handsOnTable.get(handsOnTable.size() - 1).size() != cards.size()
							|| !hand.beats(handsOnTable.get(handsOnTable.size() - 1)))) {
				gui.printMsg("Not a legal move!!!");
			} else {
				// remove cards from player
				playerList.get(playerIdx).removeCards((CardList) hand);
				// add hand to table
				handsOnTable.add(hand);
				currentPlayerIdx = (currentPlayerIdx + 1) % MAX_PLAYER_NUM;
				gui.setActivePlayer(currentPlayerIdx);
				gui.printMsg("{" + hand.getType() + "} " + hand.toString());
			}
		}
		gui.repaint();
		if (endOfGame()) {
			gui.disable();
			// end of game msg
			String eogMsg = "";
			eogMsg += "Game ends.\n";
			for (int i = 0; i < MAX_PLAYER_NUM; i++)
				if (playerList.get(i).getNumOfCards() == 0)
					eogMsg += playerList.get(i).getName() + " wins the game.\n";
				else
					eogMsg += playerList.get(i).getName() + " has "
							+ Integer.toString(playerList.get(i).getNumOfCards()) + " cards in hand.\n";
			JOptionPane.showMessageDialog(null, eogMsg);
			client.sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
		} else
			gui.promptActivePlayer();
	}

	/**
	 * a method for checking if the game ends.
	 * 
	 * @return a boolean representing whether the game ends
	 */
	public boolean endOfGame() {
		for (int i = 0; i < MAX_PLAYER_NUM; i++)
			if (playerList.get(i).getNumOfCards() == 0)
				return true;
		return false;
	}

	/**
	 * When a player leaves mid game, the game is sharp cut.
	 */
	public void sharpCut() {
		// remove all players' cards
		for (int i = 0; i < BigTwo.MAX_PLAYER_NUM; i++)
			playerList.get(i).removeAllCards();
		// remove all cards on table
		handsOnTable.clear();
		gui.printMsg("Game terminated.");
	}

	/**
	 * a method for starting a Big Two card game. It should (i) create a Big Two
	 * card game, (ii) create and shuffle a deck of cards, and (iii) start the game
	 * with the deck of cards.
	 */
	public static void main(String[] args) {
		new BigTwo();
	}

	/**
	 * a method for returning a valid hand from the specified list of cards of the
	 * player. Returns null if no valid hand can be composed from the specified list
	 * of cards.
	 * 
	 * @param player a CardGamePlayer object representing the player who plays the
	 *               hand
	 * @param cards  a CardList object representing the cards the player chose
	 * @return a type of Hand if it falls into any type. null if it is an invalid
	 *         hand
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		Single single = new Single(player, cards);
		if (single.isValid())
			return single;
		Pair pair = new Pair(player, cards);
		if (pair.isValid())
			return pair;
		Triple triple = new Triple(player, cards);
		if (triple.isValid())
			return triple;
		StraightFlush straightFlush = new StraightFlush(player, cards);
		if (straightFlush.isValid())
			return straightFlush;
		Quad quad = new Quad(player, cards);
		if (quad.isValid())
			return quad;
		FullHouse fullHouse = new FullHouse(player, cards);
		if (fullHouse.isValid())
			return fullHouse;
		Flush flush = new Flush(player, cards);
		if (flush.isValid())
			return flush;
		Straight straight = new Straight(player, cards);
		if (straight.isValid())
			return straight;
		return null;
	}

}
