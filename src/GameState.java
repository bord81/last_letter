import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class GameState implements Entity {
    private final BlockingQueue<Message> queue;
    private final GameStateSaved gameStateSaved;
    private final Set<String> usedWords;
    private String prevWord;

    public GameState(BlockingQueue<Message> queue, GameStateSaved gameStateSaved) {
        this.queue = queue;
        this.gameStateSaved = gameStateSaved;
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
                if (sideOut == GameSide.StartGame) {
                    sideOut = GameSide.User;
                }
                break;
            case LoadState:
                loadSavedGamestate();
                sideOut = GameSide.LoadDone;
                break;
            default:
                break;
        }
        queue.add(new Message(eventOut, payload, sideOut));
    }

    private void loadSavedGamestate() {
        prevWord = gameStateSaved.getPrevWord();
        usedWords.addAll(gameStateSaved.getUsedWords());
        System.out.println("Last word is: " + prevWord);
    }

    private void saveGamestate() {
        gameStateSaved.saveState(prevWord, usedWords);
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
                    saveGamestate();
                }
                break;
            case User:
                if (payload.length() == 0) {
                    side = GameSide.DictionaryUserReject;
                } else {
                    prevWord = payload;
                    usedWords.add(payload);
                    saveGamestate();
                }
                break;
            case UserReject:
            case BotReject:
                usedWords.remove(payload);
            default:
                break;
        }
        queue.add(new Message(Event.Notify, payload, side));
    }

    private boolean wordAlreadyUsed(String word) {
        return usedWords.contains(word);
    }

    private boolean wordStartsFromWrongChar(String word) {
        return prevWord.length() != 0 && word.charAt(0) != prevWord.charAt(prevWord.length() - 1);
    }
}
