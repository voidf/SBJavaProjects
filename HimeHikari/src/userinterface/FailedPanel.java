package userinterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

import utils.*;



public class FailedPanel extends JPanel {
    public FailedPanel(){
        super();
        this.setOpaque(false);
        this.setLayout(null);
        this.setSize(GLOBAL.WIDTH, GLOBAL.HEIGHT);
        this.setVisible(false);
    }

    public void resizeSelf() {
        this.setSize(GLOBAL.WIDTH, GLOBAL.HEIGHT);
    }
    public void perform(){
        this.setVisible(true);
        System.out.println("PERFORMED");
    }
    public void stop(){
        this.setVisible(false);
    }
}