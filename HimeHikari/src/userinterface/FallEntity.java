package userinterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FallEntity extends JLabel {
    public float velocity = 1.0f;
    public float x, y; // 只存百分比
    public String word;
    public FallEntity() {
        super();
//        this.setText("ONLINE");
        this.setFont(Fonts.resizeFont(Fonts.wakuwaku, GLOBAL.relativeSizeX(0.03f)));
        this.setForeground(Color.CYAN);
        resizeSelf();
    }

    public void resizeSelf() {
        this.setBounds(
                GLOBAL.relativeSizeX(this.x),
                GLOBAL.relativeSizeY(this.y),
                GLOBAL.relativeSizeX(1f),
                GLOBAL.relativeSizeY(1f)
        );
        this.setVisible(true);
    }
}