import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 10.03.13
 * Time: 19:48
 * To change this template use File | Settings | File Templates.
 */
public class SelectForm {
    private JPanel mypanel;
    private JComboBox cbLayers;
    private JComboBox cbProperties;
    private JComboBox cbSign;
    private JTextField value;
    private JButton OKButton;
    JFrame selectForm;
    GISMap map;


    public SelectForm() {
        cbLayers.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    String item = (String)e.getItem();
                    fillProperties();
                }
            }
        });
        OKButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                map.clearSelection();
                int layIndex = cbLayers.getSelectedIndex();
                String property = (String) cbProperties.getSelectedItem();
                String prvalue = value.getText();
                LegendGroupLayer legendGroupLayer = map.layers.get(layIndex);

                for(int i=0; i<legendGroupLayer.areas.size(); i++) {
                    LegendArea area = legendGroupLayer.areas.get(i);
                    switch (cbSign.getSelectedIndex()) {
                        case 0:
                            if(area.getAttribute(property).trim().compareTo(prvalue) == 0) area.setSelected(true);
                            System.out.println(prvalue.compareTo(area.getAttribute(property).trim()));
                            break;
                        case 1:
                            if(area.getAttribute(property).trim().compareTo(prvalue) != 0) area.setSelected(true);
                            break;
                        case 2:
                            if(Integer.valueOf(area.getAttribute(property).trim()) > Integer.valueOf(prvalue)) area.setSelected(true);
                            break;
                        case 3:
                            if(Integer.valueOf(area.getAttribute(property).trim()) < Integer.valueOf(prvalue)) area.setSelected(true);
                            break;
                    }
                }
                map.repaint();
                selectForm.setVisible(false);
            }
        });
    }

    public JFrame getFrame() {
        selectForm = new JFrame("Settings");
        selectForm.setContentPane(mypanel);
        selectForm.setMinimumSize(new Dimension(300, 300));
        selectForm.pack();
        selectForm.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        selectForm.setVisible(false);
        return selectForm;
    }

    public void setMap(GISMap map) {
        this.map = map;
        cbLayers.removeAllItems();
        for(int i=0;i<map.layers.size();i++) {
            cbLayers.addItem(map.layers.get(i).legendGroup.getName());
        }
    }

    public void fillProperties() {
        cbProperties.removeAllItems();
        int index = cbLayers.getSelectedIndex();
        for(int i=0; i<map.layers.get(index).legendGroup.properties.size(); i++) {
            cbProperties.addItem(map.layers.get(index).legendGroup.properties.get(i));
        }
    }

    public void makeReload() {
        setMap(map);
    }
}
