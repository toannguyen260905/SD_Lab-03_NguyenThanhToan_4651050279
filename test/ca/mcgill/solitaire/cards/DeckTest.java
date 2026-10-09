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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class DeckTest {
	private List<Card> drawAll(Deck pDeck) {
		List<Card> cards = new ArrayList<>();
		while (!pDeck.isEmpty()) {
			cards.add(pDeck.draw());
		}
		return cards;
	}

	@Test
	void configurationsAreCompleteDistinctAndRepeatable() {
		Set<List<Card>> orders = new HashSet<>();
		for (int configuration = 1; configuration <= 3; configuration++) {
			Deck deck = new Deck(configuration);
			List<Card> order = drawAll(deck);
			assertEquals(52, order.size());
			assertEquals(52, new HashSet<>(order).size());
			assertEquals(order, drawAll(new Deck(configuration)));
			deck.shuffle();
			assertEquals(order, drawAll(deck));
			orders.add(order);
		}
		assertEquals(3, orders.size());
		assertEquals(drawAll(new Deck(1)), drawAll(new Deck()));
	}

	@Test
	void validatesConfigurationAndArguments() {
		assertThrows(IllegalArgumentException.class, () -> new Deck(0));
		assertThrows(IllegalArgumentException.class, () -> new Deck(4));
		assertEquals(1, Deck.parseConfiguration(new String[0]));
		for (int configuration = 1; configuration <= 3; configuration++) {
			assertEquals(configuration, Deck.parseConfiguration(new String[] { "" + configuration }));
		}
		for (String value : new String[] { "0", "4", "abc", "", "-1" }) {
			assertThrows(IllegalArgumentException.class,
					() -> Deck.parseConfiguration(new String[] { value }));
		}
		assertThrows(IllegalArgumentException.class,
				() -> Deck.parseConfiguration(new String[] { "1", "2" }));
	}
	@Test
	void testdraw() {
		Deck lDeck = new Deck();
		lDeck.shuffle();
		Set<Card> lCards = new HashSet<Card>();
		for (int i = 0; i < 52; i++) {
			Card lCard = lDeck.draw();
			assertFalse(lCards.contains(lCard));
			lCards.add(lCard);
		}
		assertTrue(lDeck.isEmpty());
	}
}
