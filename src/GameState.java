import java.util.HashSet;
import java.util.Set;

public class GameState implements Entity {
    private final Dispatcher dispatcher;
    private final Set<String> usedWords;
    private String prevWord;

    public GameState(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.usedWords = new HashSet<>();
        prevWord = "";
    }

    @Override
    public void process(Event event, String payload, GameSide gameSide) {
        GameSide sideOut = gameSide;
        Event eventOut = Event.Notify;
        switch (event) {
            case Update:
                processUpdateEvent(event, payload, gameSide);
                return;
            case Validate:
                if (wordAlreadyUsed(payload)) {
                    if (gameSide == GameSide.Bot) {
                        sideOut = GameSide.WordByBotUsedReject;
                    } else if (gameSide == GameSide.User) {
                        sideOut = GameSide.WordByUserUsedReject;
                    } else {
                        System.out.println("GameState.process error: wordAlreadyUsed by incorrect side: " + gameSide);
                        System.exit(1);
                    }
                } else if (gameSide == GameSide.User && wordStartsFromWrongChar(payload)) {
                    sideOut = GameSide.WordByUserUsedIncorrect;
                } else {
                    eventOut = Event.Validate;
                }
                break;
            default:
                break;
        }
        dispatcher.send(eventOut, payload, sideOut);
    }

    private void processUpdateEvent(Event event, String payload, GameSide gameSide) {
        GameSide side = gameSide;
        switch (gameSide) {
            case Bot:
                if (payload.length() == 0) {
                    side = GameSide.DictionaryBotReject;
                } else {
                    prevWord = payload;
                    usedWords.add(payload);
                }
                break;
            case User:
                if (payload.length() == 0) {
                    side = GameSide.DictionaryUserReject;
                } else {
                    prevWord = payload;
                    usedWords.add(payload);
                }
                break;
            case UserReject:
            case BotReject:
            default:
                break;
        }
        dispatcher.send(Event.Notify, payload, side);
    }

    private boolean wordAlreadyUsed(String word) {
        return usedWords.contains(word);
    }

    private boolean wordStartsFromWrongChar(String word) {
        return prevWord.length() != 0 && word.charAt(0) != prevWord.charAt(prevWord.length() - 1);
    }
}
