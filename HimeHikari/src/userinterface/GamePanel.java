package userinterface;

import utils.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class GamePanel extends JPanel {
    //    public FailedPanel failedPanel;
//    public FailedNotice failedNotice;
    public float timelimit = 3e9f;
    public HomeButton hbt;
    private FallEntity fall;
    private Random rd = new Random();
    private HealthRemain healthRemain;
    private OpponentRemain opr;
    private TranslateHint trans;
    private ArrayList<Integer> taskList = new ArrayList<>();
    private boolean running = false;
    private int health = 20;
    private long starttime;
    private String na;
    private Thread onlineThread;
    private web.Client cli;
    private boolean amionline;

    private WaitingHint waiting;
    private boolean is_waiting = false;

    public GamePanel() {
        super();
        this.setFont(Fonts.resizeFont(Fonts.wakuwaku, 18));
        this.setOpaque(false);
        this.setLayout(null);
        this.setSize(GLOBAL.WIDTH, GLOBAL.HEIGHT);
        this.setVisible(false);
//        failedPanel = new FailedPanel();
//        failedNotice = new FailedNotice();
//        failedPanel.add(failedNotice);

        fall = new FallEntity();
        hbt = new HomeButton();
        trans = new TranslateHint();
        waiting = new WaitingHint();
        healthRemain = new HealthRemain();
        opr = new OpponentRemain();


        this.add(fall);
        this.add(hbt);
        this.add(trans);
        this.add(waiting);
        this.add(opr);
        this.add(healthRemain);
    }

    private void renderWaiting() {
        is_waiting = true;
        cli = new web.Client();
        cli.init();
        cli.setRecvCallback((String recv) -> {
            if (recv.startsWith("/ready")) {
                if (!running) {
                    is_waiting = false;
                    na = cli.name;
                    onReady();
                }
            } else if (recv.startsWith("/leave")) {
                var sp = recv.split(" ");
                if (!sp[1].startsWith(na)) {
                    opr.setText("Disconnected");
                }
            } else if (recv.startsWith("/healthchange")) {
                var sp = recv.split(" ");
                if (!sp[1].startsWith(na)) {
                    System.out.println(sp[1] + "\t" + na);
                    opr.setText(sp[2]);
                }
            }
        });
        new Thread(() -> {
            int ctr = 0;
            String s[] = {".", "..", "..."};
            waiting.setVisible(true);
            while (is_waiting) {

                ctr++;
                ctr %= 3;
                waiting.setText("Waiting" + s[ctr]);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            waiting.setVisible(false);
        }).start();

        onlineThread = new Thread(() -> {
            cli.loop();
        });
        onlineThread.start();
    }

    private void onReady() {
        init();
        new Thread(() -> {
            loop();
        }).start();
        opr.setVisible(true);
        fall.setVisible(true);
    }

    public void resizeSelf() {
        this.setSize(GLOBAL.WIDTH, GLOBAL.HEIGHT);
        hbt.resizeSelf();
        trans.resizeSelf();
        fall.resizeSelf();
        healthRemain.resizeSelf();
    }

    private void updateWord() {
        var w = utils.Word.randomWord();
        System.out.println(w);
        fall.word = (String) w.get(Word.C_E.word);
        var ctr = rd.nextInt(fall.word.length() - 1) + 1;
        taskList.clear();
        for (var i = 0; i < fall.word.length(); i++) {
            taskList.add(i);
        }
        Collections.shuffle(taskList);
        while (taskList.size() > ctr) taskList.remove(taskList.size() - 1);
        taskList.sort((Integer x, Integer y) -> {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        });

        trans.setText((String) w.get(Word.C_E.translate));
        renderWord();
    }

    private void renderWord() {
        var tmp = new StringBuffer();
        var ptr = 0;
        for (var i : taskList) {
            while (ptr < fall.word.length()) {
                if (ptr == i) {
                    tmp.append('_');
                    ptr++;
                    break;
                } else tmp.append(fall.word.charAt(ptr));
                ptr++;
            }
        }
        while (ptr < fall.word.length()) {
            tmp.append(fall.word.charAt(ptr));
            ptr++;
        }

        fall.setText(tmp.toString());
    }

    private void updateHealth() {
        healthRemain.setText(String.format("%d", this.health));
        if (health <= 0) {
            running = false;
            trans.setText("Failed");
            trans.setForeground(Color.RED);
            trans.setFont(Fonts.resizeFont(Fonts.wakuwaku, GLOBAL.relativeSizeX(0.07f)));
            trans.setBounds(
                    GLOBAL.relativeSizeX(0.38f),
                    GLOBAL.relativeSizeY(0),
                    GLOBAL.relativeSizeX(1f),
                    GLOBAL.relativeSizeY(1f)
            );
        }
        if (amionline) cli.send(String.format("#life %d", this.health));
    }

    private void reset() {
        fall.x = GLOBAL.lerp(0.3f, 0.7f, rd.nextFloat());
        fall.y = -0.5f;
        updateWord();
        fall.resizeSelf();
        starttime = System.nanoTime();
    }

    public void handleKeyboard(KeyEvent e) {
        if (!running) return;
        System.out.println(e.getKeyChar());
        if (e.getKeyChar() == fall.word.charAt(taskList.get(0))) {
            taskList.remove(0);
            if (taskList.isEmpty()) {
                health++;
                Word.recordAC(fall.word);
                reset();
            } else {
                renderWord();
            }
            updateHealth();
        } else {
            health--;
            Word.recordWA(fall.word);
            reset();
            updateHealth();
        }
    }


    private void init() {
        running = true;
        health = 20;
        resizeSelf();
        updateHealth();
        reset();
        System.out.println(starttime);
    }

    private void loop() {
        while (running) {
            var nn = System.nanoTime();
            if (nn - starttime < timelimit) {
                fall.y = GLOBAL.lerp(-0.5f, 0.5f, (nn - starttime) / timelimit);
                fall.resizeSelf();
            } else {
                health--;
                Word.recordWA(fall.word);
                reset();
                updateHealth();
            }
        }
    }

    public void stop() {
        running = false;
        amionline = false;
        is_waiting = false;
        fall.setVisible(false);
        trans.setText("");

        this.setVisible(false);
        if (onlineThread != null && onlineThread.isAlive()) onlineThread.stop();
        if (cli != null) {
            cli.shouldIClose = 1;
            try {
                cli.sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(boolean is_online) {

        this.setVisible(true);
        if (!is_online) {
            amionline = false;
            opr.setVisible(false);
            init();
            new Thread(() -> {
                loop();
            }).start();
        } else {
            amionline = true;
            renderWaiting();
        }
    }

}