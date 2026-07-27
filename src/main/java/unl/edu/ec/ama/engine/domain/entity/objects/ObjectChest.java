package unl.edu.ec.ama.engine.domain.entity.objects;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class ObjectChest extends Item {
    public ObjectChest(){
        this.setType(Type.CHEST);
        setCollision(true);
    }
}
