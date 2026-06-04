package co.eci.snake.concurrency;

import co.eci.snake.core.Board;
import co.eci.snake.core.Direction;
import co.eci.snake.core.Snake;
import co.eci.snake.core.engine.GameClock;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public final class SnakeRunner implements Runnable {
  private final Snake snake;
  private final Board board;
  private final GameClock clock;
  private final int baseSleepMs = 80;
  private final int turboSleepMs = 40;
  private int turboTicks = 0;
  private final AtomicInteger obstacleHits = new AtomicInteger();
  private volatile int deathOrder = -1;
  private static final AtomicInteger NEXT_DEATH_ORDER = new AtomicInteger(1);

  public SnakeRunner(Snake snake, Board board, GameClock clock) {
    this.snake = snake;
    this.board = board;
    this.clock = clock;
  }

  @Override
  public void run() {
    try {
      while (!Thread.currentThread().isInterrupted()) {
        if (!clock.isRunning()) {
          clock.awaitRunning();
          if (Thread.currentThread().isInterrupted()) break;
          continue;
        }

        maybeTurn();
        var res = board.step(snake);
        if (res == Board.MoveResult.HIT_OBSTACLE) {
          if (obstacleHits.incrementAndGet() >= 3) {
            deathOrder = NEXT_DEATH_ORDER.getAndIncrement();
            break;
          }
          randomTurn();
        } else if (res == Board.MoveResult.ATE_TURBO) {
          turboTicks = 100;
        }
        int sleep = (turboTicks > 0) ? turboSleepMs : baseSleepMs;
        if (turboTicks > 0) turboTicks--;
        Thread.sleep(sleep);
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }

  public int collisionCount() { return obstacleHits.get(); }
  public boolean isDead() { return deathOrder >= 0; }
  public int deathOrder() { return deathOrder; }
  public int length() { return snake.snapshot().size(); }

  private void maybeTurn() {
    double p = (turboTicks > 0) ? 0.05 : 0.10;
    if (ThreadLocalRandom.current().nextDouble() < p) randomTurn();
  }

  private void randomTurn() {
    var dirs = Direction.values();
    snake.turn(dirs[ThreadLocalRandom.current().nextInt(dirs.length)]);
  }
}
