package org.workshop;

import java.util.concurrent.BlockingQueue;

public class Game implements Entity {
    private final BlockingQueue<Message> queue;

    public Game(BlockingQueue<Message> queue) {
        this.queue = queue;
    }

    @Override
    public void process(Event event, String payload, SubEvent subEvent) {
        switch (event) {
            case Notify:
                if (subEvent == SubEvent.FromBot || subEvent == SubEvent.WordByUserUsedReject
                        || subEvent == SubEvent.DictionaryUserReject || subEvent == SubEvent.BotReject
                        || subEvent == SubEvent.WordByUserUsedIncorrect || subEvent == SubEvent.LoadDone) {
                    queue.add(new Message(Event.RefreshUI, payload, subEvent));
                } else if (subEvent == SubEvent.FromUser || subEvent == SubEvent.WordByBotUsedReject
                        || subEvent == SubEvent.DictionaryBotReject || subEvent == SubEvent.UserReject) {
                    queue.add(new Message(Event.Ack, payload, subEvent));
                }
                break;
            case Reject:
                queue.add(new Message(Event.Update, payload, subEvent));
                break;
            case Submit:
                queue.add(new Message(Event.Validate, payload, subEvent));
                break;
            case LoadState:
                queue.add(new Message(event, payload, subEvent));
            default:
                break;
        }
    }
}
