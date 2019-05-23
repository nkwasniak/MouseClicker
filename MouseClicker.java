package com.company;

import javafx.scene.control.DialogPane;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MouseClicker extends JFrame implements ActionListener {
    private JButton bStart, bEnd;
    private Robot robot = null;
    private boolean first = true;
    private volatile boolean loop = false;
    private JMenuBar MenuBar;
    private JMenu Options,Help;
    private JMenuItem changeDelay, setXY,changeKey,author;
    private JCheckBox def;
    private int delay = 1000;
    private boolean mouseMov = false;
    private String Hotkey=KeyEvent.getKeyText(KeyEvent.VK_F8);
    private String keyText="CHUJ";


    private JLabel label2;


    MouseClicker(){
        setSize(206,130);
        setTitle("Clicker");
        setLayout(null);
        setResizable(false);
        setAlwaysOnTop(true);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        bStart = new JButton();
        bStart.setText("Press " + Hotkey + " or click to start");

        bStart.setBounds(5,5,180,55);

        add(bStart);
        bStart.addActionListener(this);


        label2 = new JLabel("",SwingConstants.CENTER);

        MenuBar = new JMenuBar();
        Options = new JMenu("Options");
        Help = new JMenu("Help");
        changeDelay = new JMenuItem("Change Delay");
        setXY = new JMenuItem("Set X and Y points");
        changeKey = new JMenuItem("Change HotKey");
        author = new JMenuItem("Author");
        Options.add(changeDelay);
        Options.add(setXY);
        Options.add(changeKey);
        Help.add(author);
        author.addActionListener(this);

        MenuBar.add(Options);
        MenuBar.add(Help);

        MenuBar.add(Box.createHorizontalGlue());
        setJMenuBar(MenuBar);


        changeKey.addActionListener(this);
        changeDelay.addActionListener(this);
        setXY.addActionListener(this);

        try
        {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new NativeKeyListener()
            {

                @Override
                public void nativeKeyTyped(NativeKeyEvent nativeEvent)
                {
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent nativeEvent)
                {

                    keyText = NativeKeyEvent.getKeyText(nativeEvent.getKeyCode());

                    if(!keyText.equals("Ctrl"))   label2.setText("Your hotkey to start/stop app is:  " + keyText);

                    if(keyText.equals(Hotkey)){
                        onButtonPress();
                    }
                }

                @Override
                public void nativeKeyPressed(NativeKeyEvent nativeEvent)
                {

                }
            });
        }
        catch (NativeHookException e)
        {
            e.printStackTrace();
        }

/* when i am in program window

        bStart.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(Hotkey + " XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX " + KeyEvent.getKeyText(e.getKeyCode()));
                if (KeyEvent.getKeyText(e.getKeyCode()) == Hotkey){
                   // onButtonPress();
                }
            }
        });

*/


    }


public static void main (String[] args){
        MouseClicker Clicker = new MouseClicker();
        Clicker.setVisible(true);
    }

int X;
int Y;


public void klick() {
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            loop = true;
            while (loop) {
                try {
                    robot = new Robot();
                } catch (AWTException e) {
                    e.printStackTrace();
                }

                robot.delay(delay);
                if(mouseMov){
                    robot.mouseMove(X,Y);
                }

                robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
            }
        }
    });
    t.start();
}


public void onButtonPress(){
    System.out.println(first + " " + loop);
    if (first) {
        bStart.setText("Press " + Hotkey + " or click to stop");
        first = false;
        klick();
    }
    else{
        loop = false;
        first = true;
        bStart.setText("Press " + Hotkey + " or click to start");
    }


}


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        boolean playAgain = true;


        if (source == author)
            JOptionPane.showMessageDialog(this, " Wersja: 1.00 \n © Copyright by Nikodem Kwaśniak \n nkwasniak.github.io","Author",JOptionPane.INFORMATION_MESSAGE);

        if (source == bStart) onButtonPress();


        if (source == setXY){

            JTextField field1 = new JTextField();
            JTextField field2 = new JTextField();


            Object[] message = {
                    "Set X:", field1,
                    "Set Y:", field2,
            };

            Object[] options = {
                    "Ok",
                    "Cancel",
                    "Set Default",
            };


            while (playAgain){

            playAgain = false;

            int option = JOptionPane.showOptionDialog(this, message, "Enter your values or set default", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[2]);


            if (option == JOptionPane.OK_OPTION)
            {

                if (field1.getText().isEmpty() && field2.getText().isEmpty()){
                    JOptionPane.showMessageDialog(this,"No data!","Error",JOptionPane.ERROR_MESSAGE);
                    playAgain = true;
                    continue;
                }
                else {
                    String valueX = field1.getText();
                    String valueY = field2.getText();
                    X = Integer.parseInt(valueX);
                    Y = Integer.parseInt(valueY);
                    mouseMov = true;
                }
            }


            if (option == JOptionPane.CANCEL_OPTION){
                mouseMov = false;
            }

        }

        }



        if(source == changeDelay) {

            Object[] options = {
                    "Ok",
                    "Cancel",
                    "Set Default",
            };

            JTextField txt = new JTextField(String.valueOf(delay));

            txt.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    txt.setText("");
                }
            });




            txt.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if(!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE) || c == KeyEvent.VK_DELETE){
                        getToolkit().beep();
                        e.consume();
                    }
                }
            });

            Object[] message = {
                    "Input delay(ms):", txt,
            };

            playAgain = true;
            while (playAgain) {
                playAgain = false;

                if (source == changeDelay) {
                    int option = JOptionPane.showOptionDialog(this, message, "Input delay or set deafult", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);

                    String d = txt.getText();

                    if (option == JOptionPane.YES_OPTION) {
                        if (d.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "No data!", "Error", JOptionPane.ERROR_MESSAGE);
                            playAgain = true;
                            continue;
                        } else {
                            delay = Integer.parseInt(d);
                        }
                    }

                    if (option == JOptionPane.CANCEL_OPTION) {
                        delay = 1000;
                    }
                }
            }
        }

        if (source == changeKey){


            label2.setText("Press key to set shortcut and click Set Hotkey!");

            Object[] options = {
                    "Set Hotkey",
                    "Cancel",
            };

            Object[] message = {
                    label2,
            };

                while (true) {
                    keyText = "";
                    int option = JOptionPane.showOptionDialog(this, message, "Set Hotkey", JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null, options, options[1]);


                    if (option == JOptionPane.YES_OPTION) {
                        if (keyText.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "No data!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else{
                            Hotkey = keyText;
                            if(!first)  bStart.setText("Press " + Hotkey + " or click to stop");
                            else  bStart.setText("Press " + Hotkey + " or click to start");
                            break;
                        }

                    }

                    if(option == JOptionPane.NO_OPTION) break;
                }
            }
        }



}
