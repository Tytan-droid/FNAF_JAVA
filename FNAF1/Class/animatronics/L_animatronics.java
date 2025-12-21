package Class.animatronics;

import java.util.ArrayList;
import java.util.List;

import Class.rooms.Rooms_Graph;

public class L_animatronics {
    private List<abstrac_animatronic> la;

    public L_animatronics(){
        this.la= new ArrayList<>();
    }

    public List<abstrac_animatronic> get_L(){
        return this.la;
    }

    public void Add_animatronic(abstrac_animatronic animatronic){
        this.la.add(animatronic);
    }

    public void L_animatronics_Builder_n1(){
        this.L_animatronics_Builder(0, 3, 2, 2);
    }
    public void L_animatronics_Builder_n2(){
        this.L_animatronics_Builder(0, 6, 3, 3);
    }
    public void L_animatronics_Builder_n3(){
        this.L_animatronics_Builder(1, 3, 7, 4);
    }
    public void L_animatronics_Builder_n4(){
        this.L_animatronics_Builder(2, 5, 6, 5);
    }
    public void L_animatronics_Builder_n5(){
        this.L_animatronics_Builder(3, 5, 9, 7);
    }
    public void L_animatronics_Builder_n6(){
        this.L_animatronics_Builder(20, 20, 20, 20);
    }

    public void L_animatronics_Builder(int difficultie_freddy,int difficultie_bonnie,int difficultie_chica,int difficultie_foxy){
        Chica c = new Chica("CAM1A", difficultie_chica, 0);
        Bonnie b = new Bonnie("CAM1A", difficultie_bonnie, 0);
        Freddy f = new Freddy("CAM1A",difficultie_freddy,0);
        Foxy f2 = new Foxy("CAM1C", difficultie_foxy, 0);

        this.Add_animatronic(b);
        this.Add_animatronic(f);
        this.Add_animatronic(c);
        this.Add_animatronic(f2);
    }

    public void move_all_animatronics(Rooms_Graph rg){
        for (abstrac_animatronic animatronic : this.la) {
            animatronic.move(rg);
            animatronic.kill();
        }
    }
}
