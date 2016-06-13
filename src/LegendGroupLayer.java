import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 03.03.13
 * Time: 5:01
 * To change this template use File | Settings | File Templates.
 */
public class LegendGroupLayer extends AbstractLayer {
    LegendGroup legendGroup;
    LinkedList<LegendArea> areas;

    public LegendGroupLayer(LegendGroup legendGroup, Dimension dimension) {
        this.legendGroup = legendGroup;
        this.areas = legendGroup.areas;
        this.dimension = dimension;
        isVisible = true;
        draw();
    }

    @Override
    public BufferedImage draw() {
        if(isVisible()) {
            redraw();
        }
        return layerImage;
    }

    @Override
    public void redraw() {
        layerImage = new BufferedImage(dimension.width,dimension.height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D gr = (Graphics2D)layerImage.getGraphics();
        Color color;
        for(int i=0; i<areas.size(); i++) {
            LegendArea area = areas.get(i);
            Point[] points = area.points;

            for(int j=0; j<points.length; j++) {
                Point point = points[j];
                GisPixel pixel = area.pixels[point.x][point.y];
                Rectangle rect = pixel.getRect();
                if(!area.isSelected) color = new Color(pixel.getRgb());
                else color = new Color(Color.RED.getRGB());
                gr.setColor(color);
                gr.fillRect(rect.x * rect.width, rect.y * rect.height, rect.width, rect.height);
            }
        }
    }
}
