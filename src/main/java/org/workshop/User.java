package org.workshop;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class User implements Entity {
    private final BlockingQueue<Message> queue;
    private final Scanner scanner;

    public User(BlockingQueue<Message> queue) {
        this.queue = queue;
        scanner = new Scanner(System.in);
    }

    @Override
    public void process(Event event, String payload, SubEvent subEvent) {
        if (event == Event.RefreshUI) {
            String input = "";
            Event eventOut = Event.Submit;
            SubEvent sideOut = SubEvent.FromUser;
            if (subEvent == SubEvent.DictionaryUserReject || subEvent == SubEvent.BotReject
                    || subEvent == SubEvent.WordByUserUsedReject
                    || subEvent == SubEvent.WordByUserUsedIncorrect) {
                askOnReject(subEvent);
                input = askForInput();
            } else if (subEvent == SubEvent.StartGame) {
                if (askToLoadPrevious()) {
                    sideOut = subEvent;
                    eventOut = Event.LoadState;
                } else {
                    input = askForInput();
                }
            } else if (subEvent == SubEvent.LoadDone) {
                input = askForInput();
            } else {
                if (askIfWordIsAccepted(payload)) {
                    input = askForInput();
                } else {
                    input = payload;
                    eventOut = Event.Reject;
                    sideOut = SubEvent.UserReject;
                }
            }
            queue.add(new Message(eventOut, input, sideOut));
        }
    }

    private String askForInput() {
        System.out.println("Enter your word: ");
        return scanner.next().toLowerCase();
    }

    private boolean askIfWordIsAccepted(String word) {
        System.out.println("The word is: " + word);
        System.out.println("Do you accept?(y/N)");
        return getAnswer();
    }

    private void askOnReject(SubEvent side) {
        if (side == SubEvent.BotReject) {
            System.out.println("Word rejected by bot. Try another one.");
        } else if (side == SubEvent.DictionaryUserReject) {
            System.out.println("Word rejected by dictionary. Try another one.");
        } else if (side == SubEvent.WordByUserUsedReject) {
            System.out.println("Word was already used. Try another one.");
        } else if (side == SubEvent.WordByUserUsedIncorrect) {
            System.out.println("Word starts from the wrong letter. Try another one.");
        }
    }

    private boolean askToLoadPrevious() {
        System.out.println("Load previous game?(y/N)");
        return getAnswer();
    }

    private boolean getAnswer() {
        boolean accept = false;
        while (true) {
            String choice = scanner.next();
            if (choice.toLowerCase().charAt(0) == 'y') {
                accept = true;
                break;
            } else if (choice.toLowerCase().charAt(0) == 'n') {
                break;
            }
        }
        return accept;
    }
}
