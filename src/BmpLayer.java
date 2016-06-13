import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 26.02.13
 * Time: 0:25
 * To change this template use File | Settings | File Templates.
 */
public class BmpLayer extends AbstractLayer {
    String path;

    public BmpLayer(String path) {
        this.path = path;
        isVisible = false;
        draw();

    }

    @Override
    public BufferedImage draw() {
        if(isVisible()) {
            try {
                layerImage = ImageIO.read(new File(path));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            dimension = new Dimension(layerImage.getWidth(), layerImage.getHeight());
        }
        return layerImage;
    }

    @Override
    public void redraw() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
