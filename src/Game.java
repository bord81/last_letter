public class Game implements Entity {
    private final Dispatcher dispatcher;

    public Game(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void process(Event event, String payload, GameSide gameSide) {
        switch (event) {
            case Notify:
                if (gameSide == GameSide.Bot || gameSide == GameSide.WordByUserUsedReject
                        || gameSide == GameSide.DictionaryUserReject || gameSide == GameSide.BotReject
                        || gameSide == GameSide.WordByUserUsedIncorrect || gameSide == GameSide.LoadDone) {
                    dispatcher.send(Event.RefreshUI, payload, gameSide);
                } else if (gameSide == GameSide.User || gameSide == GameSide.WordByBotUsedReject
                        || gameSide == GameSide.DictionaryBotReject || gameSide == GameSide.UserReject) {
                    dispatcher.send(Event.Ack, payload, gameSide);
                }
                break;
            case Reject:
                dispatcher.send(Event.Update, payload, gameSide);
                break;
            case Submit:
                dispatcher.send(Event.Validate, payload, gameSide);
                break;
            case LoadState:
                dispatcher.send(event, payload, gameSide);
            default:
                break;
        }
    }
}
