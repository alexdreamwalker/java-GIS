import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 01.03.13
 * Time: 2:31
 * To change this template use File | Settings | File Templates.
 */
public class GisPixel {
    Rectangle rect;
    int rgb;
    LegendArea area;

    public GisPixel(Rectangle rect, int rgb) {
        this.rect = rect;
        this.rgb = rgb;
    }

    public void setArea(LegendArea area) {
        this.area = area;
    }

    public Rectangle getRect() {
        return rect;
    }

    public int getRgb() {
        return rgb;
    }

    public void setRgb(int rgb) {
        this.rgb = rgb;
    }
}
