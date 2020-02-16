import java.util.*;

public class Dispatcher {

    private final TriConsumer<StatePair, String, GameSide> controllerCB;
    private Stage currentStage;

    public Dispatcher(TriConsumer<StatePair, String, GameSide> controllerCB, Stage stageStart) {
        this.controllerCB = controllerCB;
        this.currentStage = stageStart;
    }
    void send(Event event, String payload, GameSide gameSide){
        List<StatePair> statePairs = new ArrayList<>();
        switch (currentStage) {
            case Bot:
            case User:
                statePairs.add(new StatePair(Event.Submit, Stage.Game));
                statePairs.add(new StatePair(Event.Reject, Stage.Game));
                statePairs.add(new StatePair(Event.LoadState, Stage.Game));
                processEvent(statePairs, event, payload, gameSide);
                break;
            case Dictionary:
                statePairs.add(new StatePair(Event.Update, Stage.GameState));
                processEvent(statePairs, event, payload, gameSide);
                break;
            case Game:
                statePairs.add(new StatePair(Event.Update, Stage.GameState));
                statePairs.add(new StatePair(Event.Validate, Stage.GameState));
                statePairs.add(new StatePair(Event.RefreshUI, Stage.User));
                statePairs.add(new StatePair(Event.Ack, Stage.Bot));
                statePairs.add(new StatePair(Event.LoadState, Stage.GameState));
                processEvent(statePairs, event, payload, gameSide);
                break;
            case GameState:
                statePairs.add(new StatePair(Event.Validate, Stage.Dictionary));
                statePairs.add(new StatePair(Event.Notify, Stage.Game));
                processEvent(statePairs, event, payload, gameSide);
                break;
            default:
                System.out.println("Controller.doAction Error: illegal event: "
                        + event);
                break;
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
