package org.workshop;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Controller {
    private final Bot bot;
    private final Dictionary dictionary;
    private final Game game;
    private final GameState gameState;
    private final User user;
    private final BlockingQueue<Message> queue;
    private final Dispatcher dispatcher;

    Controller() {
        queue = new LinkedBlockingQueue<>();
        dispatcher = new Dispatcher(queue, this::doAction, Stage.User);
        dictionary = new Dictionary(queue);
        if (!dictionary.isReady()) {
            System.out.println("Controller error: dictionary not ready.");
            System.exit(1);
        }
        bot = new Bot(queue, dictionary);
        game = new Game(queue);
        gameState = new GameState(queue, new GameStateSavedCSV());
        user = new User(queue);
    }

    public void startGame() {
        user.process(Event.RefreshUI, "", GameSide.StartGame);
        dispatcher.startProcessing();
    }

    void doAction(StatePair pair, String payload, GameSide gameSide) {
        switch (pair.getStage()) {
            case Bot:
                bot.process(pair.getEvent(), payload, gameSide);
                break;
            case Dictionary:
                dictionary.process(pair.getEvent(), payload, gameSide);
                break;
            case Game:
                game.process(pair.getEvent(), payload, gameSide);
                break;
            case GameState:
                gameState.process(pair.getEvent(), payload, gameSide);
                break;
            case User:
                user.process(pair.getEvent(), payload, gameSide);
                break;
            default:
                System.out.println("Controller.doAction Error: illegal stage: "
                        + pair.getStage().toString());
                break;
        }
    }
}
