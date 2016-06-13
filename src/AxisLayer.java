import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 26.02.13
 * Time: 0:17
 * To change this template use File | Settings | File Templates.
 */
public class AxisLayer extends AbstractLayer {
    int axisStep;

    public AxisLayer(Dimension dimension, int axisStep) {
        isVisible = false;
        this.dimension = dimension;
        this.axisStep = axisStep;
        draw();
    }

    public void setAxisStep(int axisStep) {
        this.axisStep = axisStep;
        redraw();
    }

    public int getAxisStep() {
        return axisStep;
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
        // Рисуем сетку
        gr.setColor(new Color(0,0,0));
        for(int y=0;y<=dimension.width;y+=axisStep){
            gr.drawLine(0, y, dimension.width, y);
            gr.drawLine(y, 0 , y, dimension.height);
        }
    }
}
