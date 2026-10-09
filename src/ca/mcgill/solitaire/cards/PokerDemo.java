package ca.mcgill.solitaire.cards;

/** Run as a Java Application to demonstrate Exercise 12 without JavaFX. */
public final class PokerDemo {
    private PokerDemo() {}

    public static void main(String[] args) {
        Hand straightFlush = new Hand();
        for (Rank rank : new Rank[] {Rank.ACE, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE}) {
            straightFlush.add(Card.get(rank, Suit.HEARTS));
        }
        Hand fourOfAKind = new Hand();
        for (Suit suit : Suit.values()) {
            fourOfAKind.add(Card.get(Rank.KING, suit));
        }
        fourOfAKind.add(Card.get(Rank.TWO, Suit.CLUBS));
        System.out.println("Hand 1: " + straightFlush.cards());
        System.out.println("Category: " + straightFlush.getPokerHandType());
        System.out.println("Hand 2: " + fourOfAKind.cards());
        System.out.println("Category: " + fourOfAKind.getPokerHandType());
        System.out.println("Hand 1 is stronger: " + (straightFlush.comparePokerTo(fourOfAKind) > 0));
        System.out.println("Same categories tie; ace is low. Evaluation requires five cards.");
    }
}
