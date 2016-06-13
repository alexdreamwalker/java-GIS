import sun.awt.VerticalBagLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 24.02.13
 * Time: 19:54
 * To change this template use File | Settings | File Templates.
 */
public class MainWindow {
    private JPanel mypanel;
    private JButton imageButton;
    private JButton loadButton;
    private JButton saveButton;
    private JButton settingsButton;
    private JButton infoButton;
    private JPanel picture;
    private JButton axisButton;
    private JButton dotsButton;
    private JScrollPane scrollPane;
    private JButton legendButton;
    private JButton selectButton;

    private GISMap map;
    private BufferedImage image;
    private JFrame settings;
    private JFrame selectForm;

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainWindow");
        frame.setContentPane(new MainWindow().mypanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public MainWindow() {

        picture.setPreferredSize(new Dimension(600,600));
        scrollPane.setMinimumSize(new Dimension(160, 200));
        scrollPane.setPreferredSize(new Dimension(660, 540));
        scrollPane.setViewportView(picture);
        map = new GISMap(picture);         //initialize and add Map
        picture.add(map);
        picture.validate();

        Settings set = new Settings();
        set.setMap(map);
        settings = set.getFrame();


        imageButton.addMouseListener(new MouseAdapter() {   //load image from file
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                String filename = "";
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "BMP Images", "bmp");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(mypanel);
                if(returnVal == JFileChooser.APPROVE_OPTION) filename = chooser.getSelectedFile().getAbsolutePath();
                map.addBmpLayer(filename);
            }
        });


        settingsButton.addMouseListener(new MouseAdapter() {        //show Settings dialog
            @Override
            public void mouseClicked(MouseEvent e) {
                settings.setVisible(true);
            }
        });


        axisButton.addMouseListener(new MouseAdapter() {       //change Visibility of Axis
            @Override
            public void mouseClicked(MouseEvent e) {
                map.axisLayer.showHide();
                map.repaint();
            }
        });
        dotsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(map.rastLayer == null) map.addRastLayer();
                map.rastLayer.showHide();
                map.repaint();
            }
        });
        legendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(map.legend != null) map.legend.hideShow();
            }
        });
        picture.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if(map.legend != null) map.clickedOnPanel(x, y);
            }
        });
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                map.saveDialog();
            }
        });
        loadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                String filename = "";
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "DB files", "db");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(mypanel);
                if(returnVal == JFileChooser.APPROVE_OPTION) filename = chooser.getSelectedFile().getAbsolutePath();
                try {
                    map.loadLayer(filename);
                } catch (Exception e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        selectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SelectForm select = new SelectForm();
                select.setMap(map);
                selectForm = select.getFrame();
                selectForm.setVisible(true);
            }
        });
    }
}
