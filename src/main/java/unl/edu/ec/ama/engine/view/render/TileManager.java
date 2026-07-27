package unl.edu.ec.ama.engine.view.render;

import unl.edu.ec.ama.engine.view.util.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];

    public TileManager(GamePanel gp){
        this.gp = gp;
        tile = new Tile[10]; // 10 tipos de tiles distintos
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap("/maps/tiles.txt");
    }

    public void getTileImage(){
        setup(0, "grass", false);
        setup(1, "water", true);
        setup(2, "wood", true);
        setup(3, "earth", false);
        setup(4, "tree", true);
        setup(5, "sand", false);
    }

    public void setup(int index, String imagePath, boolean collision){
        try {
            var stream = getClass().getResourceAsStream("/tiles/" + imagePath + ".png");
            if (stream == null) {
                throw new IOException("Archivo no encontrado: " + imagePath);
            }
            BufferedImage original = ImageIO.read(stream);
            BufferedImage scaled = UtilityTool.scaleImage(original, gp.tileSize, gp.tileSize);
            tile[index] = new Tile(scaled, collision);
        } catch (IOException e) {
            System.out.println("¡EXCEPCIÓN ATRAPADA!");
            System.out.println("Error al cargar la imagen: " + imagePath);
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath){
        try {
            InputStream is = getClass().getResourceAsStream(filePath); // leer el mapa
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;
            // pa que no se salga de los limites de la pantalla
            while(col < gp.maxWorldCol && row < gp.maxWorldRow){
                String line = br.readLine(); // para leer una linea de texto y almacenarlo en la variable

                while(col < gp.maxWorldCol){
                    String numbers[] = line.split(" "); // poner un espacio entre cada valor leido
                    int num = Integer.parseInt(numbers[col]); // cambias de string a integer para poder leerlo como numero
                    mapTileNum[col][row] = num; //guardamos el numero extraido aqui
                    col++;
                }
                if(col == gp.maxWorldCol){
                    col = 0;
                    row++;
                }
            }
            br.close(); // cerrar el bufferer reader
        } catch (Exception e) {
            e.printStackTrace();// para que imprima en consola si existe algun error
        }
    }
    public void draw(Graphics2D g2){
        // representan los indices de las matrices, los tiles
        int worldCol = 0;
        int worldRow = 0;

        while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow){

            int TileNum = mapTileNum[worldCol][worldRow];  //extrae un numero y lo asigna

            // Posicion en el mapa
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;

            // 1. CÁLCULO DE CÁMARA (¿Dónde se dibuja en la pantalla?)
            int screenX = worldX - gp.player.getWorldX() + gp.player.getScreenX();
            int screenY = worldY - gp.player.getWorldY() + gp.player.getScreenY();

            int rightOffSett = gp.screenWidth - gp.player.getScreenX();
            int bottomOffSett = gp.screenHeight - gp.player.getScreenY();

            // Congelar cámara en los bordes
            if(gp.player.getScreenX() > gp.player.getWorldX()){ screenX = worldX; }
            if(gp.player.getScreenY() > gp.player.getWorldY()){ screenY = worldY; }
            if(rightOffSett > gp.maxWorldCol * gp.tileSize - gp.player.getWorldX()){
                screenX = gp.screenWidth - (gp.maxWorldCol * gp.tileSize - worldX);
            }
            if(bottomOffSett > gp.maxWorldRow * gp.tileSize - gp.player.getWorldY()){
                screenY = gp.screenHeight - (gp.maxWorldRow * gp.tileSize - worldY);
            }

            int cameraLeftEdge = gp.player.getWorldX() - gp.player.getScreenX();
            int cameraTopEdge = gp.player.getWorldY() - gp.player.getScreenY();
            int cameraRightEdge = cameraLeftEdge + gp.screenWidth;
            int cameraBottomEdge = cameraTopEdge + gp.screenHeight;

            if(gp.player.getScreenX() > gp.player.getWorldX()) {
                cameraLeftEdge = 0;
                cameraRightEdge = gp.screenWidth;
            }
            if(gp.player.getScreenY() > gp.player.getWorldY()) {
                cameraTopEdge = 0;
                cameraBottomEdge = gp.screenHeight;
            }
            if(rightOffSett > gp.maxWorldCol * gp.tileSize - gp.player.getWorldX()) {
                cameraRightEdge = gp.maxWorldCol * gp.tileSize;
                cameraLeftEdge = cameraRightEdge - gp.screenWidth;
            }
            if(bottomOffSett > gp.maxWorldRow * gp.tileSize - gp.player.getWorldY()) {
                cameraBottomEdge = gp.maxWorldRow * gp.tileSize;
                cameraTopEdge = cameraBottomEdge - gp.screenHeight;
            }

            if(worldX + gp.tileSize > cameraLeftEdge &&
                    worldX - gp.tileSize < cameraRightEdge &&
                    worldY + gp.tileSize > cameraTopEdge &&
                    worldY - gp.tileSize < cameraBottomEdge) {

                g2.drawImage(tile[TileNum].getImage(), screenX, screenY, gp.tileSize, gp.tileSize, null);
            }

            worldCol++;

            if(worldCol == gp.maxWorldCol){
                worldCol = 0;
                worldRow++;
            }
        }
    }
}
