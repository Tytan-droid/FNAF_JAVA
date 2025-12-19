package Class.animatronics;

public abstract class abstrac_animatronic {
    private int id_room;
    private int difficultie;
    private int etape_mvt;

    public abstrac_animatronic(int id_room,int difficultie,int etape_mvt){
        this.id_room =id_room;
        this.difficultie=difficultie;
        this.etape_mvt=etape_mvt;
    }

    public int get_id_room(){
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
}
