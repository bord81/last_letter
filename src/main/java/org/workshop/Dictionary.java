package org.workshop;

import java.io.*;
import java.nio.file.FileSystems;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class Dictionary implements Entity, WordsSource {
    private final BlockingQueue<Message> queue;
    private boolean isReady;
    private final Map<Character, List<String>> words;
    private final SecureRandom random;

    public Dictionary(BlockingQueue<Message> queue) {
        this.queue = queue;
        this.words = new HashMap<>();
        this.random = new SecureRandom();
        isReady = false;
        File db = new File(System.getProperty("user.dir")
                + FileSystems.getDefault().getSeparator() + "words_alpha.txt");

        if (db.canRead()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(db));
                String line;
                while ((line = reader.readLine()) != null) {
                    String lineRead = line.toLowerCase();
                    Character first = lineRead.charAt(0);
                    words.compute(first, (character, strings) -> {
                        if (strings == null) {
                            List<String> newStrings = new ArrayList<>();
                            newStrings.add(lineRead);
                            return newStrings;
                        } else {
                            strings.add(lineRead);
                            return strings;
                        }
                    });
                }
                isReady = true;
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // log error
        }
    }

    @Override
    public void process(Event event, String payload, GameSide gameSide) {
        if (event == Event.Validate) {
            String result = "";
            if (isWordExist(payload)) {
                result = payload;
            }
            queue.add(new Message(Event.Update, result, gameSide));
        }
    }

    @Override
    public String getRandomWord(char letter) {
        List<String> wordsList = words.get(letter);
        return wordsList.get(random.nextInt(wordsList.size()));
    }

    boolean isReady() {
        return isReady;
    }

    private boolean isWordExist(String string) {
        return words.get(string.charAt(0)).contains(string);
    }
}
