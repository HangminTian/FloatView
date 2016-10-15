package tian.hang.com.floatballdemo;

import java.util.List;
import java.util.Map;

/**
 * Created by thm on 2016/10/9.
 */
public class CollectionsUtil {
    public static boolean isNotEmpty(Object[] array) {
        if(array == null)
            return false;
        if(array.length == 0)
            return false;
        else
            return true;
    }

    public static boolean isNotEmpty(String str) {
        if(str == null)
            return false;
        if(str.equals(""))
            return false;
        else
            return true;
    }

    public static boolean isEmpty(String str) {
        return  !isNotEmpty(str);
    }

    public static boolean isEmpty(Object[] array) {
        return !isNotEmpty(array);
    }

    public static boolean isNotEmpty(List<?> list) {
        if(list == null)
            return false;
        if(list.size() == 0)
            return false;
        else
            return true;
    }

    public static boolean isEmpty(List<?> list) {
        return  !isNotEmpty(list);
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        if(map == null)
            return false;
        if(map.size() == 0)
            return false;
        else
            return true;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return !isNotEmpty(map);
    }
}
