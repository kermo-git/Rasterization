import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

import rasterization.Config;

public class Display extends JPanel {
    private static final long serialVersionUID = 1L;
    private static BufferedImage image;

    private static long start;
    static {
        start = System.nanoTime();
        image = SceneBuilder.renderScene();
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("3D Graphics");
        frame.setSize(Config.SCREENSIZE_X, Config.SCREENSIZE_Y);
        frame.add(new Display());
        frame.setVisible(true);

        double duration = (double) (System.nanoTime() - start);
        System.out.println("Rendering took " + duration / 1e9 + " seconds");
    }
}
