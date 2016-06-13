import sun.awt.HorizBagLayout;
import sun.awt.VerticalBagLayout;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.imageio.*;
import javax.swing.*;

public class GISMap extends JPanel {

    JPanel panel;
    GisPixel[][] pixels;
    public Dimension dimension;
    public BmpLayer bmpLayer;
    public AxisLayer axisLayer;
    public RastLayer rastLayer;
    LinkedList<LegendGroupLayer> layers;
    Legend legend;
    JPanel savePanel;
    JDialog dialog;

    public GISMap(JPanel panel) {
        this.panel = panel;
        layers = new LinkedList<LegendGroupLayer>();
        layers.clear();
        addAxisLayer();
        legend = new Legend(this, 0);
        repaint();
    }

    public void addBmpLayer(String path) {
        bmpLayer = new BmpLayer(path);
        bmpLayer.showLayer();
        dimension = bmpLayer.getDimension();
        panel.setPreferredSize(dimension);
        axisLayer.setDimension(dimension);
        repaint();
    }

    public void addAxisLayer() {
        axisLayer = new AxisLayer(dimension, 10);
        repaint();
    }

    public void addRastLayer() {
        rastLayer = new RastLayer(bmpLayer, axisLayer.getAxisStep());
        refreshLegend();
        repaint();
    }

    public void refreshLegend() {
        if(rastLayer == null) addRastLayer();
        legend = new Legend(rastLayer, this);
        legend.legendToMap();
        rastLayer.showHide();
        bmpLayer.showHide();
        legend = new Legend(this);
        pixels = legend.pixels;
        legend.hideShow();
    }

    public void addLayer(LegendGroupLayer layer) {
        layers.addLast(layer);
        repaint();
    }

    public void loadLayer(String path) throws Exception {
        LegendGroup loadGroup = new LegendGroup(path);
        dimension = loadGroup.legendGroupLayer.getDimension();
        panel.setPreferredSize(dimension);
        layers.addLast(loadGroup.legendGroupLayer);
        legend.makeLegendFromLayers();
        legend.makeFrame();
        pixels = loadGroup.points;
        //refreshPixels();
        repaint();
    }

    public void refreshPixels() {
        LegendGroupLayer layer = layers.getLast();
        for(int i=0; i<layer.legendGroup.numbers.length; i++) {
            Point point = layer.legendGroup.numbers[i];
            pixels[point.x][point.y].setArea(layer.areas.getFirst());
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(bmpLayer != null && bmpLayer.isVisible()) g.drawImage(bmpLayer.getImage(), 0, 0, null);
        if(axisLayer != null && axisLayer.isVisible()) g.drawImage(axisLayer.getImage(), 0, 0, null);
        if(rastLayer !=null && rastLayer.isVisible()) g.drawImage(rastLayer.getImage(), 0, 0, null);
        for(int i=0; i<layers.size(); i++) {
            if(layers.get(i).isVisible()) g.drawImage(layers.get(i).getImage(),0,0,null);
        }

    }

    public void saveDialog() {
        JPanel panel = new JPanel(new VerticalBagLayout());
        panel.setPreferredSize(new Dimension(300, 400));

        savePanel = new JPanel(new VerticalBagLayout());
        savePanel.setPreferredSize(new Dimension(300, 370));

        int height = 370 / (layers.size() + 1);

        for(int i=0; i<layers.size(); i++) {
            JPanel layPanel = new JPanel(new HorizBagLayout());
            layPanel.setPreferredSize(new Dimension(300, height));

            JCheckBox checkBox = new JCheckBox();
            checkBox.setPreferredSize(new Dimension(20, height));
            layPanel.add(checkBox);

            JLabel label = new JLabel(layers.get(i).legendGroup.getName());
            label.setPreferredSize(new Dimension(350, height));
            layPanel.add(label);

            savePanel.add(layPanel);
        }

        panel.add(savePanel);

        JButton button = new JButton("Export");
        button.setPreferredSize(new Dimension(300, 30));
        button.addKeyListener(getOkKeyAdapter());
        button.addMouseListener(getOkMouseAdapter());
        panel.add(button);

        dialog = new JDialog();
        dialog.setPreferredSize(new Dimension(300, 420));
        dialog.setContentPane(panel);
        dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        dialog.pack();
        dialog.setVisible(true);
    }

    public KeyAdapter getOkKeyAdapter() {         //set Name of the layer
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                for(int i=0; i<savePanel.getComponentCount(); i ++) {
                    JPanel propPanel = (JPanel) panel.getComponent(i);
                    JCheckBox checkBox = (JCheckBox) panel.getComponent(0);
                    if(checkBox.isSelected()) try {
                        layers.get(i).legendGroup.saveToFile();
                    } catch (Exception e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                dialog.setVisible(false);
            }
        };
        return  keyAdapter;
    }

    public MouseAdapter getOkMouseAdapter() {         //set Name of the layer
        MouseAdapter keyAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for(int i=0; i<savePanel.getComponentCount(); i ++) {
                    JPanel propPanel = (JPanel) savePanel.getComponent(i);
                    JCheckBox checkBox = (JCheckBox) propPanel.getComponent(0);
                    if(checkBox.isSelected()) try {
                        layers.get(i).legendGroup.saveToFile();
                    } catch (Exception e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                dialog.setVisible(false);
            }
        };
        return  keyAdapter;
    }

    public void clearSelection() {
        for(int i=0; i<layers.size(); i++) {
            LegendGroupLayer layer = layers.get(i);
            for(int j=0; j<layer.areas.size(); j++) {
                LegendArea area = layer.areas.get(j);
                if(area.isSelected) area.setSelected(false);
            }
        }
    }

    public void clickedOnPanel(int x, int y) {
        int xCoord = x / axisLayer.getAxisStep();
        int yCoord = y / axisLayer.getAxisStep();
        LegendArea area =  pixels[xCoord][yCoord].area;
        if(area != null) {
            area.setSelected(true);
            repaint();
            JFrame areaFrame = area.showFrame();
            areaFrame.setLocation(x, y);
        }
    }
}
