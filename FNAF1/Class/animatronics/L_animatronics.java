package Class.animatronics;

import java.util.ArrayList;
import java.util.List;

import Class.Rooms.Rooms_Graph;

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
        Chica c = new Chica("CAM1A", 3, 0);
        Bonnie b = new Bonnie("CAM1A", 3, 0);

        this.Add_animatronic(c);
        this.Add_animatronic(b);

    }

    public void move_all_animatronics(Rooms_Graph rg){
        for (abstrac_animatronic animatronic : this.la) {
            animatronic.move(rg);
            animatronic.kill();
        }
    }
}
