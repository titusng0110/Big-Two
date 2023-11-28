/**
 * A subclass of the Hand class and is used to model a hand of Flush in a Big
 * Two card game.
 */
public class Flush extends Hand {

	/**
	 * a constructor for building a flush with the specified player and list of
	 * cards.
	 * 
	 * @param player player who played the hand
	 * @param cards  cards the player played
	 */
	public Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	/**
	 * a method for checking if this is a valid flush
	 * 
	 * @return a boolean representing whether the hand is a valid flush
	 */
	@Override
	public boolean isValid() {
		// number of cards match
		if (!(this.size() == 5))
			return false;
		// card characteristic match
		int s = this.getCard(0).getSuit();
		for (int i = 1; i < 5; i++) {
			if (!(this.getCard(i).getSuit() == s))
				return false;
		}
		return true;
	}

	/**
	 * a method for returning a string specifying the type of this hand i.e. flush.
	 * 
	 * @return the type of this hand i.e. "Flush"
	 */
	@Override
	public String getType() {
		return "Flush";
	}
}
