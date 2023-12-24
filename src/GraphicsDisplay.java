//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class GraphicsDisplay extends JPanel {
    private Double[][] graphicsData;
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double scale;
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private Font axisFont;

    public GraphicsDisplay() {
        this.setBackground(Color.WHITE);
        float[] dashPattern = new float[]{26.0F, 4.0F, 4.0F, 4.0F, 16.0F, 4.0F, 4.0F, 4.0F, 26.0F, 4.0F};
        this.graphicsStroke = new BasicStroke(5.0F, 0, 1, 10.0F, dashPattern, 0.0F);
        this.axisStroke = new BasicStroke(2.0F, 0, 0, 10.0F, (float[])null, 0.0F);
        this.markerStroke = new BasicStroke(1.0F, 0, 0, 10.0F, (float[])null, 0.0F);
        this.axisFont = new Font("Serif", 1, 36);
    }

    public void showGraphics(Double[][] graphicsData) {
        this.graphicsData = graphicsData;
        this.repaint();
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        this.repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        this.repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.graphicsData != null && this.graphicsData.length != 0) {
            this.minX = this.graphicsData[0][0];
            this.maxX = this.graphicsData[this.graphicsData.length - 1][0];
            this.minY = this.graphicsData[0][1];
            this.maxY = this.minY;

            for(int i = 1; i < this.graphicsData.length; ++i) {
                if (this.graphicsData[i][1] < this.minY) {
                    this.minY = this.graphicsData[i][1];
                }

                if (this.graphicsData[i][1] > this.maxY) {
                    this.maxY = this.graphicsData[i][1];
                }
            }

            double scaleX = this.getSize().getWidth() / (this.maxX - this.minX);
            double scaleY = this.getSize().getHeight() / (this.maxY - this.minY);
            this.scale = Math.min(scaleX, scaleY);
            double xIncrement;
            if (this.scale == scaleX) {
                xIncrement = (this.getSize().getHeight() / this.scale - (this.maxY - this.minY)) / 2.0;
                this.maxY += xIncrement;
                this.minY -= xIncrement;
            }

            if (this.scale == scaleY) {
                xIncrement = (this.getSize().getWidth() / this.scale - (this.maxX - this.minX)) / 2.0;
                this.maxX += xIncrement;
                this.minX -= xIncrement;
            }

            Graphics2D canvas = (Graphics2D)g;
            Stroke oldStroke = canvas.getStroke();
            Color oldColor = canvas.getColor();
            Paint oldPaint = canvas.getPaint();
            Font oldFont = canvas.getFont();
            if (this.showAxis) {
                this.paintAxis(canvas);
            }

            this.paintGraphics(canvas);
            if (this.showMarkers) {
                this.paintMarkers(canvas);
            }

            canvas.setFont(oldFont);
            canvas.setPaint(oldPaint);
            canvas.setColor(oldColor);
            canvas.setStroke(oldStroke);
        }
    }

    protected void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(this.graphicsStroke);
        canvas.setColor(Color.BLACK);
        GeneralPath graphics = new GeneralPath();

        for(int i = 0; i < this.graphicsData.length; ++i) {
            Point2D.Double point = this.xyToPoint(this.graphicsData[i][0], this.graphicsData[i][1]);
            if (i > 0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }

        canvas.draw(graphics);
    }

    private void drawCustomMarker(Graphics2D canvas, Point2D.Double center) {
        int size = 11;
        int[] xPoints = new int[]{(int)center.getX(), (int)(center.getX() - (double)size), (int)center.getX(), (int)(center.getX() + (double)size)};
        int[] yPoints = new int[]{(int)(center.getY() - (double)size), (int)center.getY(), (int)(center.getY() + (double)size), (int)center.getY()};
        canvas.drawPolygon(xPoints, yPoints, 3);
    }

    protected void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(this.markerStroke);
        canvas.setColor(Color.BLACK);
        Double[][] var2 = this.graphicsData;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Double[] point = var2[var4];
            int ram = point[1].intValue();
            if ((double)ram + 0.1 <= point[1]) {
                canvas.setPaint(Color.GREEN);
            } else {
                canvas.setPaint(Color.RED);
            }

            Point2D.Double center = this.xyToPoint(point[0], point[1]);
            this.drawCustomMarker(canvas, center);
        }

    }

    protected void paintAxis(Graphics2D canvas) {
        canvas.setStroke(this.axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(this.axisFont);
        FontRenderContext context = canvas.getFontRenderContext();
        if (this.minX <= 0.0 && this.maxX >= 0.0 && this.minY <= 0.0 && this.maxY >= 0.0) {
            Point2D.Double origin = this.xyToPoint(0.0, 0.0);
            Rectangle2D bounds = this.axisFont.getStringBounds("0", context);
            float x = (float)(origin.getX() + bounds.getWidth() / 2.0);
            float y = (float)(origin.getY() + bounds.getHeight());
            //canvas.drawString("0", x, y);
        }

        GeneralPath arrow;
        Point2D.Double lineEnd;
        Rectangle2D bounds;
        Point2D.Double labelPos;
        if (this.minX <= 0.0 && this.maxX >= 0.0) {
            canvas.draw(new Line2D.Double(this.xyToPoint(0.0, this.maxY), this.xyToPoint(0.0, this.minY)));
            arrow = new GeneralPath();
            lineEnd = this.xyToPoint(0.0, this.maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5.0, arrow.getCurrentPoint().getY() + 20.0);
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10.0, arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            bounds = this.axisFont.getStringBounds("y", context);
            labelPos = this.xyToPoint(0.0, this.maxY);
            canvas.drawString("y", (float)labelPos.getX() + 10.0F, (float)(labelPos.getY() - bounds.getY()));
        }

        if (this.minY <= 0.0 && this.maxY >= 0.0) {
            canvas.draw(new Line2D.Double(this.xyToPoint(this.minX, 0.0), this.xyToPoint(this.maxX, 0.0)));
            arrow = new GeneralPath();
            lineEnd = this.xyToPoint(this.maxX, 0.0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20.0, arrow.getCurrentPoint().getY() - 5.0);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10.0);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            bounds = this.axisFont.getStringBounds("x", context);
            labelPos = this.xyToPoint(this.maxX, 0.0);
            canvas.drawString("x", (float)(labelPos.getX() - bounds.getWidth() - 10.0), (float)(labelPos.getY() + bounds.getY()));
        }

    }

    protected Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - this.minX;
        double deltaY = this.maxY - y;
        return new Point2D.Double(deltaX * this.scale, deltaY * this.scale);
    }

    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY) {
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
}
