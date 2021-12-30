
package itraqgui;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFDirectory;
import com.sun.media.imageio.plugins.tiff.TIFFField;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;

/**
 * 设置自定义dpi值到TIFF格式图像的样例程序。
 * http://blog.csdn.net/chenweionline/article/details/2026855
 * 需要 JAI_ImageIO_1.1 提供支持。
 */
public class SetDPI4TIFF {

    private static String METADATA_NAME = "com_sun_media_imageio_plugins_tiff_image_1.0";
    private static int DPI_X = 400;
    private static int DPI_Y = 400;

    public static void main(String[] args) throws Throwable{
        // Create sample image.
        BufferedImage imageBuff = new BufferedImage(450, 350, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = imageBuff.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 450, 350);
        g.setColor(Color.red);
        g.drawLine(10, 10, 200, 200);
        g.fillRect(30, 30, 400, 3);
        g.fillRect(30, 30, 3, 300);
        g.fillRect(30, 330, 400, 3);
        g.fillRect(430, 30, 3, 300);
        g.drawString("I am here", 200, 200);
        String fileName =  "C:\\Javalib\\dpi_api.tif";
        SetDPI4TIFF.Main(imageBuff, fileName);
    }    
    public static void Main( BufferedImage imageBuff, String fileName) throws Throwable {

        RenderedImage image = imageBuff;        
        // Get TIFF writer.
        Iterator writers = ImageIO.getImageWritersByFormatName("TIFF");
        if (writers == null || !writers.hasNext()) {
            throw new IllegalStateException("No TIFF writers!");
        }
        ImageWriter writer = (ImageWriter) writers.next();

        // Get the default image metadata.
        ImageTypeSpecifier imageType = ImageTypeSpecifier.createFromRenderedImage(image);
        IIOMetadata imageMetadata = writer.getDefaultImageMetadata(imageType, null);

        // Set DPI.
//        String fileName;
        String methodology;
        //if (args.length == 0 || args[0].equalsIgnoreCase("DOM")) {
        //    fileName = "/Users/gaos2/dpi_dom.tif";
            setDPIViaDOM(imageMetadata);
            methodology = "DOM";
        //} else {
        //    imageMetadata = setDPIViaAPI(imageMetadata);
        //    methodology = "API";
        //}
        System.out.println(" Writing " + fileName + " using " + methodology + " methodology ");

        // Write image.
        writer.setOutput(new FileImageOutputStream(new File(fileName)));
        writer.write(new IIOImage(image, null, imageMetadata));
    }

    /**
     * Set DPI using DOM nodes.
     */
    private static void setDPIViaDOM(IIOMetadata imageMetadata) throws IIOInvalidTreeException {
        // Get the DOM tree.
        IIOMetadataNode root = (IIOMetadataNode) imageMetadata.getAsTree(METADATA_NAME);

        // Get the IFD.
        IIOMetadataNode ifd = (IIOMetadataNode) root.getElementsByTagName("TIFFIFD").item(0);

        // Get {X,Y}Resolution tags.
        BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
        TIFFTag tagXRes = base.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION);
        TIFFTag tagYRes = base.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION);

        // Create {X,Y}Resolution nodes.
        IIOMetadataNode nodeXRes = createRationalNode(tagXRes.getName(), tagXRes.getNumber(), DPI_X, 1);
        IIOMetadataNode nodeYRes = createRationalNode(tagYRes.getName(), tagYRes.getNumber(), DPI_Y, 1);

        // Append {X,Y}Resolution nodes to IFD node.
        ifd.appendChild(nodeXRes);
        ifd.appendChild(nodeYRes);

        // Set metadata from tree.
        imageMetadata.setFromTree(METADATA_NAME, root);
    }

    /**
     * Creates a node of TIFF data type RATIONAL.
     */
    private static IIOMetadataNode createRationalNode(String tagName, int tagNumber, int numerator, int denominator) {
        // Create the field node with tag name and number.
        IIOMetadataNode field = new IIOMetadataNode("TIFFField");
        field.setAttribute("name", tagName);
        field.setAttribute("number", "" + tagNumber);

        // Create the RATIONAL node.
        IIOMetadataNode rational = new IIOMetadataNode("TIFFRational");
        rational.setAttribute("value", numerator + "/" + denominator);

        // Create the RATIONAL node and append RATIONAL node.
        IIOMetadataNode rationals = new IIOMetadataNode("TIFFRationals");
        rationals.appendChild(rational);

        // Append RATIONALS node to field node.
        field.appendChild(rationals);

        return field;
    }

    /**
     * Set DPI using API.
     */
    private static IIOMetadata setDPIViaAPI(IIOMetadata imageMetadata) throws IIOInvalidTreeException {
        // Derive the TIFFDirectory from the metadata.
        TIFFDirectory dir = TIFFDirectory.createFromMetadata(imageMetadata);

        // Get {X,Y}Resolution tags.
        BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
        TIFFTag tagXRes = base.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION);
        TIFFTag tagYRes = base.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION);

        // Create {X,Y}Resolution fields.
        TIFFField fieldXRes = new TIFFField(tagXRes, TIFFTag.TIFF_RATIONAL, 1, new long[][]{{DPI_X, 1}});
        TIFFField fieldYRes = new TIFFField(tagYRes, TIFFTag.TIFF_RATIONAL, 1, new long[][]{{DPI_Y, 1}});

        // Append {X,Y}Resolution fields to directory.
        dir.addTIFFField(fieldXRes);
        dir.addTIFFField(fieldYRes);

        // Convert to metadata object and return.
        return dir.getAsMetadata();
    }
}