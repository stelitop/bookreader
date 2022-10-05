package bookreader.utils;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageUtils {

    /**
     * Creates a deep copy of a buffered image.
     * @param bi The original buffered image.
     * @return A copy of the buffered image.
     */
    public BufferedImage deepCopyImage(BufferedImage bi) {
        if (bi == null) return null;
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null)
                    .getSubimage(0, 0, bi.getWidth(), bi.getHeight());
    }

    /**
     * Creates a grayscale copy of the image.
     * @param bi The buffered image to copy.
     * @return A copy of the buffered image, but in grayscale.
     */
    public BufferedImage grayscaleCopy(BufferedImage bi) {
        BufferedImage image = new BufferedImage(bi.getWidth(), bi.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = image.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return image;

//        ImageFilter filter = new GrayFilter(true, 50);
//        ImageProducer producer = new FilteredImageSource(bi.getSource(), filter);
//        return Toolkit.getDefaultToolkit().createImage(producer);
    }

    /**
     * Transforms an OpenCV material into a buffered image.
     * @param mat Material.
     * @return Transformed buffered image.
     */
    public BufferedImage openCVMatToBufferedImage(Mat mat) {
        BufferedImage ret = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        //DataBufferInt intBuffer = ret.getRa
        byte[] data = ((DataBufferByte) ret.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);
        return ret;
    }

    public Mat bufferedImage2Mat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMWRITE_JPEG_QUALITY);
    }
}
