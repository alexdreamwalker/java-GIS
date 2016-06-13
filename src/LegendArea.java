import sun.awt.HorizBagLayout;
import sun.awt.VerticalBagLayout;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.jar.Attributes;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 03.03.13
 * Time: 3:03
 * To change this template use File | Settings | File Templates.
 */
public class LegendArea {
    LegendGroup legendGroup;
    GisPixel[][] pixels;
    Point[] points;
    BasicAttributes attributes;
    boolean isSelected;
    JFrame frame;
    JPanel panel;
    int id;


    public LegendArea(LegendGroup legendGroup, Point[] numbers, int id) {
        this.legendGroup = legendGroup;
        this.pixels = legendGroup.points;
        this.points = numbers;
        isSelected = false;
        this.id = id;
        setAreasToPoints();
        makeAttributes();
        makeFrame();
    }

    public void update() {
        makeAttributes();
        makeFrame();
    }

    public void setAreasToPoints() {
        //for (int i=0; i<points.length; i++) {
            //Point point = points[i];
           // pixels[point.x][point.y].setArea(this);
       // }
    }

    public void switchSelection() {
        isSelected = !isSelected;
        legendGroup.legendGroupLayer.redraw();
    }

    public void setSelected(boolean selected) {
        isSelected = selected;

        for(int i=0; i<points.length; i++) {
            Point point = points[i];
            if(pixels[point.x][point.y].area == this) {
                if(selected == true) pixels[point.x][point.y].setRgb(Color.RED.getRGB());
                else pixels[point.x][point.y].setRgb(legendGroup.color);
            }
        }

        legendGroup.legendGroupLayer.redraw();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void makeAttributes() {
        String[] properties = legendGroup.getProperties();
        attributes = new BasicAttributes();
        for(int i=0; i<properties.length; i++) {
            attributes.put(new BasicAttribute(properties[i]));
        }
    }

    public void setAttribute(String property, String value) {
        attributes.put(property, value);
    }

    public String getAttribute(String attribute) {
        return attributes.get(attribute).toString().split(":")[1];
    }

    public MouseAdapter getOkMouseAdapter() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for(int i=0; i<panel.getComponentCount(); i ++) {
                    JPanel propPanel = (JPanel) panel.getComponent(i);
                    JLabel label = (JLabel) propPanel.getComponent(0);
                    JTextField textField = (JTextField) propPanel.getComponent(1);
                    attributes.put(label.getText(), textField.getText());
                }
                frame.setVisible(false);
                setSelected(false);
                makeFrame();
            }
        };
        return mouseAdapter;
    }

    public KeyAdapter getOkKeyAdapter() {         //set Name of the layer
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                for(int i=0; i<panel.getComponentCount(); i ++) {
                    JPanel propPanel = (JPanel) panel.getComponent(i);
                    JLabel label = (JLabel) propPanel.getComponent(0);
                    JTextField textField = (JTextField) propPanel.getComponent(1);
                    attributes.put(label.getText(), textField.getText());
                }
                frame.setVisible(false);
                makeFrame();
            }
        };
        return  keyAdapter;
    }

    public int calculatePerimetr() {
        double result = 0;
        int step = pixels[0][0].rect.width;
        Polygon polygon = new Polygon();
        for(int i=0; i<points.length; i++) {
            Point point = points[i];
            polygon.addPoint(point.x*step, point.y* step);
        }

        Rectangle bounds = polygon.getBounds();
        result = bounds.width * 2 + bounds.height * 2;

        double square = calculateSquare();
        if(4*Math.sqrt(square) < result) result = 4*Math.sqrt(square);

        return (int)result;
    }

    public int calculateSquare() {
        int step = pixels[0][0].rect.width;
        step = step * step;
        return points.length * step;
    }

    public void makeFrame() {
        JPanel mainPanel = new JPanel(new VerticalBagLayout());
        mainPanel.setPreferredSize(new Dimension(200,300));
        String[] properties = legendGroup.getProperties();
        panel = new JPanel(new VerticalBagLayout());
        panel.setPreferredSize(new Dimension(200, 220));

        int height = 200 / (properties.length + 1);

        for(int i =0; i<properties.length; i++) {
            String attribute = properties[i];
            JPanel attrPanel = new JPanel(new HorizBagLayout());
            attrPanel.setPreferredSize(new Dimension(300, height));

            JLabel attrName = new JLabel(attribute);
            attrName.setPreferredSize(new Dimension(100, height));
            attrPanel.add(attrName);

            JTextField attrValue = new JTextField(attributes.get(attribute).toString().split(":")[1]);
            attrValue.setPreferredSize(new Dimension(100, height));
            attrPanel.add(attrValue);

            panel.add(attrPanel);
        }
        mainPanel.add(panel);

        JLabel perLabel = new JLabel("Perimeter: " + calculatePerimetr());
        perLabel.setPreferredSize(new Dimension(200, 20));
        mainPanel.add(perLabel);

        JLabel squareLabel = new JLabel("Square: " + calculateSquare());
        squareLabel.setPreferredSize(new Dimension(200,20));
        mainPanel.add(squareLabel);

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(200, 30));
        okButton.addMouseListener(getOkMouseAdapter());
        okButton.addKeyListener(getOkKeyAdapter());
        mainPanel.add(okButton);

        frame = new JFrame("Area properties: "+legendGroup.getName());
        frame.setContentPane(mainPanel);
        frame.setPreferredSize(new Dimension(200, 320));
        frame.setMinimumSize(new Dimension(200, 320));
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(false);
    }

    public JFrame showFrame() {
        frame.setVisible(true);
        return frame;
    }
}
