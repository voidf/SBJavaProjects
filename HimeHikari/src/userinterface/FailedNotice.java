package userinterface;

import userinterface.Fonts;
import userinterface.GLOBAL;

import javax.swing.*;
import java.awt.*;

public class FailedNotice extends JButton {
    public FailedNotice(){
        super();
        this.setText("Failed");
        this.setFont(Fonts.resizeFont(Fonts.wakuwaku, GLOBAL.relativeSizeX(0.1f)));
        this.setForeground(Color.RED);
        resizeSelf();
    }
    public void resizeSelf() {
        this.setBounds(
                GLOBAL.relativeSizeX(0f),
                GLOBAL.relativeSizeY(0f),
                GLOBAL.relativeSizeX(1f),
                GLOBAL.relativeSizeY(1f)
        );
        this.setVisible(true);
    }
}