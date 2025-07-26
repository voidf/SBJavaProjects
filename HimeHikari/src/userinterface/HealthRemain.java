package userinterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HealthRemain extends JLabel {

    public HealthRemain() {
        super();
        this.setText("ONLINE");
        this.setFont(Fonts.resizeFont(Fonts.Noto, GLOBAL.relativeSizeX(0.03f)));
        this.setIcon(new ImageIcon("Assets/icons/2x/baseline_health_and_safety_white_18dp.png"));
        this.setForeground(new Color(255,0,0,128));
        this.setBounds(
                GLOBAL.relativeSizeX(0f / 32f),
                GLOBAL.relativeSizeY(-15f / 32f),
                GLOBAL.relativeSizeX(1f),
                GLOBAL.relativeSizeY(1f)
        );
        this.setVisible(true);
    }

    public void resizeSelf() {
        this.setBounds(
                GLOBAL.relativeSizeX(0f / 32f),
                GLOBAL.relativeSizeY(-15f / 32f),
                GLOBAL.relativeSizeX(1f),
                GLOBAL.relativeSizeY(1f)
        );
        this.setVisible(true);
    }
}