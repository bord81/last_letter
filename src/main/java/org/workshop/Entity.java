package org.workshop;

public interface Entity {
    void process(Event event, String payload, GameSide gameSide);

}