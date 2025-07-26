package userinterface;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.FileInputStream;

public class Fonts {
    public static Font wakuwaku;
    public static Font teki;
    public static Font BMJUA;
    public static Font RationalInteger;
    public static Font Noto;
    private static boolean is_init = false;
    public static void init(){
        if (is_init) return;
        else is_init = true;
        wakuwaku = loadFont("Assets/font/mini-wakuwaku.otf");
        teki = loadFont("Assets/font/nishiki-teki.ttf");
        BMJUA = loadFont("Assets/font/BMJUA_ttf.ttf");
        RationalInteger = loadFont("Assets/font/RationalInteger.ttf");
        Noto = loadFont("Assets/font/NotoSansCJKsc-Black.otf");
    }

    public static Font resizeFont(Font f, int size){
        return f.deriveFont(Font.BOLD, size);
    }

    @Nullable
    public static Font loadFont(String path)  {
        try {
            FileInputStream getfont = new FileInputStream(path);
            Font act = Font.createFont(Font.TRUETYPE_FONT, getfont);
//            Font font = act.deriveFont(Font.BOLD, size);
            getfont.close();
            return act;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}