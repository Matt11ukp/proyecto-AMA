package unl.edu.ec.ama.engine.domain.entity.objects;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */


public class ObjectDoor extends Item {
    public ObjectDoor(){
        this.setType(Type.DOOR);
        setCollision(true);
    }
}
