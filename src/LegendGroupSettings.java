import sun.awt.VerticalBagLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 02.03.13
 * Time: 21:55
 * To change this template use File | Settings | File Templates.
 */
public class LegendGroupSettings {

    JPanel settingsPanel;
    JPanel mainPanel;
    JFrame frame;
    LinkedList<LegendGroup> legendGroups;
    int index;


    public LegendGroupSettings(LinkedList<LegendGroup> legendGroups) {
        this.legendGroups = legendGroups;
    }

    public MouseAdapter getPlusMouseAdapter() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTextField propertyField = new JTextField();
                propertyField.setPreferredSize(new Dimension(150, 20));
                settingsPanel.add(propertyField);
                frame.revalidate();
            }
        };
        return mouseAdapter;
    }

    public MouseAdapter getMinusMouseAdapter() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(settingsPanel.getComponentCount() < 1) return;
                settingsPanel.remove(settingsPanel.getComponentCount() - 1);
                frame.revalidate();
            }
        };
        return mouseAdapter;
    }

    public MouseAdapter getOkMouseAdapter() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                legendGroups.get(index).properties.clear();
                for(int i=0;i<settingsPanel.getComponentCount(); i++) {
                    JTextField textField = (JTextField) settingsPanel.getComponent(i);
                    legendGroups.get(index).addProperty(textField.getText());
                }
                legendGroups.get(index).update();
                frame.setVisible(false);
            }
        };
        return mouseAdapter;
    }

    public void showSettings(int index) {
        this.index = index;
        mainPanel = new JPanel(new VerticalBagLayout());
        mainPanel.setPreferredSize(new Dimension(150, 360));
        settingsPanel = new JPanel(new VerticalBagLayout());
        settingsPanel.setPreferredSize(new Dimension(150,300));
        String[] properties = legendGroups.get(index).getProperties();

        int height = 20;

        for(int i = 0; i<properties.length; i++) {
            JTextField propertyField = new JTextField(properties[i]);
            propertyField.setPreferredSize(new Dimension(150, height));
            settingsPanel.add(propertyField);
        }

        mainPanel.add(settingsPanel);

        JButton plusButton = new JButton("+");
        plusButton.setPreferredSize(new Dimension(150, height));
        plusButton.addMouseListener(getPlusMouseAdapter());
        mainPanel.add(plusButton);

        JButton minusButton = new JButton("-");
        minusButton.setPreferredSize(new Dimension(150, height));
        minusButton.addMouseListener(getMinusMouseAdapter());
        mainPanel.add(minusButton);

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(150, height));
        okButton.addMouseListener(getOkMouseAdapter());
        mainPanel.add(okButton);

        frame = new JFrame("Properties");
        frame.setContentPane(mainPanel);
        frame.setPreferredSize(new Dimension(150, 400));
        frame.setMinimumSize(new Dimension(150, 400));
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
