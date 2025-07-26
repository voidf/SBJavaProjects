package userinterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TranslateHint extends JLabel {

    public TranslateHint() {
        super();
        resizeSelf();
    }

    public void resizeSelf() {
        this.setFont(Fonts.resizeFont(Fonts.Noto, GLOBAL.relativeSizeX(0.03f)));
        this.setForeground(Color.CYAN);
        this.setBounds(
                GLOBAL.relativeSizeX(12f / 32f),
                GLOBAL.relativeSizeY(11f / 32f),
                GLOBAL.relativeSizeX(1f),
                GLOBAL.relativeSizeY(1f)
        );
        this.setVisible(true);
    }
}