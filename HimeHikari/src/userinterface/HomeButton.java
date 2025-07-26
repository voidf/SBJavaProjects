package userinterface;

import javax.swing.*;

public class HomeButton extends JButton {
    public HomeButton() {
        super();
        this.setText("HOME");
        this.setFont(Fonts.resizeFont(Fonts.wakuwaku, GLOBAL.relativeSizeX(0.01f)));
        this.setBounds(
                GLOBAL.relativeSizeX(14f / 16f),
                GLOBAL.relativeSizeY(7f / 9f),
                GLOBAL.relativeSizeX(1f / 16f),
                GLOBAL.relativeSizeY(1f / 9f)
        );
        this.setVisible(true);
    }

    public void resizeSelf() {
        this.setBounds(
                GLOBAL.relativeSizeX(14f / 16f),
                GLOBAL.relativeSizeY(7f / 9f),
                GLOBAL.relativeSizeX(1f / 16f),
                GLOBAL.relativeSizeY(1f / 9f)
        );
        this.setVisible(true);
    }
}