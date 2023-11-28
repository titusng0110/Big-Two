/**
 * A subclass of the Hand class and is used to model a hand of Single in a Big
 * Two card game.
 */
public class Single extends Hand {
	
	/**
	 * a constructor for building a single with the specified player and list of
	 * cards.
	 * 
	 * @param player player who played the hand
	 * @param cards  cards the player played
	 */
	public Single(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	/**
	 * a method for checking if this is a valid single
	 * 
	 * @return a boolean representing whether the hand is a valid single
	 */
	@Override
	public boolean isValid() {
		// number of cards match
		if (!(this.size() == 1))
			return false;
		// card characteristic match

		return true;
	}

	/**
	 * a method for returning a string specifying the type of this hand i.e. single.
	 * 
	 * @return the type of this hand i.e. "Single"
	 */
	@Override
	public String getType() {
		return "Single";
	}
}
