package ca.mcgill.solitaire.cards;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class HandTest {
    private Hand hand(int... ranks) {
        Hand hand = new Hand(13);
        int[] used = new int[13];
        for (int rank : ranks) {
            int suit = (used[rank]++ + hand.size()) % 4;
            Card card = Card.get(Rank.values()[rank], Suit.values()[suit]);
            while (hand.contains(card)) {
                suit = (suit + 1) % 4;
                card = Card.get(Rank.values()[rank], Suit.values()[suit]);
            }
            hand.add(card);
        }
        return hand;
    }

    private Hand suited(int... ranks) {
        Hand hand = new Hand();
        for (int rank : ranks) {
            hand.add(Card.get(Rank.values()[rank], Suit.CLUBS));
        }
        return hand;
    }

    @Test
    void recognizesEveryCategoryAndStrengthOrder() {
        Hand[] hands = {
            hand(0, 2, 5, 8, 12), hand(2, 2, 5, 8, 12),
            hand(2, 2, 5, 5, 12), hand(2, 2, 2, 8, 12),
            hand(1, 2, 3, 4, 5), suited(0, 2, 5, 8, 12),
            hand(2, 2, 2, 8, 8), hand(2, 2, 2, 2, 12),
            suited(1, 2, 3, 4, 5)
        };
        for (int i = 0; i < hands.length; i++) {
            assertEquals(PokerHandType.values()[i], hands[i].getPokerHandType());
            assertEquals(0, hands[i].comparePokerTo(hands[i]));
            for (int j = 0; j < hands.length; j++) {
                assertEquals(Integer.signum(i - j),
                    Integer.signum(Hand.pokerComparator().compare(hands[i], hands[j])));
            }
        }
    }

    @Test
    void aceIsLowAndCardRanksDoNotBreakTies() {
        assertEquals(PokerHandType.STRAIGHT, hand(4, 0, 3, 1, 2).getPokerHandType());
        assertEquals(PokerHandType.STRAIGHT_FLUSH, suited(0, 1, 2, 3, 4).getPokerHandType());
        assertEquals(PokerHandType.HIGH_CARD, hand(9, 10, 11, 12, 0).getPokerHandType());
        assertEquals(PokerHandType.FLUSH, suited(9, 10, 11, 12, 0).getPokerHandType());
        assertEquals(0, hand(1, 1, 1, 5, 9).comparePokerTo(hand(12, 12, 12, 3, 7)));
    }

    @Test
    void rejectsIncompleteAndOversizedPokerHands() {
        for (int size : new int[] {0, 1, 4, 6}) {
            Hand hand = new Hand(13);
            for (int i = 0; i < size; i++) {
                hand.add(Card.get(Rank.values()[i], Suit.CLUBS));
            }
            assertThrows(IllegalStateException.class, hand::getPokerHandType);
            assertThrows(IllegalStateException.class,
                () -> hand.comparePokerTo(suited(0, 1, 2, 3, 4)));
            assertThrows(IllegalStateException.class,
                () -> suited(0, 1, 2, 3, 4).comparePokerTo(hand));
        }
    }

    @Test
    void enforcesCapacityUniquenessAndEncapsulation() {
        assertThrows(IllegalArgumentException.class, () -> new Hand(0));
        assertThrows(IllegalArgumentException.class, () -> new Hand(-1));
        Hand hand = new Hand(1);
        Card ace = Card.get(Rank.ACE, Suit.CLUBS);
        assertTrue(hand.isEmpty());
        hand.add(ace);
        assertEquals(1, hand.size());
        assertTrue(hand.isFull());
        assertTrue(hand.contains(ace));
        assertThrows(IllegalArgumentException.class, () -> hand.add(ace));
        assertThrows(IllegalStateException.class,
            () -> hand.add(Card.get(Rank.TWO, Suit.CLUBS)));
        assertThrows(NullPointerException.class, () -> hand.add(null));
        assertThrows(NullPointerException.class, () -> hand.remove(null));
        assertThrows(NullPointerException.class, () -> hand.contains(null));
        assertThrows(NullPointerException.class, () -> hand.comparePokerTo(null));
        assertThrows(UnsupportedOperationException.class, () -> hand.cards().clear());
        var iterator = hand.iterator();
        assertSame(ace, iterator.next());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        var snapshot = hand.cards();
        assertTrue(hand.remove(ace));
        assertFalse(hand.remove(ace));
        assertFalse(hand.contains(ace));
        assertTrue(hand.isEmpty());
        assertEquals(1, snapshot.size());
    }
}
