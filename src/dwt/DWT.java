/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dwt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Azhary Arliansyah
 * Discrete Wavelet Transform menggunakan Haar Wavelet pada piksel gambar
 */
public class DWT {

    private static boolean enableCycle;
    private static int maxCycle;
    
    public static void main(String[] args) {
        BufferedImage img = ReadImg("C:\\Users\\acer\\Documents\\NetBeansProjects\\DWT\\img\\reimu.jpg");
        img = HaarWaveletTransform(img, true, 2);
        WriteImg(img, "C:\\Users\\acer\\Documents\\NetBeansProjects\\DWT\\img\\reimu-dwt.jpg");
    }
    
    private static BufferedImage HaarWaveletTransform(BufferedImage img, boolean enableCycle, int maxCycle) {
        SetCycle(enableCycle);
        SetMaxCycle(maxCycle);
        
        BufferedImage result = img;
        result = DecomposeRow(result);
        result = DecomposeColumn(result);
        return result;
    }
    
    private static void SetCycle(boolean enable) {
        DWT.enableCycle = enable;
    }
    
    private static void SetMaxCycle(int maxCycle) {
        DWT.maxCycle = maxCycle;
    }
   
    private static BufferedImage ReadImg(String path) {
        BufferedImage img = null;
        try {
            File file = new File(path);
            img = ImageIO.read(file);
        } catch (IOException ex) {
            Logger.getLogger(DWT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return img;
    }
    
    private static void WriteImg(BufferedImage img, String path) {
        try {
            File output = new File(path);
            ImageIO.write(img, "jpg", output);
        } catch (IOException ex) {
            Logger.getLogger(DWT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static BufferedImage DecomposeRow(BufferedImage img) {
        BufferedImage result = img;
        
        int width = img.getWidth();
        int height = img.getHeight();
        
        int[][] pixels = new int[height][width];

        for (int i = 0; i < height; i++) {
            int numCycle = 0;
            for (int length = width / 2; ; length /= 2) {
                BufferedImage temp = result;
                for (int j = 0; j < length; j++) {
                    Color c1 = new Color(temp.getRGB(j * 2, i));
                    Color c2 = new Color(temp.getRGB(j * 2 + 1, i));
                    pixels[i][j] = (c1.getRGB() + c2.getRGB()) / 2;
                    result.setRGB(j, i, pixels[i][j]);
                    pixels[i][length + j] = (c1.getRGB() - c2.getRGB()) / 2;
                    result.setRGB(length + j, i, pixels[i][length + j]);
                }
                
                numCycle++;
                if (enableCycle) {
                    if (numCycle >= maxCycle) {
                        break;
                    }
                }
                
                if (length <= 1) {
                    break;
                }
            }
        }
        
        return result;
    }
    
    private static BufferedImage DecomposeColumn(BufferedImage img) {
        BufferedImage result = img;
        
        int width = img.getWidth();
        int height = img.getHeight();
        
        int[][] pixels = new int[height][width];

        for (int i = 0; i < width; i++) {
            int numCycle = 0;
            for (int length = height / 2; ; length /= 2) {
                BufferedImage temp = result;
                for (int j = 0; j < length; j++) {
                    Color c1 = new Color(temp.getRGB(i, j * 2));
                    Color c2 = new Color(temp.getRGB(i, j * 2 + 1));
                    pixels[j][i] = (c1.getRGB() + c2.getRGB()) / 2;
                    result.setRGB(i, j, pixels[j][i]);
                    pixels[length + j][i] = (c1.getRGB() - c2.getRGB()) / 2;
                    result.setRGB(i, length + j, pixels[length + j][i]);
                }
                
                numCycle++;
                if (enableCycle) {
                    if (numCycle >= maxCycle) {
                        break;
                    }
                }
                
                if (length <= 1) {
                    break;
                }
            }
        }
        
        return result;
    }
}
