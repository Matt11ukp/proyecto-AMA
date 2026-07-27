package unl.edu.ec.ama.engine.domain.user;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public interface IAvatarConfig {
    int  getActualSkin();   void setActualSkin(int v);
    int  getActualHair();   void setActualHair(int v);
    int  getActualShirt();  void setActualShirt(int v);
    int  getActualEye();    void setActualEye(int v);
    boolean isGender();     void setGender(boolean v);
}
