package cn.kq.indexedlistview;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * Created by zhaopan on 16/1/23 14:13
 * e-mail: kangqiao610@gmail.com
 */
public class StringUtil {

    private static final String DEFAULT_ALPHABET = "#";

    public static boolean isTrimEmpty(String str){
        return null == str || str.trim().length() == 0 ;
    }

    public static boolean isCaseAtoZ(char c) {
        return 'A' <= c && c <= 'Z';
    }

    public static String getFirstLetter(String str) {
        return getFirstLetter(str, DEFAULT_ALPHABET, true);
    }

    public static String getFirstLetter(String str, String def) {
        return getFirstLetter(str, def, true);
    }

    public static String getFirstLetter(String content, String def, boolean useDef) {
        //如果默认的为空, 取定义的.
        if (isTrimEmpty(def)) {
            def = DEFAULT_ALPHABET;
        }

        //如果content是空的直接用默认的.
        if (isTrimEmpty(content)) {
            return def;
        }

        if(content.equals(def)){
            return def;
        }

        //转换首字母.
        content = content.trim().toUpperCase();
        char firstChar = content.charAt(0);
        String firstStr = String.valueOf(firstChar);

        //判断是否是英文的A-Z
        if (isCaseAtoZ(firstChar)) {
            return firstStr;
        }

        //转换成中文的A-Z
        if (firstStr.matches("[\\u4e00-\\u9fa5]")) {
            // 擦，Pinyin4j.jar里有个io流没关！！！！！！！
            String[] strings = PinyinHelper.toHanyuPinyinStringArray(firstChar);
            if (strings.length > 0) {
                return String.valueOf(strings[0].charAt(0)).toUpperCase();
            }
        }

        //不是英文, 也不是中文, 是否用默认的.
        if (useDef) {
            return def;
        }

        //还是选用原生的首字符吧.
        return firstStr;
    }
}
