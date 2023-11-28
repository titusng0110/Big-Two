/**
 * A subclass of the Hand class and is used to model a hand of Pair in a Big Two
 * card game.
 */
public class Pair extends Hand {
	
	/**
	 * a constructor for building a pair with the specified player and list of
	 * cards.
	 * 
	 * @param player player who played the hand
	 * @param cards  cards the player played
	 */
	public Pair(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	/**
	 * a method for checking if this is a valid pair
	 * 
	 * @return a boolean representing whether the hand is a valid pair
	 */
	@Override
	public boolean isValid() {
		// number of cards match
		if (!(this.size() == 2))
			return false;
		// card characteristic match
		if (!(this.getCard(0).getRank() == this.getCard(1).getRank()))
			return false;
		return true;
	}

	/**
	 * a method for returning a string specifying the type of this hand i.e. pair.
	 * 
	 * @return the type of this hand i.e. "Pair"
	 */
	@Override
	public String getType() {
		return "Pair";
	}
}
