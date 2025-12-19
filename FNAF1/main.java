import Class.animatronics.*;

import java.util.List;

import Class.Rooms.*;

public class main {

    public static void main(String[] args) {

        L_animatronics L_a = new L_animatronics();
        L_a.L_animatronics_Builder_n1();

        Rooms_Graph rg = new Rooms_Graph();
        rg.Rooms_Graph_Builder();

        for (int x = 0; x < 20; x++) {
            L_a.move_all_animatronics(rg);
        }
    }
}