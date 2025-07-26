package userinterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WaitingHint extends JLabel {

    public WaitingHint() {
        super();
        resizeSelf();
    }

    public void resizeSelf() {
        this.setFont(Fonts.resizeFont(Fonts.Noto, GLOBAL.relativeSizeX(0.06f)));
        this.setForeground(Color.GREEN);
        this.setBounds(
                GLOBAL.relativeSizeX(6f / 32f),
                GLOBAL.relativeSizeY(6f / 32f),
                GLOBAL.relativeSizeX(1f),
                GLOBAL.relativeSizeY(1f)
        );
        this.setVisible(false);
    }
}