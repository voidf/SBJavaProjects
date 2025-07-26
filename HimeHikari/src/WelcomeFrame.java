import userinterface.Fonts;
import userinterface.GLOBAL;
import userinterface.GamePanel;
import userinterface.MasterPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;

public class WelcomeFrame {
//    public static int width = 1366, height = 768;
    public JFrame master = new JFrame("= =");


    private JLabel bg;
    private MasterPanel mp;
    private ImageIcon bgimg = new ImageIcon("Assets/osu-resources/osu.Game.Resources/Textures/Menu/menu-background-1.jpg");
    private ImageIcon dstimg = new ImageIcon();
    private boolean is_init = false;
    private GamePanel gp;

    private void setBackground(){
        dstimg.setImage(bgimg.getImage().getScaledInstance(GLOBAL.WIDTH, GLOBAL.HEIGHT, Image.SCALE_AREA_AVERAGING));
        bg.setBounds(0, 0, dstimg.getIconWidth(), dstimg.getIconHeight());
    }

    public void loadListener(){
        mp.sbt.addActionListener((ActionEvent e) -> {
            mp.setVisible(false);
            System.out.println("SOLO MODE");
            gp.start(false);
        });
        mp.obt.addActionListener((ActionEvent e) -> {
            mp.setVisible(false);
            System.out.println("ONLINE MODE");
            gp.start(true);
        });
        gp.hbt.addActionListener((ActionEvent e) -> {
            gp.stop();
            mp.setVisible(true);
            System.out.println("RETURN HOME");
        });

        master.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                gp.handleKeyboard(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    public WelcomeFrame() { // 这个只能调用一次
        if (is_init) return;
        else is_init = true;

        Fonts.init();
        utils.Word.initialize();

        mp = new MasterPanel();
        gp = new GamePanel();


        master.setVisible(true);
        master.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        master.setFocusable(true);
        master.setSize(GLOBAL.WIDTH, GLOBAL.HEIGHT);
        master.setBounds(0, 0, GLOBAL.WIDTH, GLOBAL.HEIGHT);

        bg = new JLabel(bgimg);

        setBackground();

        master.getLayeredPane().add(bg, new Integer(Integer.MIN_VALUE));
        master.getContentPane().add(bg, 0);

        master.add(mp, 0);
        master.add(gp, 0);


        loadListener();

        master.addComponentListener(new ComponentAdapter() { // 等比例适配和缩放
            @Override
            public void componentResized(ComponentEvent e) {
                GLOBAL.WIDTH=master.getWidth();
                GLOBAL.HEIGHT=master.getHeight();
                var mx = Math.max(GLOBAL.WIDTH/16f, GLOBAL.HEIGHT/9f);
                GLOBAL.WIDTH = (int)(mx * 16f);
                GLOBAL.HEIGHT = (int)(mx * 9f);
                master.setSize(GLOBAL.WIDTH, GLOBAL.HEIGHT);
                setBackground();
                mp.resizeSelf();
                gp.resizeSelf();

            }
        });




    }

    public static void main(String args[]) {
        new WelcomeFrame();
    }


}