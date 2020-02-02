public class Controller {
    private final Bot bot;
    private final Dictionary dictionary;
    private final Game game;
    private final GameState gameState;
    private final User user;

    Controller() {
        Dispatcher dispatcher = new Dispatcher(this::doAction, Stage.User);
        dictionary = new Dictionary(dispatcher);
        if (!dictionary.isReady()) {
            System.out.println("Controller error: dictionary not ready.");
            System.exit(1);
        }
        bot = new Bot(dispatcher, dictionary);
        game = new Game(dispatcher);
        gameState = new GameState(dispatcher);
        user = new User(dispatcher);
    }

    public void startGame() {
        user.process(Event.RefreshUI, "", GameSide.StartGame);
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
