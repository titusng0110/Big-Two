/**
 * The BigTwoCard class is a subclass of the Card class and is used to model a
 * card used in a Big Two card game. It should override the compareTo() method
 * it inherits from the Card class to reflect the ordering of cards used in a
 * Big Two card game.
 */
public class BigTwoCard extends Card {

	/**
	 * a constructor for building a card with the specified suit and rank. suit is
	 * an integer between 0 and 3, and rank is an integer between 0 and 12.
	 * 
	 * @param suit suit of the card (0-3)
	 * @param rank rank of the card (0-12)
	 */
	public BigTwoCard(int suit, int rank) {
		super(suit, rank);
	}

	/**
	 * a method for comparing the order of this card with the specified card.
	 * 
	 * @param card card to compare to
	 * @return a negative integer, zero, or a positive integer when this card is
	 *         less than, equal to, or greater than the specified card.
	 */
	@Override
	public int compareTo(Card card) {
		if ((this.getRank() + 11) % 13 < (card.getRank() + 11) % 13)
			return -1;
		else if ((this.getRank() + 11) % 13 > (card.getRank() + 11) % 13)
			return 1;
		else if (this.getSuit() < card.getSuit())
			return -1;
		else if (this.getSuit() > card.getSuit())
			return 1;
		else
			return 0;
	}
}
