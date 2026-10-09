/*******************************************************************************
 * Solitaire
 * 
 * Copyright (C) 2025 by Martin P. Robillard
 * 
 * See: https://github.com/prmr/Solitaire
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/.
 *******************************************************************************/
package ca.mcgill.solitaire.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents 52 cards in one of three predetermined, winnable orders.
 */
public class Deck {
	private CardStack aCards;
	private final int aConfiguration;
	public static final int CONFIGURATION_COUNT = 3;

	/**
	 * Creates a deck using configuration 1.
	 */
	public Deck() {
		this(1);
	}

	/** Creates a deterministic deck. @param pConfiguration a number from 1 to 3. */
	public Deck(int pConfiguration) {
		if (pConfiguration < 1 || pConfiguration > CONFIGURATION_COUNT) {
			throw new IllegalArgumentException("Configuration must be 1, 2, or 3.");
		}
		aConfiguration = pConfiguration;
		shuffle();
	}

	/** Parses the optional configuration argument shared by GUI and driver. */
	public static int parseConfiguration(String[] pArgs) {
		if (pArgs.length == 0) {
			return 1;
		}
		if (pArgs.length == 1 && pArgs[0].matches("[1-3]")) {
			return Integer.parseInt(pArgs[0]);
		}
		throw new IllegalArgumentException("Usage: Solitaire [1|2|3] (default: 1)");
	}

	/**
	 * Restores the selected order. The historical method name is retained for
	 * compatibility; this method no longer randomizes the cards.
	 */
	public void shuffle() {
		List<Card> cards = new ArrayList<>();
		Suit[] suits = Suit.values();
		for (Rank rank : Rank.values()) {
			for (int i = 0; i < suits.length; i++) {
				cards.add(Card.get(rank, suits[(i + aConfiguration - 1) % suits.length]));
			}
		}
		// Tableau.initialize deals whole columns of sizes 1..7. Reverse each
		// block so its smallest card is exposed first and larger cards lie below.
		int offset = 0;
		for (int size = 1; size <= 7; size++) {
			Collections.reverse(cards.subList(offset, offset + size));
			offset += size;
		}
		// CardStack pops from the end: store the reverse of the draw order.
		Collections.reverse(cards);
		aCards = new CardStack(cards);
	}

	/**
	 * Places pCard on top of the deck.
	 * 
	 * @param pCard The card to place on top of the deck.
	 * @pre pCard !=null
	 */
	public void push(Card pCard) {
		assert pCard != null;
		aCards.push(pCard);
	}

	/**
	 * Draws a card from the deck and removes the card from the deck.
	 * 
	 * @return The card drawn.
	 * @pre !isEmpty()
	 */
	public Card draw() {
		assert !isEmpty();
		return aCards.pop();
	}

	/**
	 * @return True iff there are no cards in the deck.
	 */
	public boolean isEmpty() {
		return aCards.isEmpty();
	}
}
