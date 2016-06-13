import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 26.02.13
 * Time: 0:13
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractLayer {
    boolean isVisible;
    BufferedImage layerImage;
    Dimension dimension;

    public AbstractLayer(Dimension dimension){
       isVisible = false;
       this.dimension = dimension;
    }

    public AbstractLayer(){

    }

    public BufferedImage getImage() {
        return layerImage;
    }

    public void showLayer() {
        isVisible = true;
        draw();
    }

    public void hideLayer() {
        isVisible = false;
        draw();
    }

    public void showHide() {
        isVisible = !isVisible;
        if(layerImage == null) draw();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
        draw();
    }

    public abstract BufferedImage draw();
    public abstract void redraw();
}
