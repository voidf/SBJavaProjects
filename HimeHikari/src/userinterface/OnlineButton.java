package userinterface;

import javax.swing.*;

public class OnlineButton extends JButton {
    public OnlineButton() {
        super();
        this.setText("ONLINE");
        this.setFont(Fonts.resizeFont(Fonts.wakuwaku, GLOBAL.relativeSizeX(0.035f)));
        this.setBounds(
                GLOBAL.relativeSizeX(3f / 8f),
                GLOBAL.relativeSizeY(3f / 8f),
                GLOBAL.relativeSizeX(1f / 4f),
                GLOBAL.relativeSizeY(1f / 9f)
        );
        this.setVisible(true);
    }

    public void resizeSelf() {
        this.setBounds(
                GLOBAL.relativeSizeX(3f / 8f),
                GLOBAL.relativeSizeY(3f / 8f),
                GLOBAL.relativeSizeX(1f / 4f),
                GLOBAL.relativeSizeY(1f / 9f)
        );
        this.setVisible(true);
    }
}