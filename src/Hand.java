import java.util.Arrays;

/**
 * The Hand class is a subclass of the CardList class and is used to model a
 * hand of cards. It has a private instance variable for storing the player who
 * plays this hand. It also has methods for getting the player of this hand,
 * checking if it is a valid hand, getting the type of this hand, getting the
 * top card of this hand, and checking if it beats a specified hand.
 */
public abstract class Hand extends CardList {
	private CardGamePlayer player;

	/**
	 * a constructor for building a hand with the specified player and list of
	 * cards.
	 * 
	 * @param player player who played the hand
	 * @param cards  cards the player played
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		this.player = player;
		for (int i = 0; i < cards.size(); i++)
			this.addCard(cards.getCard(i));
		this.sort();
	}

	/**
	 * a method for retrieving the player of this hand.
	 * 
	 * @return player who played the hand
	 */
	public CardGamePlayer getPlayer() {
		return this.player;
	}

	/**
	 * a method for retrieving the top card of this hand.
	 * 
	 * @return a method for retrieving the top card of this hand.
	 */
	public Card getTopCard() {
		if (getType() == "Single" || getType() == "Pair" || getType() == "Triple" || getType() == "Straight"
				|| getType() == "Flush" || getType() == "StraightFlush")
			return this.getCard(this.size() - 1);
		else if (getType() == "Quad") {
			if (getCard(3).getRank() != getCard(4).getRank())
				return getCard(3);
			else
				return getCard(4);
		} else if (getType() == "FullHouse") {
			if (getCard(2).getRank() != getCard(3).getRank())
				return getCard(2);
			else
				return getCard(4);
		}
		return null;
	}

	/**
	 * a method for checking if this hand beats a specified hand.
	 * 
	 * @param hand hand to check against
	 * @return a boolean representing whether this hand beats the specified hand
	 */
	public boolean beats(Hand hand) {
		String[] patternRank = new String[] { "Straight", "Flush", "FullHouse", "Quad", "StraightFlush" };
		if (this.size() == 5 && hand.size() == 5) {
			if (Arrays.asList(patternRank).indexOf(this.getType()) > Arrays.asList(patternRank).indexOf(hand.getType()))
				return true;
			else
				return this.getTopCard().compareTo(hand.getTopCard()) > 0;
		} else
			return this.getTopCard().compareTo(hand.getTopCard()) > 0;
	}

	/**
	 * a method for checking if this is a valid hand.
	 * 
	 * @return a boolean representing whether this hand is valid
	 */
	public abstract boolean isValid();

	/**
	 * a method for returning a string specifying the type of this hand.
	 * 
	 * @return the type of this hand
	 */
	public abstract String getType();
}
