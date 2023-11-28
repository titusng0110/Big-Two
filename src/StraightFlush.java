/**
 * A subclass of the Hand class and is used to model a hand of Straight Flush in
 * a Big Two card game.
 */
public class StraightFlush extends Hand {

	/**
	 * a constructor for building a straight flush with the specified player and
	 * list of cards.
	 * 
	 * @param player player who played the hand
	 * @param cards  cards the player played
	 */
	public StraightFlush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	/**
	 * a method for checking if this is a valid straight flush
	 * 
	 * @return a boolean representing whether the hand is a valid straight flush
	 */
	@Override
	public boolean isValid() {
		// number of cards match
		if (!(this.size() == 5))
			return false;
		// card characteristic match
		int r = (this.getCard(0).getRank() + 11) % 13;
		int s = this.getCard(0).getSuit();
		for (int i = 1; i < 5; i++) {
			if (!((this.getCard(i).getRank() + 11) % 13 == r + 1))
				return false;
			r++;
			if (!(this.getCard(i).getSuit() == s))
				return false;
		}
		return true;
	}

	/**
	 * a method for returning a string specifying the type of this hand i.e.
	 * straight flush.
	 * 
	 * @return the type of this hand i.e. "StraightFlush"
	 */
	@Override
	public String getType() {
		return "StraightFlush";
	}
}
