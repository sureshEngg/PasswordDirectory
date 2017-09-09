package suresh.syp.saveurpasswa;

/**
 * Created by sureshsharma on 8/28/2017.
 */

public class Constant {
    public static final String DATABASENAME = "SYP_DB.db";
    public static final String DATABASELOCATION = "/data/data/com.example.sureshsharma.saveurpasswa/databases/"+DATABASENAME;
    public static final String PASSWORDS_TBL = "CREATE Table password_tbl (title TEXT, username TEXT, password TEXT, other TEXT, id TEXT PRIMARY KEY);";
    public static final String PASSWORD_TBL_NAME = "password_tbl";
    public static final String USER_TBL = "CREATE Table user_tbl (pin TEXT);";
    public static final String USER_TBL_NAME = "user_tbl";
}
