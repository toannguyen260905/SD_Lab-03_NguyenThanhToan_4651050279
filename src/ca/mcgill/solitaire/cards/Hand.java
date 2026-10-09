package ca.mcgill.solitaire.cards;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A hand of distinct cards with a configurable capacity.
 * Poker evaluation implements the two simplifications in Chapter 3, Exercise 12:
 * ace is low and hands of the same category tie.
 */
public final class Hand implements Iterable<Card> {
    private final int capacity;
    private final List<Card> cards = new ArrayList<>();

    /** Creates an empty five-card hand. */
    public Hand() { this(5); }

    /** @throws IllegalArgumentException if the capacity is not positive */
    public Hand(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }
        this.capacity = capacity;
    }

    /**
     * Adds a non-null, distinct card.
     * @throws IllegalArgumentException if the card already belongs to this hand
     * @throws IllegalStateException if this hand is full
     */
    public void add(Card card) {
        Objects.requireNonNull(card, "card");
        if (cards.contains(card)) {
            throw new IllegalArgumentException("Duplicate card: " + card);
        }
        if (isFull()) { throw new IllegalStateException("Hand is full."); }
        cards.add(card);
    }

    /** Removes a non-null card, returning whether it was present. */
    public boolean remove(Card card) {
        return cards.remove(Objects.requireNonNull(card, "card"));
    }

    public boolean contains(Card card) {
        return cards.contains(Objects.requireNonNull(card, "card"));
    }

    public boolean isEmpty() { return cards.isEmpty(); }
    public int size() { return cards.size(); }
    public boolean isFull() { return size() == capacity; }

    /** Returns an immutable snapshot so clients cannot modify internal storage. */
    public List<Card> cards() { return List.copyOf(cards); }

    @Override
    public Iterator<Card> iterator() { return cards().iterator(); }

    /**
     * Evaluates this hand. Ace-Two-Three-Four-Five is a straight;
     * Ten-Jack-Queen-King-Ace is not.
     * @throws IllegalStateException if the hand does not contain exactly five cards
     */
    public PokerHandType getPokerHandType() {
        if (size() != 5) {
            throw new IllegalStateException("Poker evaluation requires exactly five cards.");
        }
        int[] counts = new int[Rank.values().length];
        boolean flush = true;
        for (Card card : cards) {
            counts[card.rank().ordinal()]++;
            flush &= card.suit() == cards.get(0).suit();
        }
        int pairs = 0;
        int triples = 0;
        int fours = 0;
        int distinct = 0;
        int first = -1;
        int last = -1;
        for (int rank = 0; rank < counts.length; rank++) {
            if (counts[rank] > 0) {
                distinct++;
                if (first == -1) { first = rank; }
                last = rank;
            }
            if (counts[rank] == 2) { pairs++; }
            if (counts[rank] == 3) { triples++; }
            if (counts[rank] == 4) { fours++; }
        }
        boolean straight = distinct == 5 && last - first == 4;
        if (straight && flush) { return PokerHandType.STRAIGHT_FLUSH; }
        if (fours == 1) { return PokerHandType.FOUR_OF_A_KIND; }
        if (triples == 1 && pairs == 1) { return PokerHandType.FULL_HOUSE; }
        if (flush) { return PokerHandType.FLUSH; }
        if (straight) { return PokerHandType.STRAIGHT; }
        if (triples == 1) { return PokerHandType.THREE_OF_A_KIND; }
        if (pairs == 2) { return PokerHandType.TWO_PAIR; }
        if (pairs == 1) { return PokerHandType.ONE_PAIR; }
        return PokerHandType.HIGH_CARD;
    }

    /** Negative means weaker, zero means tied, positive means stronger. */
    public int comparePokerTo(Hand other) {
        Objects.requireNonNull(other, "other");
        return getPokerHandType().compareTo(other.getPokerHandType());
    }

    /** Returns a comparator ordering valid poker hands from weakest to strongest. */
    public static Comparator<Hand> pokerComparator() { return Hand::comparePokerTo; }
}
