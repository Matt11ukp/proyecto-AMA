package unl.edu.ec.ama.engine.domain.entity;

import java.awt.*;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class EventRect extends Rectangle {
    private int eventRectDefaultX, eventRectDefaultY;
    private boolean eventDone;

    public EventRect(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.eventRectDefaultX = x;
        this.eventRectDefaultY = y;
        this.eventDone = false;
    }

    public int getEventRectDefaultX() {
        return eventRectDefaultX;
    }

    public int getEventRectDefaultY() {
        return eventRectDefaultY;
    }

    public boolean isEventDone() {
        return eventDone;
    }

    public void setEventDone(boolean eventDone) {
        this.eventDone = eventDone;
    }
}
