package org.workshop;

import java.util.*;
import java.util.concurrent.BlockingQueue;

public class Dispatcher {
    private final BlockingQueue<Message> queue;
    private final TriConsumer<StatePair, String, GameSide> controllerCB;
    private Stage currentStage;
    private List<StatePair> botStatePairs;
    private List<StatePair> userStatePairs;
    private List<StatePair> dictStatePairs;
    private List<StatePair> gameStatePairs;
    private List<StatePair> gameStateStatePairs;

    public Dispatcher(BlockingQueue<Message> queue, TriConsumer<StatePair, String, GameSide> controllerCB, Stage stageStart) {
        this.queue = queue;
        this.controllerCB = controllerCB;
        this.currentStage = stageStart;
        botStatePairs = new ArrayList<>();
        userStatePairs = new ArrayList<>();
        dictStatePairs =  new ArrayList<>();
        gameStatePairs = new ArrayList<>();
        gameStateStatePairs = new ArrayList<>();

        botStatePairs.add(new StatePair(Event.Submit, Stage.Game));
        botStatePairs.add(new StatePair(Event.Reject, Stage.Game));

        userStatePairs.add(new StatePair(Event.Submit, Stage.Game));
        userStatePairs.add(new StatePair(Event.Reject, Stage.Game));
        userStatePairs.add(new StatePair(Event.LoadState, Stage.Game));

        dictStatePairs.add(new StatePair(Event.Update, Stage.GameState));

        gameStatePairs.add(new StatePair(Event.Update, Stage.GameState));
        gameStatePairs.add(new StatePair(Event.Validate, Stage.GameState));
        gameStatePairs.add(new StatePair(Event.RefreshUI, Stage.User));
        gameStatePairs.add(new StatePair(Event.Ack, Stage.Bot));
        gameStatePairs.add(new StatePair(Event.LoadState, Stage.GameState));

        gameStateStatePairs.add(new StatePair(Event.Validate, Stage.Dictionary));
        gameStateStatePairs.add(new StatePair(Event.Notify, Stage.Game));
    }

    private void send(Event event, String payload, GameSide gameSide){
        switch (currentStage) {
            case Bot:
                processEvent(botStatePairs, event, payload, gameSide);
                break;
            case User:
                processEvent(userStatePairs, event, payload, gameSide);
                break;
            case Dictionary:
                processEvent(dictStatePairs, event, payload, gameSide);
                break;
            case Game:
                processEvent(gameStatePairs, event, payload, gameSide);
                break;
            case GameState:
                processEvent(gameStateStatePairs, event, payload, gameSide);
                break;
            default:
                System.out.println("Controller.doAction Error: illegal event: "
                        + event);
                break;
        }
    }

    void startProcessing() {
        while (true) {
            try {
                Message nextMessage = queue.take();
                send(nextMessage.getEvent(), nextMessage.getPayload(), nextMessage.getGameSide());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processEvent(List<StatePair> statePairs, Event currentEvent, String payload, GameSide gameSide) {
        StatePair statePairFound = null;
        for (StatePair statePair : statePairs) {
            if (statePair.getEvent() == currentEvent) {
                statePairFound = statePair;
                break;
            }
        }
        if (statePairFound != null) {
            currentStage = statePairFound.getStage();
            controllerCB.accept(statePairFound, payload, gameSide);
        } else {
            System.out.println("Dispatcher.processEvent Error: illegal event "
            + currentEvent.toString() + " in stage " + currentStage.toString());
        }
    }
}
