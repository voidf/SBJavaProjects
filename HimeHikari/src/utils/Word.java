package utils;

import javax.swing.*;
import java.io.*;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Word {
    public static HashMap wordmono = new HashMap();
    public static Object[] wordmonoKeys;


    public static void loadFile(File src) throws Exception {
        BufferedReader f = new BufferedReader(new FileReader(src));
        String p = "(.*)";
        var pat = Pattern.compile("\\s*(\\S*)\\s*?(\\S.*?)$");

        while (f.ready()) {
            var tmp = (new StringBuffer(f.readLine()));
            Matcher m = pat.matcher(tmp);
            if (m.find()) {
                var k = m.group(1);
                var v = m.group(2);
                wordmono.put(k, v);
//            String[] separated = s.split("([^\n].*?) *?([^\n].*?)");
//                System.out.println(separated);
            }
        }
        wordmonoKeys = wordmono.keySet().toArray();
        System.out.println("done");
    }

    public static void askFile() {
        var jf = new JFileChooser(".");
        jf.setDialogTitle("导入第三方词库...");
        var res = jf.showSaveDialog(null);
        jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jf.setFileHidingEnabled(false);
        if (res == JFileChooser.APPROVE_OPTION) {
            File sel = jf.getSelectedFile();
            try {
                loadFile(sel);
                System.out.printf("导入完成，现在词库有%d个单词\n", wordmono.size());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("导入失败");
            }
        }

    }

    public enum C_E {
        word,
        translate
    }

    public static EnumMap<C_E, Object> randomWord() {
        Random r = new Random();
        var k = wordmonoKeys[r.nextInt(wordmonoKeys.length)];
        var v = wordmono.get(k);
        var mp = new EnumMap<C_E, Object>(C_E.class);
        mp.put(C_E.word, k);
        mp.put(C_E.translate, v);
        return mp;
    }

    public static void initialize(){
        File cet6src = utils.Utils.getResource("Assets/cet6.txt");
        System.out.println(cet6src);
        try {
            loadFile(cet6src);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("默认初始化失败，请检查Assets/cet6.txt是否存在");
        }
    }

    public static void recordAC(String w){
        try {
            var f = new FileWriter("AC.txt",true);
            f.write(w+"\n");
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void recordWA(String w){
        try {
            var f = new FileWriter("WA.txt",true);
            f.write(w+"\n");
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initialize();
//        askFile();
        System.out.println(randomWord().get(C_E.word));
        System.out.println(randomWord());
        System.out.println(randomWord());
        System.out.println(randomWord());

    }
}
