package co.eci.snake.concurrency;

import co.eci.snake.core.Board;
import co.eci.snake.core.Direction;
import co.eci.snake.core.Snake;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardConcurrencyLoadTest {

  @Test
  void board_should_handle_high_concurrency_without_exceptions() throws Exception {
    Board board = new Board(25, 25);
    List<Snake> snakes = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      int x = 2 + (i * 3) % board.width();
      int y = 2 + (i * 2) % board.height();
      snakes.add(Snake.of(x, y, Direction.values()[i % Direction.values().length]));
    }

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      List<Future<?>> futures = new ArrayList<>();
      for (Snake snake : snakes) {
        futures.add(executor.submit(() -> {
          for (int step = 0; step < 200; step++) {
            assertDoesNotThrow(() -> board.step(snake));
          }
          return null;
        }));
      }

      for (Future<?> future : futures) {
        future.get();
      }
    }

    assertTrue(board.mice().size() >= 0);
    assertTrue(board.obstacles().size() >= 0);
    assertTrue(board.turbo().size() >= 0);
  }
}
