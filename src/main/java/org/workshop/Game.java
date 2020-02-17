package org.workshop;

import java.util.concurrent.BlockingQueue;

public class Game implements Entity {
    private final BlockingQueue<Message> queue;

    public Game(BlockingQueue<Message> queue) {
        this.queue = queue;
    }

    @Override
    public void process(Event event, String payload, GameSide gameSide) {
        switch (event) {
            case Notify:
                if (gameSide == GameSide.Bot || gameSide == GameSide.WordByUserUsedReject
                        || gameSide == GameSide.DictionaryUserReject || gameSide == GameSide.BotReject
                        || gameSide == GameSide.WordByUserUsedIncorrect || gameSide == GameSide.LoadDone) {
                    queue.add(new Message(Event.RefreshUI, payload, gameSide));
                } else if (gameSide == GameSide.User || gameSide == GameSide.WordByBotUsedReject
                        || gameSide == GameSide.DictionaryBotReject || gameSide == GameSide.UserReject) {
                    queue.add(new Message(Event.Ack, payload, gameSide));
                }
                break;
            case Reject:
                queue.add(new Message(Event.Update, payload, gameSide));
                break;
            case Submit:
                queue.add(new Message(Event.Validate, payload, gameSide));
                break;
            case LoadState:
                queue.add(new Message(event, payload, gameSide));
            default:
                break;
        }
    }
}
