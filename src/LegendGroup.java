import com.sun.webpane.sg.prism.WCGraphicsPrismContext;

import javax.naming.directory.BasicAttribute;
import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 02.03.13
 * Time: 1:38
 * To change this template use File | Settings | File Templates.
 */
public class LegendGroup {
    GisPixel[][] points;
    int xCount;
    int yCount;
    Point[] numbers;
    String name;
    int color;
    LinkedList<String> properties;
    LinkedList<LegendArea> areas;
    LegendGroupLayer legendGroupLayer;

    public LegendGroup() {
        name = "Group name";
        color = 0;
        points = new GisPixel[0][0];
        properties = new LinkedList<String>();
        areas = new LinkedList<LegendArea>();
    }

    public LegendGroup(String name, int color, GisPixel[][] points, int xCount, int yCount) {
        this.name = name;
        this.color = color;
        this.points = points;
        this.xCount = xCount;
        this.yCount = yCount;
        properties = new LinkedList<String>();
        areas = new LinkedList<LegendArea>();
        this.numbers = new Point[0];
        createAreas();
    }

    public void setPoints(GisPixel[][] points, int xCount, int yCount) {
        this.points = points;
        this.xCount = xCount;
        this.yCount = yCount;
        createAreas();
    }

    public boolean isEmpty() {
        return (numbers.length == 0);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumbers(Point[] numbers) {
        this.numbers = numbers;
        createAreas();
    }

    public void setColor(int color) {
        this.color = color;
        for(int i=0; i<numbers.length; i++) points[numbers[i].x][numbers[i].y].setRgb(color);
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void addProperty(String property) {
        properties.add(property);
    }

    public void removeProperty(String property) {
        if(properties.contains(property)) properties.remove(properties.indexOf(property));
    }

    public void update() {
        for(int i=0; i<areas.size(); i++) areas.get(i).update();
    }


    public String[] getProperties() {
        String[] strProperties =  properties.toArray(new String[properties.size()]);
        return strProperties;
    }

    public LegendGroupLayer makeLayer(Dimension dimension) {
        LegendGroupLayer layer = new LegendGroupLayer(this, dimension);
        legendGroupLayer = layer;
        return layer;
    }

    public void createAreas() {
        int number = 0;

        for(int i=0; i<numbers.length; i++) {
            Point point = numbers[i];
            if(points[point.x][point.y].area == null) {
                LegendArea legendArea = new LegendArea(this, numbers, number);
                areas.add(legendArea);
                number++;
                setArea(point.x, point.y, color, legendArea);
            }
        }
    }

    public void setArea(int x, int y, int color, LegendArea area) {
        if(x<0 || y<0) return;
        if(x>=xCount || y>=yCount) return;
        if(points[x][y] == null) return;
        GisPixel pixel = points[x][y];
        if(pixel.area != null) return;
        if(pixel.getRgb() != color) return;
        pixel.setArea(area);
        setArea(x+1, y, color, area);
        setArea(x-1, y, color, area);
        setArea(x, y+1, color, area);
        setArea(x, y-1, color, area);
        return;
    }

    public String createFile() {
        Path FROM = Paths.get("layers/layer.db");       //copy existing db
        Path TO = Paths.get("layers/"+name+".db");
        CopyOption[] options = new CopyOption[]{
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
        };
        try {
            Files.copy(FROM, TO, options);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return TO.toString();
    }

    public void saveToFile() throws Exception{
        String db = createFile();

        //connect to db
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:sqlite:"+db);
        con.setAutoCommit(false);
        Statement stat = con.createStatement();

        //insert gispixels
        PreparedStatement prep = con.prepareStatement("INSERT INTO gispixel values(?,?,?,?,?,?,?);");
        for(int i=0; i<xCount; i++)
            for(int j=0; j<yCount; j++) {
                GisPixel pixel = points[i][j];
                prep.setString(2, String.valueOf(pixel.rect.x));
                prep.setString(3, String.valueOf(pixel.rect.y));
                prep.setString(4, String.valueOf(pixel.rect.width));
                prep.setString(5, String.valueOf(pixel.rect.height));
                prep.setString(6, String.valueOf(pixel.getRgb()));
                if(pixel.area != null) prep.setString(7, String.valueOf(pixel.area.id));
                else prep.setString(7, "");
                prep.addBatch();
            }
        prep.executeBatch();
        con.commit();
        //insert points
        System.out.println("Start exporting points from "+name);
        prep = con.prepareStatement("INSERT INTO points values(?,?,?);");
        for(int i=0; i<numbers.length; i++) {
            Point point = numbers[i];
            prep.setString(2, String.valueOf(point.x));
            prep.setString(3, String.valueOf(point.y));
            prep.addBatch();
        }
        prep.executeBatch();
        con.commit();

        //insert properties
        System.out.println("Start exporting properties from "+name);
        prep = con.prepareStatement("INSERT INTO properties values(?,?);");
        for(int i=0; i<properties.size(); i++) {
            prep.setString(2, properties.get(i));
            prep.addBatch();
        }
        prep.executeBatch();
        con.commit();

        //insert areas
        System.out.println("Start exporting areas from "+name);
        prep = con.prepareStatement("INSERT INTO areas values(?,?,?,?);");
        for(int i=0; i<areas.size(); i++) {
            LegendArea area = areas.get(i);
            for(int j=0; j< properties.size(); j++) {
                prep.setString(2, String.valueOf(area.id));
                prep.setString(3, properties.get(i));
                prep.setString(4, area.attributes.get(properties.get(i)).toString().split(":")[1]);
                prep.addBatch();
            }
        }
        prep.executeBatch();
        con.commit();

        //insert settings
        System.out.println("Start exporting settings from "+name);
        prep = con.prepareStatement("INSERT INTO settings values(?,?,?);");
        prep.setString(2, "name");
        prep.setString(3, name);
        prep.addBatch();
        prep.setString(2, "color");
        prep.setString(3, String.valueOf(color));
        prep.addBatch();
        prep.setString(2, "width");
        if(legendGroupLayer != null ) {
            prep.setString(3, String.valueOf(legendGroupLayer.dimension.width));
            prep.addBatch();
            prep.setString(2, "height");
            prep.setString(3, String.valueOf(legendGroupLayer.dimension.height));
            prep.addBatch();
        }
        prep.setString(2, "xcount");
        prep.setString(3, String.valueOf(xCount));
        prep.addBatch();
        prep.setString(2, "ycount");
        prep.setString(3, String.valueOf(yCount));
        prep.addBatch();
        prep.executeBatch();
        con.commit();

        //done!
        con.close();
        System.out.println("Export of layer "+name+" completed!");
    }

    public LegendGroup(String path) throws Exception {
        name = "Group name";
        color = 0;
        points = new GisPixel[0][0];
        properties = new LinkedList<String>();
        areas = new LinkedList<LegendArea>();
        numbers = new Point[0];
        Dimension dimension = new Dimension(0, 0);
        int width = 0;
        int height = 0;
        int pointsCount = 0;

        //connect to db
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:sqlite:"+path);
        con.setAutoCommit(false);
        Statement stat = con.createStatement();

        //get settings

        ResultSet rs = stat.executeQuery("select * from settings where setting = 'name';");
        while (rs.next()) {
            name = rs.getString("value");
        }
        rs = stat.executeQuery("select * from settings where setting = 'color';");
        while (rs.next()) {
            color = rs.getInt("value");
        }
        rs = stat.executeQuery("select * from settings where setting = 'width';");
        while (rs.next()) {
            width = rs.getInt("value");
        }
        rs = stat.executeQuery("select * from settings where setting = 'height';");
        while (rs.next()) {
            height = rs.getInt("value");
        }
        dimension = new Dimension(width , height);
        rs = stat.executeQuery("select * from settings where setting = 'xcount';");
        while (rs.next()) {
            xCount = rs.getInt("value");
        }
        rs = stat.executeQuery("select * from settings where setting = 'ycount';");
        while (rs.next()) {
            yCount = rs.getInt("value");
        }
        points = new GisPixel[xCount][yCount];

        //get Points
        rs = stat.executeQuery("select COUNT(*) as pcount from points;");
        while (rs.next()) {
            pointsCount = rs.getInt("pcount");
        }
        numbers = new Point[pointsCount];
        rs = stat.executeQuery("select * from points;");
        int i = 0;
        while (rs.next()) {
            int x = rs.getInt("x");
            int y = rs.getInt("y");
            Point point = new Point(x, y);
            numbers[i] = point;
            i++;
        }

        //get properties
        rs = stat.executeQuery("select * from properties;");
        while (rs.next()) {
            String property = rs.getString("property");
            properties.add(property);
        }

        //get areas
        LegendArea area = new LegendArea(this, numbers, 0);
        rs = stat.executeQuery("select * from areas;");
        while (rs.next()) {
            int area_id = rs.getInt("area");
            String property = rs.getString("property");
            String value = rs.getString("value");
            area.setAttribute(property, value);
        }
        areas.add(area);

        //get pixels
        rs = stat.executeQuery("select * from gispixel;");
        while (rs.next()) {
            int x = rs.getInt("x");
            int y = rs.getInt("y");
            int pwidth = rs.getInt("width");
            int pheight = rs.getInt("height");
            int rgb = rs.getInt("rgb");
            Rectangle rect = new Rectangle(x, y, pwidth, pheight);
            GisPixel pixel = new GisPixel(rect, rgb);
            points[x][y] = pixel;
        }

        //done!
        rs.close();
        con.close();
        makeLayer(dimension);
    }

}
