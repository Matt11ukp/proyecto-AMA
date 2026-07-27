package unl.edu.ec.ama.engine.domain.world;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public record WorldEntry(String kind, int col, int row, String extra) {

    public WorldEntry(String kind, int col, int row) {
        this(kind, col, row, "");
    }

    public boolean hasExtra() {
        return extra != null && !extra.isBlank();
    }
}
