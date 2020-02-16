import java.io.*;
import java.nio.file.FileSystems;
import java.util.*;

public class GameStateSavedCSV implements GameStateSaved {
    private final Set<String> usedWords = new HashSet<>();
    private String prevWord = "";
    private File gameStateFile = new File(System.getProperty("user.dir")
            + FileSystems.getDefault().getSeparator() + "game_state.dat");

    @Override
    public String getPrevWord() {
        if (prevWord.length() == 0) {
            loadWords();
        }
        return prevWord;
    }

    @Override
    public Set<String> getUsedWords() {
        if (usedWords.size() == 0) {
            loadWords();
        }
        return usedWords;
    }

    @Override
    public void saveState(String word, Set<String> usedWords){
        if (!gameStateFile.exists()) {
            try {
                gameStateFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(gameStateFile))) {
            writer.write(word);
            writer.newLine();
            usedWords.forEach(s -> {
                try {
                    writer.write(s);
                    writer.write(",");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWords() {
        if (gameStateFile.canRead()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(gameStateFile));
                String line;
                line = reader.readLine();
                if (line !=null) {
                    prevWord = line;
                }
                line = reader.readLine();
                String[] words = line.split(",");
                Collections.addAll(usedWords, words);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // log error
        }
    }
}
