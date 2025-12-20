package Class.animatronics;

import java.util.Random;

import Class.SoundManager;
import Class.Rooms.Rooms_Graph;
import Class.Rooms.abstrac_room;

public abstract class abstrac_animatronic {
    private String id_room;
    private int difficultie;
    private int etape_mvt;

    public abstrac_animatronic(String id_room,int difficultie,int etape_mvt){
        this.id_room =id_room;
        this.difficultie=difficultie;
        this.etape_mvt=etape_mvt;
    }

    public String get_id_room(){
        return this.id_room;
    }
    public int get_difficultie(){
        return this.difficultie;
    }    
    public int get_etape_mvt(){
        return this.etape_mvt;
    }
    public void set_etape_mvt(int new_etape_mvt){
        this.etape_mvt=new_etape_mvt;
    }
    public void set_id_room(String id_room){
        this.id_room=id_room;
    }

    public void move(Rooms_Graph rg){
        Random rand = new Random();
        int n = rand.nextInt(20) + 1;
        String id_room = this.get_id_room();
        if (this.get_etape_mvt()==4*60 && n<=this.get_difficultie() ){
            this.set_etape_mvt(0);
            abstrac_room r= rg.getRoom(this.get_id_room());
            int size = rg.getNeighbors(r).size();
            if(size>0){
                n = rand.nextInt(size);
                this.set_id_room(rg.getNeighbors(r).get(n).get_name());
            }
            n=rand.nextInt(100);
            if (n<=50+this.difficultie){
                this.set_id_room(rg.approch_you(r).get_name());
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

    public void kill(){
        if (this.get_id_room().equals("You")){
            System.out.println("YOU ARE DEAD");
        }
    }

    public void mvt_sound(){
        if(this.get_id_room().equals("CAM2B")||this.get_id_room().equals("CAM4B")){
            SoundManager.play("Deep_Steps");    
        }
    }
}
