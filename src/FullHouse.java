/**
 * A subclass of the Hand class and is used to model a hand of Full House in a
 * Big Two card game.
 */
public class FullHouse extends Hand {

	/**
	 * a constructor for building a full house with the specified player and list of
	 * cards.
	 * 
	 * @param player player who played the hand
	 * @param cards  cards the player played
	 */
	public FullHouse(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	/**
	 * a method for checking if this is a valid full house
	 * 
	 * @return a boolean representing whether the hand is a valid full house
	 */
	@Override
	public boolean isValid() {
		// number of cards match
		if (!(this.size() == 5))
			return false;
		// card characteristic match
		if (this.getCard(0).getRank() == this.getCard(1).getRank()
				&& this.getCard(1).getRank() == this.getCard(2).getRank()
				&& this.getCard(3).getRank() == this.getCard(4).getRank())
			return true;
		if (this.getCard(2).getRank() == this.getCard(3).getRank()
				&& this.getCard(3).getRank() == this.getCard(4).getRank()
				&& this.getCard(0).getRank() == this.getCard(1).getRank())
			return true;
		return false;
	}

	/**
	 * a method for returning a string specifying the type of this hand i.e. full
	 * house.
	 * 
	 * @return the type of this hand i.e. "FullHouse"
	 */
	@Override
	public String getType() {
		return "FullHouse";
	}

}
