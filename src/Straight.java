/**
 * A subclass of the Hand class and is used to model a hand of Straight in a Big
 * Two card game.
 */
public class Straight extends Hand {

	/**
	 * a constructor for building a straight with the specified player and list of
	 * cards.
	 * 
	 * @param player player who played the hand
	 * @param cards  cards the player played
	 */
	public Straight(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	/**
	 * a method for checking if this is a valid straight
	 * 
	 * @return a boolean representing whether the hand is a valid straight
	 */
	@Override
	public boolean isValid() {
		// number of cards match
		if (!(this.size() == 5))
			return false;
		// card characteristic match
		int r = (this.getCard(0).getRank() + 11) % 13;
		for (int i = 1; i < 5; i++) {
			if (!((this.getCard(i).getRank() + 11) % 13 == r + 1))
				return false;
			r++;
		}
		return true;
	}

	/**
	 * a method for returning a string specifying the type of this hand i.e.
	 * straight.
	 * 
	 * @return the type of this hand i.e. "Straight"
	 */
	@Override
	public String getType() {
		return "Straight";
	}

}
