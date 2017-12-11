package dwt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
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
    private static String resultTextPath = "C:\\Users\\acer\\Documents\\NetBeansProjects\\DWT\\hasil-text\\";
    private static String resultPath = "C:\\Users\\acer\\Documents\\NetBeansProjects\\DWT\\Hasil-DWT";
    private static String imgPath = "C:\\Users\\acer\\Documents\\NetBeansProjects\\DWT\\img-to-scan\\";
    private static int index = 0;
    
    private static double[][] highpassFilterMatrix = new double[][] {
        {1.0/Math.sqrt(2), -(1.0/Math.sqrt(2)), 0, 0},
        {0, 0, 1.0/Math.sqrt(2), -(1.0/Math.sqrt(2))}
    };
    
    private static double[][] lowpassFilterMatrix = new double[][] {
        {1.0/Math.sqrt(2), 1.0/Math.sqrt(2), 0, 0},
        {0, 0, 1.0/Math.sqrt(2), 1.0/Math.sqrt(2)}
    };
    
    public static void main(String[] args) {
        List<String> res = new ArrayList<>();
        res.add("W22\\and");
        res.add("W22\\been");
        res.add("W22\\being");
        res.add("W22\\for");
        res.add("W22\\had");
        res.add("W23\\and");
        res.add("W23\\been");
        res.add("W23\\being");
        res.add("W23\\for");
        res.add("W23\\had");
        res.add("W25\\and");
        res.add("W25\\been");
        res.add("W25\\being");
        res.add("W25\\for");
        res.add("W25\\had");
        res.add("W26\\and");
        res.add("W26\\been");
        res.add("W26\\being");
        res.add("W26\\for");
        res.add("W26\\had");
        res.add("W31\\been");
        res.add("W31\\being");
        res.add("W31\\for");
        res.add("W31\\had");
        res.add("W31\\he");
        res.add("W32\\and");
        res.add("W32\\for");
        res.add("W32\\had");
        res.add("W32\\he");
        res.add("W32\\is");
        res.add("W33\\and");
        res.add("W33\\been");
        res.add("W33\\for");
        res.add("W33\\had");
        res.add("W33\\he");
        res.add("W34\\and");
        res.add("W34\\for");
        res.add("W34\\he");
        res.add("W34\\of");
        res.add("W34\\that");
        res.add("W35\\and");
        res.add("W35\\been");
        res.add("W35\\being");
        res.add("W35\\for");
        res.add("W35\\had");
        
        for (String str : res) {
            String path = imgPath + str;
            String resultPathText = resultTextPath + str;
            File imgFolder = new File(path);
            ScanDirTxt(imgFolder, resultPathText);
        }
        
    }
    
    private static void ScanDir(File dir) {
        for (File entry: dir.listFiles()) {
            if (entry.isDirectory()) {
                System.out.println("ENTERING FOLDER " + entry.getName());
                ScanDir(entry);
            } else {
                String extension = "";
                int i = entry.getName().lastIndexOf('.');
                if (i >= 0) {
                    extension = entry.getName().substring(i+1);
                }
                if (extension.equals("png")) {
                    System.out.println("Scanning: " + entry.getName());
                    BufferedImage img = ReadImg(entry.getAbsolutePath());
                    System.out.println("Final result: ");
                    img = HaarWaveletTransform(img, true, 1);
                    index++;
                    WriteImg(img, resultPath + "\\" + String.valueOf(index) + "-" + entry.getName());
                }
            }
        }
    }
    
    private static void ScanDirTxt(File dir, String path) {
        for (File entry: dir.listFiles()) {
            if (entry.isDirectory()) {
                System.out.println("ENTERING FOLDER " + entry.getName());
                ScanDirTxt(entry, path);
            } else {
                String extension = "";
                int i = entry.getName().lastIndexOf('.');
                if (i >= 0) {
                    extension = entry.getName().substring(i+1);
                }
                if (extension.equals("png")) {
                    System.out.println("Scanning: " + entry.getName());
                    BufferedImage img = ReadImg(entry.getAbsolutePath());
                    index++;
                    double[][] imgPixels = ConvertImgToMatrix(img);
                    double[][] approx = ApproximateImg(imgPixels);
                    double[][] horizontal = HorizontalImg(imgPixels);
                    double[][] vertical = VerticalImg(imgPixels);
                    double[][] diagonal = DiagonalImg(imgPixels);
//                    System.out.println("Generating matrix approximation");
                    WriteTxtMatrix(path + "\\" + String.valueOf(index) + "-"
                            + entry.getName() + "-approximation.txt", approx);
//                    System.out.println("Generating matrix horizontal");
                    WriteTxtMatrix(path + "\\" + String.valueOf(index) + "-"
                            + entry.getName() + "-horizontal.txt", horizontal);
//                    System.out.println("Generating matrix vertical");
                    WriteTxtMatrix(path + "\\" + String.valueOf(index) + "-"
                            + entry.getName() + "-vertical.txt", vertical);
//                    System.out.println("Generating matrix diagonal");
                    WriteTxtMatrix(path + "\\" + String.valueOf(index) + "-"
                            + entry.getName() + "-diagonal.txt", diagonal);
                }
            }
        }
    }
    
    private static BufferedImage HaarWaveletTransform(BufferedImage img, boolean enableCycle, int maxCycle) {
        SetCycle(enableCycle);
        SetMaxCycle(maxCycle);
        
        BufferedImage result = img;
        result = DecomposeRow(result);
        result = DecomposeColumn(result);
        ShowImgPixels(result);
        return result;
    }
    
    private static void SetCycle(boolean enable) {
        DWT.enableCycle = enable;
    }
    
    private static void SetMaxCycle(int maxCycle) {
        DWT.maxCycle = maxCycle;
    }
    
    private static void ShowImgPixels(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        
        List<String> txtContent = new ArrayList<>();
        
        System.out.println("(");
        for (int i = 0; i < height; i++) {
            System.out.print("(");
            for (int j = 0; j < width; j++) {
                Color c = new Color(img.getRGB(j, i));
//                System.out.println("R:" + c.getRed() + ", G:" + c.getGreen()
//                        + ", B:" + c.getBlue());
                int R = c.getRed();
                int G = c.getGreen();
                int B = c.getBlue();
                System.out.print((float)(R + G + B) / 3.0);
                System.out.print(",");
            }
            System.out.println(")");
        }
        System.out.println(")");
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
            ImageIO.write(img, "png", output);
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
    
    private static void WriteTxt(String filePath, List<String> content) {
        Writer bufferedWriter = null;
        try {
            Writer fileWriter = new FileWriter(filePath);
            bufferedWriter = new BufferedWriter(fileWriter);
            
            for (String line : content) {
                bufferedWriter.write(line);
		bufferedWriter.write(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            System.out.println("Problem occurs when creating file " + filePath);
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    System.out.println("Problem occurs when closing file !");
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static void WriteTxtMatrix(String filePath, double[][] matrix) {
        Writer bufferedWriter = null;
        try {
            Writer fileWriter = new FileWriter(filePath);
            bufferedWriter = new BufferedWriter(fileWriter);
            
            for (double[] x : matrix) {
                boolean allZero = false;
                for (double y : x) {
                    if (y > 0)
                        bufferedWriter.write(String.valueOf(y) + " ");
                    else
                        allZero = true;
                }
                if (!allZero)
                    bufferedWriter.write(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            System.out.println("Problem occurs when creating file " + filePath);
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    System.out.println("Problem occurs when closing file !");
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static double[][] ApproximateImg(double[][] imgPixels) {
        double[][] result = new double[imgPixels.length]
                [imgPixels[0].length];
        
        int iterx = 0, itery = 0;
        int i = 0;
        do
        {
            if (imgPixels.length < 4) break;
            
            int j = 0;
            do
            {
                if (j + 4 >= imgPixels[0].length) break;
                double[][] tempMatrix = new double[4][4];
                int m = 0;
                int k = i;
                if (k + 4 >= imgPixels.length) break;
                for (k = i; k < k + 4; k++) {
                    if (m >= 4) break;
                    int n = 0;
                    for (int l = j; l < l + 4; l++) {
                        if (n >= 4) break;
                        tempMatrix[m][n] = imgPixels[k][l];
                        n++;
                    }
                    m++;
                }
                
                tempMatrix = Approximation(tempMatrix);
                
                m = 0;
                for (k = i; k < k + 2; k++) {
                    if (m >= 2) break;
                    int n = 0;
                    for (int l = j; l < l + 2; l++) {
                        if (n >= 2) break;
                        result[k][l] = tempMatrix[m][n];
                        n++;
                    }
                    m++;
                }
                            
                j += 4;
                itery++;
            }
            while(j < imgPixels[0].length);
            
            i += 4;
            iterx++;
        }
        while(i < imgPixels.length);
        
        return result;
    }
    
    private static double[][] HorizontalImg(double[][] imgPixels) {
        double[][] result = new double[imgPixels.length]
                [imgPixels[0].length];
        
        int iterx = 0, itery = 0;
        int i = 0;
        do
        {
            if (imgPixels.length < 4) break;
            
            int j = 0;
            do
            {
                if (j + 4 >= imgPixels[0].length) break;
                double[][] tempMatrix = new double[4][4];
                int m = 0;
                int k = i;
                if (k + 4 >= imgPixels.length) break;
                for (k = i; k < k + 4; k++) {
                    if (m >= 4) break;
                    int n = 0;
                    for (int l = j; l < l + 4; l++) {
                        if (n >= 4) break;
                        tempMatrix[m][n] = imgPixels[k][l];
                        n++;
                    }
                    m++;
                }
                
                tempMatrix = Horizontal(tempMatrix);
                
                m = 0;
                for (k = i; k < k + 2; k++) {
                    if (m >= 2) break;
                    int n = 0;
                    for (int l = j; l < l + 2; l++) {
                        if (n >= 2) break;
                        result[k][l] = tempMatrix[m][n];
                        n++;
                    }
                    m++;
                }
                            
                j += 4;
                itery++;
            }
            while(j < imgPixels[0].length);
            
            i += 4;
            iterx++;
        }
        while(i < imgPixels.length);
        
        return result;
    }
    
    private static double[][] VerticalImg(double[][] imgPixels) {
        double[][] result = new double[imgPixels.length]
                [imgPixels[0].length];
        
        int iterx = 0, itery = 0;
        int i = 0;
        do
        {
            if (imgPixels.length < 4) break;
            
            int j = 0;
            do
            {
                if (j + 4 >= imgPixels[0].length) break;
                double[][] tempMatrix = new double[4][4];
                int m = 0;
                int k = i;
                if (k + 4 >= imgPixels.length) break;
                for (k = i; k < k + 4; k++) {
                    if (m >= 4) break;
                    int n = 0;
                    for (int l = j; l < l + 4; l++) {
                        if (n >= 4) break;
                        tempMatrix[m][n] = imgPixels[k][l];
                        n++;
                    }
                    m++;
                }
                
                tempMatrix = Vertical(tempMatrix);
                
                m = 0;
                for (k = i; k < k + 2; k++) {
                    if (m >= 2) break;
                    int n = 0;
                    for (int l = j; l < l + 2; l++) {
                        if (n >= 2) break;
                        result[k][l] = tempMatrix[m][n];
                        n++;
                    }
                    m++;
                }
                            
                j += 4;
                itery++;
            }
            while(j < imgPixels[0].length);
            
            i += 4;
            iterx++;
        }
        while(i < imgPixels.length);
        
        return result;
    }
    
    private static double[][] DiagonalImg(double[][] imgPixels) {
        double[][] result = new double[imgPixels.length]
                [imgPixels[0].length];
        
        int iterx = 0, itery = 0;
        int i = 0;
        do
        {
            if (imgPixels.length < 4) break;
            
            int j = 0;
            do
            {
                if (j + 4 >= imgPixels[0].length) break;
                double[][] tempMatrix = new double[4][4];
                int m = 0;
                int k = i;
                if (k + 4 >= imgPixels.length) break;
                for (k = i; k < k + 4; k++) {
                    if (m >= 4) break;
                    int n = 0;
                    for (int l = j; l < l + 4; l++) {
                        if (n >= 4) break;
                        tempMatrix[m][n] = imgPixels[k][l];
                        n++;
                    }
                    m++;
                }
                
                tempMatrix = Diagonal(tempMatrix);
                
                m = 0;
                for (k = i; k < k + 2; k++) {
                    if (m >= 2) break;
                    int n = 0;
                    for (int l = j; l < l + 2; l++) {
                        if (n >= 2) break;
                        result[k][l] = tempMatrix[m][n];
                        n++;
                    }
                    m++;
                }
                            
                j += 4;
                itery++;
            }
            while(j < imgPixels[0].length);
            
            i += 4;
            iterx++;
        }
        while(i < imgPixels.length);
        
        return result;
    }
    
    
    private static double[][] Approximation(double[][] imgPixels) {
        double[][] result = new double[lowpassFilterMatrix.length]
                [lowpassFilterMatrix[0].length];
        double[][] transpose = TransposeMatrix(imgPixels);
        result = MultiplyMatrix(lowpassFilterMatrix, transpose);
        result = TransposeMatrix(result);
        result = MultiplyMatrix(lowpassFilterMatrix, result);
        return result;
    }
    
    private static double[][] Horizontal(double[][] imgPixels) {
        double[][] result = new double[lowpassFilterMatrix.length]
                [lowpassFilterMatrix[0].length];
        double[][] transpose = TransposeMatrix(imgPixels);
        result = MultiplyMatrix(lowpassFilterMatrix, transpose);
        result = TransposeMatrix(result);
        result = MultiplyMatrix(highpassFilterMatrix, result);
        return result;
    }
    
    private static double[][] Vertical(double[][] imgPixels) {
        double[][] result = new double[highpassFilterMatrix.length]
                [highpassFilterMatrix[0].length];
        double[][] transpose = TransposeMatrix(imgPixels);
        result = MultiplyMatrix(highpassFilterMatrix, transpose);
        result = TransposeMatrix(result);
        result = MultiplyMatrix(lowpassFilterMatrix, result);
        return result;
    }
    
    private static double[][] Diagonal(double[][] imgPixels) {
        double[][] result = new double[highpassFilterMatrix.length]
                [highpassFilterMatrix[0].length];
        double[][] transpose = TransposeMatrix(imgPixels);
        result = MultiplyMatrix(highpassFilterMatrix, transpose);
        result = TransposeMatrix(result);
        result = MultiplyMatrix(highpassFilterMatrix, result);
        return result;
    }
    
    private static double[][] ConvertImgToMatrix(BufferedImage img) {
        double[][] matrixResult = new double[img.getHeight()][img.getWidth()];
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color c = new Color(img.getRGB(j, i));
                matrixResult[i][j] = (c.getRed() + c.getGreen()
                        + c.getBlue()) / 3;
            }
        }
        return matrixResult;
    }
    
    private static double[][] TransposeMatrix(double[][] matrix) {
        double[][] transpose = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                transpose[j][i] = matrix[i][j];
            }
        }
        return transpose;
    }
    
    private static double[][] MultiplyMatrix(double[][] matrixA,
            double[][] matrixB) {
        double[][] result = new double[matrixA.length][matrixB[0].length];
        
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = 0.0;
            }
        }
        
        if (matrixA[0].length != matrixB.length) {
            System.out.println("Cannot multiply matrix "
                    + matrixA.length + " x " + matrixA[0].length
                    + " with " + matrixB.length + " x "
                    + matrixB[0].length);
            return result;
        }
        
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                for (int k = 0; k < matrixA[0].length; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        
        return result;
    }
    
    private static void PrintMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
