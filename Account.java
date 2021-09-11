import java.io.Serializable;

public class Account implements Serializable {
    private static int ID = 0;
    private String id;
    private String password;
    private Step[] steps;

}
