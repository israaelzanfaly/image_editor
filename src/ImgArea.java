
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class ImgArea extends Canvas {

    Image orImg;
    BufferedImage orBufferedImage, bimg, bimg1;
    float radian, e;
    Dimension ds;
    int mX, mY, x, y;
    static boolean imageLoaded;
    boolean actionSlided;
    boolean actionRotated;
    boolean crop = true;
    MediaTracker mt;
    static Color c;
    Robot rb;
    boolean dirHor;
    String imgFileName;

    public ImgArea() {

        ds = getToolkit().getScreenSize(); //get the screen size   
        mX = (int) ds.getWidth() / 2; //half of the screen width
        mY = (int) ds.getHeight() / 2;//half of the screen height

    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g; //create Graphics2D object   
        if (imageLoaded) {
            if (actionSlided || actionRotated) {
                x = mX - bimg.getWidth() / 2;
                y = mY - bimg.getHeight() / 2;
                g2d.translate(x, y); //move to coordinate (x,y)  
                g2d.drawImage(bimg, 0, 0, null); //draw the iamge

            } else { //draw the original image
                x = mX - orBufferedImage.getWidth() / 2;
                y = mY - orBufferedImage.getHeight() / 2;
                g2d.translate(x, y); //move to  coordinate (x,y)
                g2d.drawImage(orBufferedImage, 0, 0, null); //draw image
            }
        }
        g2d.dispose(); //clean the Graphic2D object

    }

    public void initialize() {
        imageLoaded = false;
        actionSlided = false;
        dirHor = false;
        c = null;
        radian = 0.0f;
        e = 0.0f;
    }

    public void setImgFileName(String fname) {
        imgFileName = fname;
    }

    public void setValue(float value) {
        e = value;
    }
    //set a value to e variable 
    //this method is invoked when the user makes change to the  image slider

    public void setActionSlided(float value) {
        e = value;
    }

    //Set a boolean value the actionSlided variable 
    public void setActionSlided(boolean value) {
        actionSlided = value;
    }
    //The createBufferedImageFromImage method is abled to generate a buffered image from an input image

    public BufferedImage createBufferedImageFromImage(Image image, int width, int height, boolean tran) {
        BufferedImage dest;
        if (tran) {
            dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        } else {
            dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return dest;
    }

    //Prepare the image so it is ready to display and editable
    public void prepareImage(String filename) {
        initialize();
        try {
            //track the image loading
            mt = new MediaTracker(this);
            orImg = Toolkit.getDefaultToolkit().getImage(filename);
            mt.addImage(orImg, 0);
            mt.waitForID(0);
            //get the image width and height  
            int width = orImg.getWidth(null);
            int height = orImg.getHeight(null);
            //create buffered image from the image so any change to the image can be made
            orBufferedImage = createBufferedImageFromImage(orImg, width, height, false);
            //create the blank buffered image
            //the update image data is stored in the buffered image   
            bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            imageLoaded = true; //now the image is loaded
        } catch (Exception e) {
            System.exit(-1);
        }
    }

    public void draggedScreen(int w, int h) {
        BufferedImage bi = (BufferedImage) createImage(w, h);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        //resize the update image

        if (crop) {
            g2d.drawImage(bimg, 0, 0, w, h, null);
        } //resize the original image
        else {
            g2d.drawImage(orImg, 0, 0, w, h, null);
        }
        bimg = bi;
        g2d.dispose();

    }

//The filterImage method applies brightness to the image when the knob of the image slider is 
    //making changed.
    //When the value of the image slider changes it affects the e variable
    //so the image is brighter or darker
    public void filterImage() {
        float[] elements = {0.0f, 1.0f, 0.0f, -1.0f, e, 1.0f, 0.0f, 0.0f, 0.0f};
        Kernel kernel = new Kernel(3, 3, elements);  //create keynel object to encapsulate the elements array
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null); //create ConvolveOp to encapsulate 
        //the kernel
        bimg = new BufferedImage(orBufferedImage.getWidth(), orBufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        cop.filter(orBufferedImage, bimg); //start filtering the image 
        //the filtered image is stored in the bimg buffered image
        //now the image increases or decreases its brightness

    }

    //Rotate the image shown on the program interface
    public void makeImageRotate(BufferedImage image, int w, int h) {

        BufferedImage bi = (BufferedImage) createImage(w, h);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        radian = (float) Math.PI; //angle     
        g2d.translate(w / 2, h / 2); //move to coordinate (w/2,h/2)
        g2d.rotate(radian); //rotate the image
        g2d.translate(-h / 2, -w / 2); //move the coordinate back
        g2d.drawImage(image, 0, 0, null); //draw the rotated image
        bimg = bi; //update the image so now you see the rotated image
        g2d.dispose();

    }

    //The rotateImage invokes the makeImageRotate method to rotate the image
    public void rotateImage() {
        BufferedImage bi;
        //rotate update image
        if (actionRotated || actionSlided) {
            bi = bimg;
        } //rotate the original image 
        else {
            bi = orBufferedImage;
        }

        makeImageRotate(bi, bi.getHeight(), bi.getWidth());

        actionRotated = true; //set the actionRotated to true to indicate that 
        //the image is rotated
        repaint(); //repaint the update image

    }
    //cancel the image editing so we reset the drawing area

    public void reset() {
        if (imageLoaded) {
            prepareImage(imgFileName);
            repaint();
        }

    }
    //Save the image file

    public void saveToFile(String filename) {
        String ftype = filename.substring(filename.lastIndexOf('.') + 1);
        try {

            //save the update image
            if (actionSlided || actionRotated) {
                ImageIO.write(bimg, ftype, new File(filename));
            }
        } catch (IOException e) {
            System.out.println("Error in saving the file");
        }
    }
}
