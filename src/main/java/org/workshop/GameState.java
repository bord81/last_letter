package org.workshop;

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
    public void process(Event event, String payload, SubEvent subEvent) {
        SubEvent sideOut = subEvent;
        Event eventOut = Event.Notify;
        switch (event) {
            case Update:
                processUpdateEvent(event, payload, subEvent);
                return;
            case Validate:
                if (wordAlreadyUsed(payload)) {
                    if (subEvent == SubEvent.FromBot) {
                        sideOut = SubEvent.WordByBotUsedReject;
                    } else if (subEvent == SubEvent.FromUser) {
                        sideOut = SubEvent.WordByUserUsedReject;
                    } else {
                        System.out.println("GameState.process error: wordAlreadyUsed by incorrect side: " + subEvent);
                        System.exit(1);
                    }
                } else if (subEvent == SubEvent.FromUser && wordStartsFromWrongChar(payload)) {
                    sideOut = SubEvent.WordByUserUsedIncorrect;
                } else {
                    eventOut = Event.Validate;
                }
                if (sideOut == SubEvent.StartGame) {
                    sideOut = SubEvent.FromUser;
                }
                break;
            case LoadState:
                loadSavedGamestate();
                sideOut = SubEvent.LoadDone;
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


    private void processUpdateEvent(Event event, String payload, SubEvent subEvent) {
        SubEvent side = subEvent;
        switch (subEvent) {
            case FromBot:
                if (payload.length() == 0) {
                    side = SubEvent.DictionaryBotReject;
                } else {
                    prevWord = payload;
                    usedWords.add(payload);
                    saveGamestate();
                }
                break;
            case FromUser:
                if (payload.length() == 0) {
                    side = SubEvent.DictionaryUserReject;
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
