package ca.mcgill.solitaire.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ca.mcgill.solitaire.auto.GreedyPlayingStrategy;

class WinnableGameTest {
	@Test
	void allConfigurationsWinAgainAfterUndoAndReset() {
		for (int configuration = 1; configuration <= 3; configuration++) {
			GameModel model = new GameModel(new GreedyPlayingStrategy(), configuration);
			for (int game = 0; game < 3; game++) {
				int moves = 0;
				while (!model.isCompleted() && moves < 200) {
					assertTrue(model.tryToAutoPlay(), "Stuck in configuration " + configuration);
					moves++;
				}
				assertTrue(model.isCompleted());
				assertEquals(52, model.getScore());
				model.undoLast();
				assertFalse(model.isCompleted());
				assertEquals(51, model.getScore());
				assertTrue(model.tryToAutoPlay());
				assertTrue(model.isCompleted());
				model.reset();
				assertEquals(0, model.getScore());
				assertFalse(model.canUndo());
			}
		}
	}
}
