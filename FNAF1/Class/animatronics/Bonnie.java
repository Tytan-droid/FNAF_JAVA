package Class.animatronics;

import java.util.Random;

import Class.Rooms.Rooms_Graph;
import Class.Rooms.abstrac_room;

public class Bonnie extends abstrac_animatronic {
    
    public  Bonnie(String id_room,int difficultie,int etape_mvt){
        super(id_room,difficultie,etape_mvt);
    }
    @Override
    public void move(Rooms_Graph rg){
        Random rand = new Random();
        int n = rand.nextInt(20) + 1;
        String id_room=this.get_id_room();
        if (this.get_etape_mvt()==4*60 && n<=this.get_difficultie() ){
            this.set_etape_mvt(0);
            abstrac_room r= rg.getRoom(this.get_id_room());
            int size = rg.getNeighbors(r).size();
            n = rand.nextInt(size);
            this.set_id_room(rg.getNeighbors(r).get(n).get_name());
            n=rand.nextInt(100);
            if (n<=50+this.get_difficultie()){
                this.set_id_room(rg.approch_you_left(r).get_name());
            }
            if(id_room.equals("Door_Right") && !rg.getNeighbors(r).contains(rg.getRoom("You"))){
                this.set_id_room("CAM1B");
            }
            if(id_room.equals("Door_Left") && !rg.getNeighbors(r).contains(rg.getRoom("You"))){
                this.set_id_room("CAM1B");
            }
            this.mvt_sound();
        }else if(this.get_etape_mvt()<4*60){
            this.set_etape_mvt(this.get_etape_mvt()+1);
        }else{
            this.set_etape_mvt(0);
        }
    }
}
