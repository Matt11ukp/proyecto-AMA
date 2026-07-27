package unl.edu.ec.ama.engine.domain.world;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class WorldLoader {

    private static final Logger LOG = Logger.getLogger(WorldLoader.class.getName());
    private static final String WORLD_FILE = "/maps/world.txt";

    public List<WorldEntry> load() {
        List<WorldEntry> entries = new ArrayList<>();

        try (InputStream is = getClass().getResourceAsStream(WORLD_FILE)) {
            if (is == null) {
                LOG.warning("No se encontró el archivo de mundo: " + WORLD_FILE);
                return Collections.emptyList();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    WorldEntry entry = parseLine(line.trim(), lineNumber);
                    if (entry != null) entries.add(entry);
                }
            }

        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error leyendo " + WORLD_FILE, e);
        }

        return Collections.unmodifiableList(entries);
    }

    private WorldEntry parseLine(String line, int lineNumber) {
        if (line.isEmpty() || line.startsWith("#")) return null;

        String[] tokens = line.split("\\s+");

        if (tokens.length < 3) {
            LOG.warning(String.format(
                "world.txt línea %d: formato inválido '%s' (se esperan al menos 3 tokens: TIPO COL ROW)",
                lineNumber, line));
            return null;
        }

        String kind  = tokens[0].toUpperCase();
        int    col   = parseNonNegativeInt(tokens[1], lineNumber, "COL");
        int    row   = parseNonNegativeInt(tokens[2], lineNumber, "ROW");

        if (col < 0 || row < 0) return null; // parseNonNegativeInt ya logueó el error

        String extra = tokens.length >= 4 ? tokens[3] : "";

        return new WorldEntry(kind, col, row, extra);
    }

    private int parseNonNegativeInt(String token, int lineNumber, String fieldName) {
        try {
            int value = Integer.parseInt(token);
            if (value < 0) {
                LOG.warning(String.format(
                    "world.txt línea %d: %s no puede ser negativo (valor: %d)",
                    lineNumber, fieldName, value));
                return -1;
            }
            return value;
        } catch (NumberFormatException e) {
            LOG.warning(String.format(
                "world.txt línea %d: %s no es un número válido '%s'",
                lineNumber, fieldName, token));
            return -1;
        }
    }
}
