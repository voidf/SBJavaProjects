package userinterface;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MasterPanel extends JPanel {
    public SoloButtom sbt;
    public OnlineButton obt;

    public MasterPanel(){
        super();

        this.setOpaque(false);
        this.setLayout(null);
        this.setSize(GLOBAL.WIDTH, GLOBAL.HEIGHT);
        this.setVisible(true);
        insertButtons();
    }
    public void insertButtons(){
        sbt = new SoloButtom();
        obt = new OnlineButton();

        this.add(sbt);
        this.add(obt);
    }
    public void resizeSelf(){
        this.setSize(GLOBAL.WIDTH, GLOBAL.HEIGHT);
        sbt.resizeSelf();
        obt.resizeSelf();
    }

}