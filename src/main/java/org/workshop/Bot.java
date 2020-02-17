package org.workshop;

import java.util.concurrent.BlockingQueue;

public class Bot implements Entity {
    private final BlockingQueue<Message> queue;
    private final WordsSource wordsSource;
    private String prevWord;

    Bot(BlockingQueue<Message> queue, WordsSource wordsSource) {
        this.queue = queue;
        this.wordsSource = wordsSource;
        prevWord = "";
    }

    @Override
    public void process(Event event, String payload, SubEvent subEvent) {
        if (event == Event.Ack) {
            String answer;
            if (subEvent == SubEvent.UserReject) {
                answer =
                        wordsSource.getRandomWord(prevWord.toLowerCase().charAt(prevWord.length() - 1));
            } else {
                prevWord = payload;
                answer =
                        wordsSource.getRandomWord(payload.toLowerCase().charAt(payload.length() - 1));
            }
            queue.add(new Message(Event.Submit, answer, SubEvent.FromBot));
        }
    }
}