import Class.animatronics.Chica;

public class main {

    public static void main(String[] args) {
        System.out.println(sayHi());
        Chica c=new Chica(1,1,0);
        System.out.println(c.get_id_room());
        System.out.println(c.get_difficultie());
        System.out.println(c.get_etape_mvt());
        c.set_etape_mvt(4);
        System.out.println(c.get_etape_mvt());
    }
    private static String sayHi(){
        return "Hi";
    }
}