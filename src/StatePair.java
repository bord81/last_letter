class StatePair {
    private final Event event;
    private final Stage stage;

    StatePair(Event event, Stage stage) {
        this.event = event;
        this.stage = stage;
    }

    Event getEvent() {
        return event;
    }

    Stage getStage() {
        return stage;
    }
}
