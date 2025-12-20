package Class.animatronics;

public class Freddy extends abstrac_animatronic {

    public  Freddy(String id_room,int difficultie,int etape_mvt){
        super(id_room,difficultie,etape_mvt);
    }
    @Override
    public void kill(){
        if ((this.get_id_room().equals("CAM2B"))||this.get_id_room().equals("CAM4B")) {
            System.out.println("YOU ARE DEAD");
        }
    }
    @Override
    public void mvt_sound(){
        System.out.println("Freddy move");
    }

}
