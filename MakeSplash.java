import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class MakeSplash {
    public static void main(String[] args) throws Exception {
        int size = 1024;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // Enable anti-aliasing for smooth text
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // No background, keep it transparent because it's the icon itself
        // But the user requested "black over white, nothign else".
        // The splash screen background will be white, and the icon will just be black text.
        // Let's make the icon transparent background with black text.
        
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, size, size);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 96));
        
        String text1 = "routine";
        String text2 = "manager";
        
        FontMetrics fm = g2d.getFontMetrics();
        int w1 = fm.stringWidth(text1);
        int w2 = fm.stringWidth(text2);
        
        // Draw centered
        int y = size / 2 - fm.getHeight() / 2;
        g2d.drawString(text1, (size - w1) / 2, y);
        g2d.drawString(text2, (size - w2) / 2, y + fm.getHeight());
        
        g2d.dispose();
        
        File output = new File("app/src/main/res/drawable/splash_text.png");
        ImageIO.write(img, "png", output);
        System.out.println("Saved to " + output.getAbsolutePath());
    }
}
