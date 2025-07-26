package userinterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class OpponentRemain extends JLabel {

    public OpponentRemain() {
        super();
        this.setText("20");
        this.setFont(Fonts.resizeFont(Fonts.Noto, GLOBAL.relativeSizeX(0.03f)));
        this.setIcon(new ImageIcon("Assets/icons/2x/baseline_social_distance_white_18dp.png"));
        this.setForeground(new Color(255,0,0,128));
        resizeSelf();
        this.setVisible(false);
    }

    public void resizeSelf() {
        this.setBounds(
                GLOBAL.relativeSizeX(0f / 32f),
                GLOBAL.relativeSizeY(-13f / 32f),
                GLOBAL.relativeSizeX(1f),
                GLOBAL.relativeSizeY(1f)
        );

    }
}