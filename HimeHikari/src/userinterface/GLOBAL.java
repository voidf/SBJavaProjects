package userinterface;

public class GLOBAL{
    public static int WIDTH = 1360,HEIGHT = 765;
    public static int relativeSizeX(float scale){return (int)(WIDTH*scale);}
    public static int relativeSizeY(float scale){return (int)(HEIGHT*scale);}
    public static int lerp(int lower, int upper, float percentage){
        return (int)(lower + (upper-lower) * percentage);
    }
    public static float lerp(float lower, float upper, float percentage){
        return lower + (upper-lower) * percentage;
    }
}