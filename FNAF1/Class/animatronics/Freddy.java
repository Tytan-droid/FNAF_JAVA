package Class.animatronics;

import Class.SoundManager;

public class Freddy extends abstrac_animatronic {

    public  Freddy(String id_room,int difficultie,int etape_mvt){
        super(id_room,difficultie,etape_mvt);
    }
    @Override
    public void kill(){
        if ((this.get_id_room().equals("Door_Left"))||this.get_id_room().equals("Door_Right")) {
            System.out.println("YOU ARE DEAD");
        }
    }
    @Override
    public void mvt_sound(){
        SoundManager.play("fnaf-freddys-laugh");
    }

}
