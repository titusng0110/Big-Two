/**
 * A subclass of the Hand class and is used to model a hand of Triple in a Big
 * Two card game.
 */
public class Triple extends Hand {

	/**
	 * a constructor for building a triple with the specified player and list of
	 * cards.
	 * 
	 * @param player player who played the hand
	 * @param cards  cards the player played
	 */
	public Triple(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	/**
	 * a method for checking if this is a valid triple
	 * 
	 * @return a boolean representing whether the hand is a valid triple
	 */
	@Override
	public boolean isValid() {
		// number of cards match
		if (!(this.size() == 3))
			return false;
		// card characteristic match
		if (!(this.getCard(0).getRank() == this.getCard(1).getRank()
				&& this.getCard(1).getRank() == this.getCard(2).getRank()))
			return false;
		return true;
	}

	/**
	 * a method for returning a string specifying the type of this hand i.e. triple.
	 * 
	 * @return the type of this hand i.e. "Triple"
	 */
	@Override
	public String getType() {
		return "Triple";
	}
}
