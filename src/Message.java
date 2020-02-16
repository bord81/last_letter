public class Message {
    private final Event event;
    private final String payload;
    private final GameSide gameSide;

    public Event getEvent() {
        return event;
    }

    public String getPayload() {
        return payload;
    }

    public GameSide getGameSide() {
        return gameSide;
    }

    public Message(Event event, String payload, GameSide gameSide) {
        this.event = event;
        this.payload = payload;
        this.gameSide = gameSide;
    }
}
