package com.hhupark.test;

import static com.hhupark.core.CoreFunc.getPlateType;
import static com.hhupark.core.CoreFunc.projectedHistogram;
import static org.bytedeco.javacpp.opencv_highgui.imread;

import java.util.Vector;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.junit.Test;

import com.hhupark.core.CharsIdentify;
import com.hhupark.core.CharsRecognise;
import com.hhupark.core.CoreFunc;
import com.hhupark.core.PlateDetect;

/**
 * @author lin.yao
 * 
 */
public class EasyPrTest {
    
    @Test
    public void testPlateRecognise() {
        //String imgPath = "res/image/test_image/test.jpg";
        String imgPath = "res/image/test_image/plate_detect.jpg";

        Mat src = imread(imgPath);
        PlateDetect plateDetect = new PlateDetect();
        plateDetect.setPDLifemode(true);
        Vector<Mat> matVector = new Vector<Mat>();
        if (0 == plateDetect.plateDetect(src, matVector)) {
            CharsRecognise cr = new CharsRecognise();
            
            for (int i = 0; i < matVector.size(); ++i) {
                String result = cr.charsRecognise(matVector.get(i));
                System.out.println("Chars Recognised: " + result);
            }
        }
    }
    @Test
    public void testPlateRecognise1() {
        //String imgPath = "res/image/test_image/test.jpg";
        String imgPath = "res/image/test_image/plate_locate.jpg";

        Mat src = imread(imgPath);
        PlateDetect plateDetect = new PlateDetect();
        plateDetect.setPDLifemode(true);
        Vector<Mat> matVector = new Vector<Mat>();
        if (0 == plateDetect.plateDetect(src, matVector)) {
            CharsRecognise cr = new CharsRecognise();
            
            for (int i = 0; i < matVector.size(); ++i) {
                String result = cr.charsRecognise(matVector.get(i));
                System.out.println("Chars Recognised: " + result);
            }
        }
    }
//
//    @Test
//    public void testPlateDetect() {
//        String imgPath = "res/image/test_image/test.jpg";
//
//        Mat src = imread(imgPath);
//        PlateDetect plateDetect = new PlateDetect();
//        plateDetect.setPDLifemode(true);
//        Vector<Mat> matVector = new Vector<Mat>();
//        if (0 == plateDetect.plateDetect(src, matVector)) {
//            for (int i = 0; i < matVector.size(); ++i) {
//                showImage("Plate Detected", matVector.get(i));
//            }
//        }
//    }

//    @Test
//    public void testPlateLocate() {
//        String imgPath = "res/image/test_image/test.jpg";
//
//        Mat src = imread(imgPath);
//
//        PlateLocate plate = new PlateLocate();
//        plate.setDebug(true);
//        plate.setLifemode(true);
//
//        Vector<Mat> resultVec = plate.plateLocate(src);
//
//        int num = resultVec.size();
//        System.out.println(num);
//        for (int j = 0; j < num; j++) {
//             showImage("Plate Located " + j, resultVec.get(j));
//        }
//        return;
//    }

    @Test
    public void testCharsRecognise() {
        String imgPath = "res/image/test_image/chars_recognise_suEUK722.jpg";

        Mat src = imread(imgPath);
        CharsRecognise cr = new CharsRecognise();
        cr.setCRDebug(true);
        String result = cr.charsRecognise(src);
        System.out.println("Chars Recognised1: " + result);
    }

    @Test
    public void testColorDetect() {
        String imgPath = "res/image/test_image/core_func_yellow.jpg";

        Mat src = imread(imgPath);

        CoreFunc.Color color = getPlateType(src, true);
        System.out.println("Color Deteted: " + color);
    }

    @Test
    public void testProjectedHistogram() {
        String imgPath = "res/image/test_image/chars_identify_E.jpg";

        Mat src = imread(imgPath);
        projectedHistogram(src, CoreFunc.Direction.HORIZONTAL);
    }

    @Test
    public void testCharsIdentify() {
        String imgPath = "res/image/test_image/chars_identify_E.jpg";
        Mat src = imread(imgPath);
        CharsIdentify charsIdentify = new CharsIdentify();
        String result = charsIdentify.charsIdentify(src, false, true);
        System.out.println(result);
    }
}
