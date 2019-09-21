

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author isra
 */
class Frame extends JFrame implements ActionListener {

    ImgArea ia;
    JFileChooser chooser;
    JMenuBar mainmenu;
    JMenu menu;
    JMenu editmenu;
    JMenuItem mopen;
    JMenuItem msaveas;
    JMenuItem msave;
    JMenuItem mexit;
    JMenuItem rectangle;
    JMenuItem mbright;
    JMenuItem mrotate;
    JMenuItem mcancel;
    String filename;

    public Frame() {
        ia = new ImgArea();
        Container cont = getContentPane();
        cont.add(ia, BorderLayout.CENTER);
        mainmenu = new JMenuBar();
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);

        mopen = new JMenuItem("Open...");
        mopen.setMnemonic(KeyEvent.VK_O);
        mopen.addActionListener(this);

        msaveas = new JMenuItem("Save as...");
        msaveas.setMnemonic(KeyEvent.VK_S);
        msaveas.addActionListener(this);

        msave = new JMenuItem("Save");
        msave.setMnemonic(KeyEvent.VK_V);
        msave.addActionListener(this);

        mexit = new JMenuItem("Exit");
        mexit.setMnemonic(KeyEvent.VK_X);
        mexit.addActionListener(this);
        menu.add(mopen);
        menu.add(msaveas);
        menu.add(msave);
        menu.add(mexit);

        editmenu = new JMenu("Edit");
        editmenu.setMnemonic(KeyEvent.VK_E);
        rectangle = new JMenuItem("Rectangle");
        rectangle.addActionListener(this);

        mbright = new JMenuItem("Image brightness");
        mbright.setMnemonic(KeyEvent.VK_B);
        mbright.addActionListener(this);

        mrotate = new JMenuItem("Image invert");
        mrotate.setMnemonic(KeyEvent.VK_T);
        mrotate.addActionListener(this);

        mcancel = new JMenuItem("Cancel editing");
        mcancel.setMnemonic(KeyEvent.VK_L);
        mcancel.addActionListener(this);

        editmenu.add(rectangle);
        editmenu.add(mbright);
        editmenu.add(mrotate);
        editmenu.add(mcancel);

        mainmenu.add(menu);
        mainmenu.add(editmenu);
        setJMenuBar(mainmenu);

        setTitle("Image Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(this.getExtendedState() | this.MAXIMIZED_BOTH);
        setVisible(true);

        chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "gif", "bmp", "png");
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(false);
        enableSaving(false);
        ia.requestFocus();
    }

    //The enableSaving method defines code to enable or  disable saving sub-menu items
    public void enableSaving(boolean f) {
        msaveas.setEnabled(f);
        msave.setEnabled(f);

    }

    //The setImage method has code to open the file dialog so the user can choose
    //the file to show on the program interface
    public void setImage() {

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filename = chooser.getSelectedFile().toString();
            ia.prepareImage(filename);
        }

    }
    //The showSaveFileDialog method has code to display the save file dialog
    //It is invoked when the user select Save as... sub-menu item

    public void showSaveFileDialog() {
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filen = chooser.getSelectedFile().toString();
            ia.saveToFile(filen);

        }
    }

    //Selected Rectanglel Area
    public class RectangleArea implements MouseListener, MouseMotionListener {

        int drag_status = 0, c1, c2, c3, c4;

        RectangleArea() {
            addMouseListener(this);
            addMouseMotionListener(this);

        }

        public void start() {

        }

        @Override
        public void mouseClicked(MouseEvent me) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mousePressed(MouseEvent me) {
            repaint();
            c1 = me.getX();
            c2 = me.getY();
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            repaint();
            if ((drag_status == 1)) {
                c3 = me.getX();
                c4 = me.getY();
                int w = c1 - c3;
                int h = c2 - c4;
                w = w * -1;
                h = h * -1;

                ia.draggedScreen(w, h);
                ia.repaint();
            }

        }

        @Override
        public void mouseEntered(MouseEvent me) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseExited(MouseEvent me) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            repaint();
            drag_status = 1;
            c3 = me.getX();
            c4 = me.getY();
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    ////start the ImageBrightness class
    //The ImageBrightness class represents the interface to allow the user to make the image 
    //brighter or darker by changing the value of the image slider
    //The ImageBrightness class is in the Main class
    public class ImageBrightness extends JFrame implements ChangeListener {

        JSlider slider;

        ImageBrightness() {
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();

                }
            });
            Container cont = getContentPane();
            slider = new JSlider(-10, 10, 0);
            slider.setEnabled(false);
            slider.addChangeListener(this);
            cont.add(slider, BorderLayout.CENTER);
            slider.setEnabled(true);
            setTitle("Image brightness");
            setPreferredSize(new Dimension(300, 100));
            setVisible(true);
            pack();
            enableSlider(false);
        }

        public void enableSlider(boolean enabled) {
            slider.setEnabled(enabled);
        }

        public void stateChanged(ChangeEvent e) {
            ia.setValue(slider.getValue() / 10.0f);
            ia.setActionSlided(true);
            ia.filterImage();
            ia.repaint();
            enableSaving(true);

        }

    } ////end of the ImageBrightness class

    public void actionPerformed(ActionEvent e) {

        JMenuItem source = (JMenuItem) (e.getSource());
        if (source.getText() == "Open...") {
            setImage();
            ia.repaint();
            validate();

        } else if (source.getText() == "Image brightness") {

            ImageBrightness ib = new ImageBrightness();
            if (ImgArea.imageLoaded) {
                ib.enableSlider(true);
            }
        } else if (source.getText()=="Image invert") {

            if (ImgArea.imageLoaded) {
                ia.rotateImage();
                enableSaving(true);
            }
        } else if (source.getText() == "Rectangle") {
//            RectangleArea ra = new RectangleArea();
            {

            }
        } else if (source.getText()=="Save as...") {
            showSaveFileDialog();

        } else if (source.getText()=="Save" ) {

            ia.saveToFile(filename);
        } else if (source.getText()=="Cancel editing") {
            ia.setImgFileName(filename);
            ia.reset();
        } else if (source.getText()=="Exit" ) {
            System.exit(0);
        }

    }

}
