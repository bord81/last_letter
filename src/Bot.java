public class Bot implements Entity {
    private final Dispatcher dispatcher;
    private final WordsSource wordsSource;
    private String prevWord;

    Bot(Dispatcher dispatcher, WordsSource wordsSource) {
        this.dispatcher = dispatcher;
        this.wordsSource = wordsSource;
        prevWord = "";
    }

    @Override
    public void process(Event event, String payload, GameSide gameSide) {
        if (event == Event.Ack) {
            String answer;
            if (gameSide == GameSide.UserReject) {
                answer =
                        wordsSource.getRandomWord(prevWord.toLowerCase().charAt(prevWord.length() - 1));
            } else {
                prevWord = payload;
                answer =
                        wordsSource.getRandomWord(payload.toLowerCase().charAt(payload.length() - 1));
            }
            dispatcher.send(Event.Submit, answer, GameSide.Bot);
        }
    }
}