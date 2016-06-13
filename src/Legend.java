import sun.awt.HorizBagLayout;
import sun.awt.VerticalBagLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 02.03.13
 * Time: 1:38
 * To change this template use File | Settings | File Templates.
 */
public class Legend {
    LinkedList<LegendGroup> legendGroups;
    LinkedList<LegendGroupLayer> layers;
    LegendGroupSettings legendGroupSettings;
    GISMap map;
    RastLayer rastLayer;
    GisPixel[][] pixels;
    public JFrame frame;
    JPanel panel;
    JPanel settingsPanel;
    int xCount;
    int yCount;
    int chosenColor;

    public Legend(RastLayer rastLayer, GISMap map) {   //creates new legend and groups based on picture
        this.map = map;
        this.rastLayer = rastLayer;
        pixels = new GisPixel[0][0];
        if(rastLayer != null) this.pixels = rastLayer.dots;
        this.xCount = rastLayer.xCount;
        this.yCount = rastLayer.yCount;
        legendGroups = new LinkedList<LegendGroup>();
        legendGroupSettings = new LegendGroupSettings(legendGroups);
        makeLegendGroups();
        makeFrame();
    }

    public Legend(GISMap map) {  //creates legend based on existing layers and groups
        this.map = map;
        this.rastLayer = map.rastLayer;
        this.pixels = rastLayer.dots;
        this.xCount = rastLayer.xCount;
        this.yCount = rastLayer.yCount;
        legendGroups = new LinkedList<LegendGroup>();
        legendGroupSettings = new LegendGroupSettings(legendGroups);
        this.layers = map.layers;
        makeLegendFromLayers();
        makeFrame();
    }

    public Legend(GISMap gisMap, int flag) {
        this.map = gisMap;
        this.layers = gisMap.layers;
        legendGroups = new LinkedList<LegendGroup>();
        legendGroupSettings = new LegendGroupSettings(legendGroups);
    }

    public void setPixels(GisPixel[][] pixels) {
        this.pixels = pixels;
        legendGroups.clear();
    }

    public void makeLegendGroups() {
        legendGroups.clear();
        LinkedList<Integer> colors = new LinkedList<Integer>();
        colors.clear();

        for(int i=0; i<xCount; i++)
            for(int j=0; j<yCount; j++) {
            int color = pixels[i][j].getRgb();
            if(!colors.contains(color)) colors.add(color);
        }

        for(int i=0; i<colors.size(); i++) {
            LegendGroup legendGroup = new LegendGroup("Group", colors.get(i), pixels, xCount, yCount);
            legendGroups.add(legendGroup);
        }

        fillLegendGroups();
    }

    public void fillLegendGroups() {
         for(int i=0; i<legendGroups.size(); i++) {
             LegendGroup legendGroup = legendGroups.get(i);
             int groupColor = legendGroup.getColor();
             LinkedList<Point> points = new LinkedList<Point>();
             points.clear();

             for(int z=0; z<xCount; z++)
                 for(int j=0; j<yCount; j++) {
                     int color = pixels[z][j].getRgb();
                     if(color == groupColor) {
                         Point point = new Point(z, j);
                         points.add(point);
                     }
             }

             Point[] groupPoints = points.toArray(new Point[points.size()]);
             legendGroup.setNumbers(groupPoints);
         }
    }

    public void makeFrame() {
        panel = new JPanel(new VerticalBagLayout());
        panel.setPreferredSize(new Dimension(170,300));

        int height = 300 / legendGroups.size();

        for(int i=0; i<legendGroups.size(); i++) {             //create rows of legend
            LegendGroup legendGroup = legendGroups.get(i);
            JPanel groupPanel = new JPanel(new HorizBagLayout());
            groupPanel.setPreferredSize(new Dimension(150,height));

            JCheckBox checkBox = new JCheckBox();
            checkBox.setPreferredSize(new Dimension(20,height));
            checkBox.setSelected(true);
            checkBox.setMnemonic(i);
            checkBox.addMouseListener(getCheckBoxMouseAdapter());
            groupPanel.add(checkBox);

            JLabel colorLabel = new JLabel();                 //color label
            colorLabel.setPreferredSize(new Dimension(30, height));
            colorLabel.setOpaque(true);
            colorLabel.setBackground(new Color(legendGroup.getColor()));
            colorLabel.setDisplayedMnemonic(i);
            colorLabel.addMouseListener(getColorMouseAdapter());
            groupPanel.add(colorLabel);

            JTextField nameField = new JTextField(legendGroup.getName());   //caption label
            nameField.setName(String.valueOf(i));
            nameField.setPreferredSize(new Dimension(90, height));
            nameField.addKeyListener(getNameKeyAdapter());
            groupPanel.add(nameField);

            JButton settingsButton = new JButton("...");
            settingsButton.setPreferredSize(new Dimension(20,height));
            settingsButton.setMnemonic(i);
            settingsButton.addMouseListener(getSettingsMouseAdapter());
            groupPanel.add(settingsButton);

            panel.add(groupPanel);
        }

        frame = new JFrame("Legend");
        frame.setContentPane(panel);
        frame.setPreferredSize(new Dimension(170, 300));
        frame.setMinimumSize(new Dimension(170, 300));
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(false);
    }

    public MouseAdapter getColorMouseAdapter() {   //choose color of layer
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel colorLabel = (JLabel) e.getSource();
                Color newColor = JColorChooser.showDialog(panel, "Choose Background Color", colorLabel.getBackground());
                colorLabel.setBackground(newColor);
                legendGroups.get(colorLabel.getDisplayedMnemonic()).setColor(newColor.getRGB());
                legendGroups.get(colorLabel.getDisplayedMnemonic()).legendGroupLayer.redraw();
                map.repaint();
            }
        };
        return mouseAdapter;
    }

    public MouseAdapter getCheckBoxMouseAdapter() {    //set Visibility of the layer
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                int index = checkBox.getMnemonic();
                legendGroups.get(index).legendGroupLayer.isVisible = checkBox.isSelected();
                legendGroups.get(index).legendGroupLayer.redraw();
                map.repaint();
            }
        };
        return mouseAdapter;
    }

    public MouseAdapter getSettingsMouseAdapter() {    //show/set properties of the layer
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JButton settingsButton = (JButton) e.getSource();
                int index = settingsButton.getMnemonic();
                legendGroupSettings.showSettings(index);
            }
        };
        return mouseAdapter;
    }

    public KeyAdapter getNameKeyAdapter() {         //set Name of the layer
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                JTextField nameField = (JTextField) e.getSource();
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String newName = nameField.getText();
                    int index = Integer.valueOf(nameField.getName());
                    legendGroups.get(index).setName(newName);
                }
            }
        };
        return  keyAdapter;
    }

    public void makeLayersFromLegend() {
        layers = new LinkedList<LegendGroupLayer>();
        for(int i=0; i<legendGroups.size(); i++) {
            LegendGroupLayer legendGroupLayer = legendGroups.get(i).makeLayer(map.dimension);
            if(!legendGroups.get(i).isEmpty()) layers.add(legendGroupLayer);
        }
    }

    public void legendToMap() {
        makeLayersFromLegend();
        for(int i=0; i<layers.size(); i++) {
            map.addLayer(layers.get(i));
        }
    }

    public void makeLegendFromLayers() {
        legendGroups.clear();
        for(int i=0; i<layers.size(); i++) {
            LegendGroupLayer legendGroupLayer = layers.get(i);
            legendGroups.add(legendGroupLayer.legendGroup);
        }
    }

    public void hideShow() {
        frame.setVisible(!frame.isVisible());
    }

}
