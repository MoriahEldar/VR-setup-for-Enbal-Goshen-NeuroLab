package consts;

import java.text.Format;
import java.text.SimpleDateFormat;

public class Formats {
    public static String experimentNameFormat(String name) { //TODO ask!
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(new java.util.Date());
        return "exp_" + date + "_" + name;
    }
}
