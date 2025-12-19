package Class.Rooms;

public abstract class abstrac_room {
    private int id;
    private String name;
    
    public abstrac_room(int id,String name) {
        this.id = id;
        this.name=name;
    }

    public int get_id() {
        return this.id;
    }
    public String get_name(){
        return this.name;
    }
}
