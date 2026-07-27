package unl.edu.ec.ama.engine.domain.entity.objects;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public enum Type {
    BOOTS(0),
    CHEST(1),
    DOOR(2),
    DOOR_OPEN(3),
    HEART(4),
    HALF_HEART(5),
    BLANK_HEART(6),
    KEY(7),
    PORTAL(8),
    TEST_PORTAL(9);

    private final int index;
    Type(int index) { this.index = index; }
    public int getIndex()        { return index; }
}
