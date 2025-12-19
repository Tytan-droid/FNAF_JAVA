package Class.animatronics;

import java.util.Random;

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
        if (this.get_etape_mvt()==4 && n<=this.get_difficultie() ){
            this.set_etape_mvt(0);
            abstrac_room r= rg.getRoom(this.get_id_room());
            int size = rg.getNeighbors(r).size();
            n = rand.nextInt(size);
            this.set_id_room(rg.getNeighbors(r).get(n).get_name());
        }else if(this.get_etape_mvt()<4){
            this.set_etape_mvt(this.get_etape_mvt()+1);
        }
    }

    public void kill(){
        if (this.get_id_room()=="You"){
            System.out.println("YOU ARE DEAD");
        }
    }
}
