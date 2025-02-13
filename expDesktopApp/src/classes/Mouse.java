package classes;

import java.io.Serializable;

public class Mouse implements Serializable {
    private static final long serialVersionUID = 2L;
    // a unique identifier
    private int id;
    // the mouse name
    private String name;

    public Mouse(int id) {
        this.id = id;
        // generate name
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
