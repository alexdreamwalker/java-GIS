import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 26.02.13
 * Time: 1:18
 * To change this template use File | Settings | File Templates.
 */
public class Settings {
    private JPanel panel;
    private JSpinner spinner1;
    private JTextField textField1;
    private JButton OKButton;
    private GISMap map;
    JFrame settings;

    void setMap(GISMap map) {
        this.map = map;
        spinner1.setValue(map.axisLayer.getAxisStep());
    }

    public JFrame getFrame() {
        settings = new JFrame("Settings");
        settings.setContentPane(panel);
        settings.setMinimumSize(new Dimension(300, 300));
        settings.pack();
        settings.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        settings.setVisible(false);
        return settings;
    }

    public Settings() {
        OKButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int axisStep = (Integer)spinner1.getValue();
                map.axisLayer.setAxisStep(axisStep);
                if(map.rastLayer != null) {
                    map.rastLayer.setAxisStep(axisStep);
                    if(map.legend != null) map.refreshLegend();
                }
                map.repaint();
                settings.setVisible(false);
            }
        });
    }
}
