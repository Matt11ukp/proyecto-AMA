package unl.edu.ec.ama.engine.domain.entity.objects;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class ObjectKey extends Item {

    public ObjectKey(){
        this.setType(Type.KEY);
        setCollision(false);
    }

}
