import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 28.02.13
 * Time: 2:45
 * To change this template use File | Settings | File Templates.
 */
class RastLayer extends AbstractLayer {
    GisPixel[][] dots;
    int axisStep;
    int xCount;
    int yCount;
    BmpLayer bmpLayer;

    public RastLayer(BmpLayer bmpLayer, int axisStep){
        this.bmpLayer = bmpLayer;
        this.dimension = bmpLayer.dimension;
        this.axisStep = axisStep;
        isVisible = false;
        fillRectangles();
        draw();
    }

    public void setBmpLayer(BmpLayer bmpLayer) {
        this.bmpLayer = bmpLayer;
        redraw();
    }

    public void setAxisStep(int axisStep) {
        this.axisStep = axisStep;
        fillRectangles();
        redraw();
    }

    public void fillRectangles(){
        xCount = dimension.width/axisStep;
        yCount = dimension.height/axisStep;

        dots = new GisPixel[xCount][yCount];

        for(int x=0; x<xCount; x++)
            for(int y=0; y<yCount; y++) {
                Rectangle rectangle = new Rectangle(x, y, axisStep, axisStep);
                int color = bmpLayer.getImage().getRGB(x*axisStep + axisStep/2 , y*axisStep + axisStep/2);
                dots[x][y] = new GisPixel(rectangle, color);
            }
    }

    public void setDotColor(int x, int y, int rgb) {
        dots[x][y].setRgb(rgb);
    }

    public int getDotColor(int x, int y) {
        return dots[x][y].getRgb();
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
        for(int x=0; x<xCount; x++)
            for(int y=0; y<yCount; y++) {
                Rectangle rect = dots[x][y].getRect();
                gr.setColor(new Color(dots[x][y].getRgb()));
                gr.fillRect(rect.x * axisStep, rect.y * axisStep, axisStep, axisStep);
            }
    }
}
