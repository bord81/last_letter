package org.workshop;

import java.util.Set;

public interface GameStateSaved {
    String getPrevWord();

    Set<String> getUsedWords();

    void saveState(String word, Set<String> usedWords);
}
