/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prototype;

import Extra.Control;
import Extra.Database;
import Extra.Model;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.bytedeco.javacpp.BytePointer;
import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imencode;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import org.bytedeco.opencv.global.opencv_imgproc;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2GRAY;

public class Register {

    JFrame frame = new JFrame();
    JLabel take_photo = new JLabel("Hold down the button for 3 seconds");
    JButton button = new JButton("Photo");
    JLabel camera = new JLabel("");
    private Register.DaemonThread myThread = null;

    VideoCapture VidCap = null;
    Mat WebcamImage = new Mat();
    CascadeClassifier training = new CascadeClassifier("C:\\Project\\photos\\haarcascade_frontalface_alt.xml");
    BytePointer BytePoint = new BytePointer();
    RectVector FoundFace = new RectVector();

    String root, firstNamePerson, lastNamePerson;
    int numSamples = 25, sample = 1, idPerson;

    Database connection = new Database();

    Register(int id, String fName, String lName) {

        take_photo.setBounds(120, 0, 300, 50);
        take_photo.setFont(new Font(null, Font.PLAIN, 15));
        frame.add(take_photo);
        button.setBounds(150, 400, 200, 50);
        frame.add(button);
        camera.setBounds(10, 40, 470, 330);
        camera.setBorder(BorderFactory.createEtchedBorder());
        frame.add(camera);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setVisible(true);
        idPerson = id;
        firstNamePerson = fName;
        lastNamePerson = lName;
        start();
    }

    //   public void actionPerformed(ActionEvent e) {
//
    //      if (e.getSource() == button) {
    //        frame.dispose();
    //          Menu menu = new Menu();
    //      }
    //  }
    class DaemonThread implements Runnable {
        protected volatile boolean running = false;
        @Override
        public void run() {
            synchronized (this) {
                while (running) {
                    try {
                        if (VidCap.grab()) {
                            VidCap.retrieve(WebcamImage);   //access webcam footage
                            Graphics g = camera.getGraphics();  //show footage in jlabel in gui

                            Mat colour = new Mat(); //assign footage to mat class
                            colour = WebcamImage;

                            Mat grey = new Mat();
                            cvtColor(colour, grey, COLOR_BGRA2GRAY);   //converts image to grey
                            RectVector detectedFace = new RectVector();    //instructs to draw square around any detected faces
                            training.detectMultiScale(colour, detectedFace,1.1, 1, 0, 
                                    new Size(150, 150), new Size(500, 500));    //use examples of faces from cascade

                            for (int i = 0; i < detectedFace.size(); i++) {    //searches for faces
                                Rect Faces = detectedFace.get(0);
                                rectangle(colour, Faces, new Scalar(255, 255, 255, 5));
                                Mat face = new Mat(grey, Faces);
                                opencv_imgproc.resize(face, face, new Size(160, 160));

                                if (button.getModel().isPressed()) {

                                    if (sample <= numSamples) {
                                        String folderImage = "C:\\Project\\photos\\person." + idPerson + "." + sample + ".jpg";
                                        imwrite(folderImage, face);//assigns images taken to folder for storage
                                        //counterLabel.setText(String.valueOf(sample));
                                        sample++;
                                    }
                                    if (sample > 25) {      //mutiple images must be taken for optimal recognition 
                                        generate();
                                        addDatabase();
                                        System.out.println("File Generated");
                                        stop();
                                    }
                                }
                            }

                            imencode(".bmp", WebcamImage, BytePoint);
                            Image image = ImageIO.read(new ByteArrayInputStream(BytePoint.getStringBytes()));
                            BufferedImage buffered = (BufferedImage) image;    //allows webcam to shown in JFrame

                            if (g.drawImage(buffered, 0, 0, camera.getWidth(), camera.getHeight(), 0, 0, buffered.getWidth(), buffered.getHeight(), null)) {
                                if (running == false) {
                                    System.out.println("Save Photo");
                                    this.wait();
                                }
                            }
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error when starting camera(IOEx)\n" + ex);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error when starting camera (Interrupted)\n" + ex);
                    }

                }
            }
        }
    }

    public void generate() {
        File directory = new File("C:\\Project\\photos\\");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jpg") || name.endsWith(".png");
            }
        };
        File[] file = directory.listFiles(filter);
        MatVector photo = new MatVector(file.length); //places images in folder
        Mat label = new Mat(file.length, 1, CV_32SC1);
        IntBuffer labelBuffer = label.createBuffer();

        int counter = 0;
        for (File image : file) {
            Mat photos = imread(image.getAbsolutePath(), IMREAD_GRAYSCALE);
            int labels = Integer.parseInt(image.getName().split("\\.")[1]);
            opencv_imgproc.resize(photos, photos, new Size(160, 160));
            photo.put(counter, photos);
            labelBuffer.put(counter, labels);
            counter++;
        }
        FaceRecognizer lbph = LBPHFaceRecognizer.create();
        lbph.train(photo, label);
        lbph.save("C:\\Project\\photos\\classifierLBPH.yml");   //feeds images taken to classifierLBPH to learn and train
    }

    public void addDatabase() {
        Control control;
        Model model;
        control = new Control();
        model = new Model();
        model.setId(idPerson);
        model.setFirst_name(firstNamePerson);
        model.setLast_name(lastNamePerson);
        control.insert(model);
    }

    public void stop() {
        myThread.running = false;
        VidCap.release();
        frame.dispose();
        Menu menu = new Menu();

    }

    public void start() {
        VidCap = new VideoCapture(0);
        myThread = new Register.DaemonThread();
        Thread t = new Thread(myThread);
        t.setDaemon(true);
        myThread.running = true;
        t.start();
    }

}
