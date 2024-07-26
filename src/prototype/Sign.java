/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prototype;

import Extra.Database;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imencode;
import org.bytedeco.opencv.global.opencv_imgproc;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2GRAY;

public class Sign {

    JFrame frame = new JFrame();
    JLabel label_photo = new JLabel("");
    JButton back = new JButton("Back");
    JOptionPane check = new JOptionPane();
    private Sign.DaemonThread myThread = null;

    VideoCapture VidCap = null;
    Mat WebcamImage = new Mat();
    CascadeClassifier training = new CascadeClassifier("C:\\Project\\photos\\haarcascade_frontalface_alt.xml");
    FaceRecognizer recognize = LBPHFaceRecognizer.create();
    BytePointer BytePoint = new BytePointer();
    RectVector FoundFace = new RectVector();

    int idPerson;

    Database connection = new Database();

    Sign() {

       // label_name.setFont(new Font(null, Font.PLAIN, 15));
       // label_name.setBounds(160, 350, 200, 50);
      //  frame.add(label_name);
        label_photo.setBounds(10, 10, 470, 330);
        label_photo.setBorder(BorderFactory.createEtchedBorder());
        frame.add(label_photo);
        back.setBounds(160, 400, 100, 50);
        frame.add(back);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setVisible(true);
        recognize.read("C:\\Project\\photos\\classifierLBPH.yml");
        recognize.setThreshold(50);
        start();
    }

    class DaemonThread implements Runnable {

        protected volatile boolean runnable = false;

        @Override
        public void run() {
            synchronized (this) {
                while (runnable) {
                    try {
                        if (VidCap.grab()) {
                            VidCap.retrieve(WebcamImage);
                            Graphics g = label_photo.getGraphics();

                            Mat colour = new Mat();
                            colour = WebcamImage;

                            Mat grey = new Mat();
                            cvtColor(WebcamImage, grey, COLOR_BGRA2GRAY);
                            RectVector detectedFace = new RectVector();
                            training.detectMultiScale(grey, detectedFace, 1.1, 1, 0, new Size(150, 150), new Size(500, 500));

                            for (int i = 0; i < detectedFace.size(); i++) {
                                
                                if (back.getModel().isPressed()) {
                                    stop();
                                }
                                Rect Faces = detectedFace.get(i);
                                rectangle(WebcamImage, Faces, new Scalar(0, 255, 0, 0));
                                Mat face = new Mat(grey, Faces);
                                opencv_imgproc.resize(face, face, new Size(160, 160));
                                
                                
                                IntPointer label = new IntPointer(1);
                                DoublePointer trust = new DoublePointer(1);
                                recognize.predict(face, label, trust);
                                int predict = label.get(0);
                                String name = null;
                                if (predict == -1) {
                                   // label_name.setText("Unknown");
                                    idPerson = 0;
                                } else {
                                    System.out.println(trust.get(0));
                                    idPerson = predict;
                                    myThread.runnable = false;
                                    VidCap.release();
//                                    Thread.sleep(1000);
                                    records();
                                }
                            }

                            imencode(".bmp", WebcamImage, BytePoint);
                            Image image = ImageIO.read(new ByteArrayInputStream(BytePoint.getStringBytes()));
                            BufferedImage buffered = (BufferedImage) image;

                            if (g.drawImage(buffered, 0, 0, label_photo.getWidth(), label_photo.getHeight(), 0, 0, buffered.getWidth(), buffered.getHeight(), null)) {
                                if (runnable == false) {
                                    //System.out.println("Save Photo");
                                    this.wait();
                                }
                            }
                        }

                    } catch (IOException | InterruptedException ex) {

                    }

                }
            }
        }
    }

    private void records() {
        SwingWorker swing = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                connection.connect();
                try {
                    String SQL = "SELECT * FROM person WHERE id = " + String.valueOf(idPerson);
                    connection.execute(SQL);
                    while (connection.result.next()) {
                        String f1 = connection.result.getString("firstname");
                        String f2 = connection.result.getString("lastname");
                        //label_name.setText(f1 +" "+ f2);
                        System.out.println("Person: " + connection.result.getString("id"));
//                        Array identity = connection.result.getArray(2);
//                        String[] person = (String[]) identity.getArray();
//                        for (int i = 0; i < person.length; i++) {
//                            System.out.println(person[i]);
//                        }
                      int response = JOptionPane.showConfirmDialog(check, "Are you " + f1 + " " + f2 + "?", 
                              "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (response == JOptionPane.YES_OPTION) {
                            try {
                                FileWriter data = new FileWriter("C:\\Users\\jemcg\\Documents\\Computer Science\\Project\\data.txt", true);
                                PrintWriter print_line = new PrintWriter(data);
                                print_line.printf("%s" + "%n", connection.result.getString("firstname") 
                                        + " " + connection.result.getString("lastname"));
                                print_line.close();
                                frame.dispose();
                                Menu menu = new Menu();
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                        } else if (response == JOptionPane.NO_OPTION) {
                            start();
                        } else if (response == JOptionPane.CLOSED_OPTION) {
                            start();
                        }
                    }
                } catch (Exception e) {
                }
                connection.disconnect();
                return null;
            }
        };
        swing.execute();
    }

    public void stop() {
        myThread.runnable = false;
        VidCap.release();
        frame.dispose();
        Menu menu = new Menu();

    }

    public void start() {
        VidCap = new VideoCapture(0);
        myThread = new Sign.DaemonThread();
        Thread t = new Thread(myThread);
        t.setDaemon(true);
        myThread.runnable = true;
        t.start();
    }
}
