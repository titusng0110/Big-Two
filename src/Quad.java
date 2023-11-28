/**
 * A subclass of the Hand class and is used to model a hand of Quad in a Big Two
 * card game.
 */
public class Quad extends Hand {

	/**
	 * a constructor for building a quad with the specified player and list of
	 * cards.
	 * 
	 * @param player player who played the hand
	 * @param cards  cards the player played
	 */
	public Quad(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	/**
	 * a method for checking if this is a valid quad
	 * 
	 * @return a boolean representing whether the hand is a valid quad
	 */
	@Override
	public boolean isValid() {
		// number of cards match
		if (!(this.size() == 5))
			return false;
		// card characteristic match
		if (this.getCard(0).getRank() == this.getCard(1).getRank()
				&& this.getCard(1).getRank() == this.getCard(2).getRank()
				&& this.getCard(2).getRank() == this.getCard(3).getRank())
			return true;
		if (this.getCard(1).getRank() == this.getCard(2).getRank()
				&& this.getCard(2).getRank() == this.getCard(3).getRank()
				&& this.getCard(3).getRank() == this.getCard(4).getRank())
			return true;
		return false;
	}

	/**
	 * a method for returning a string specifying the type of this hand i.e. quad.
	 * 
	 * @return the type of this hand i.e. "Quad"
	 */
	@Override
	public String getType() {
		return "Quad";
	}

}
