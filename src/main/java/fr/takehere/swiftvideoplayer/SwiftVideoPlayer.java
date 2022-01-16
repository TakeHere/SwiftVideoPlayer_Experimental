package fr.takehere.swiftvideoplayer;

import fr.takehere.swiftvideoplayer.display.GameFrame;
import fr.takehere.swiftvideoplayer.display.GamePane;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.Encoder;
import java.io.*;
import java.nio.Buffer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SwiftVideoPlayer {

    private static SwiftVideoPlayer instance;
    public double videoFrameRate;
    public List<BufferedImage> bufferedImages;

    public static void main(String[] args) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        instance = new SwiftVideoPlayer();

        instance.bufferedImages = new ArrayList<>();
        FileSystem fs = FileSystems.getDefault();
        Java2DFrameConverter bimConverter = new Java2DFrameConverter();

        String videoPath = "D:" + fs.getSeparator() + "Bureau" + fs.getSeparator() + "bazar" + fs.getSeparator() + "video.mp4";
        //String audioPath = "D:" + fs.getSeparator() + "Bureau" + fs.getSeparator() + "bazar" + fs.getSeparator() + "audio.mp3";
        String framesPath = "D:" + fs.getSeparator() + "Bureau" + fs.getSeparator() + "bazar" + fs.getSeparator() + "frames";
        File framesDir = new File(framesPath);

        File file = new File(videoPath);

        if (!file.exists()){
            System.err.println("Aucun fichier vidéo trouvé");
            System.exit(1);
        }

        deleteFolder(framesDir);

        /*
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(audioPath, 2);
        recorder.setAudioOption("crf", "0");
        recorder.setAudioQuality(0);
        //bit rate
        recorder.setAudioBitrate(192000);
        //sample rate
        recorder.setSampleRate(44100);
        recorder.setAudioChannels(2);
        //encoder
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_MP3);
        //start
        recorder.start();
        */


        System.out.println("Enregistrement des images...");

        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file);
        frameGrabber.start();
        instance.videoFrameRate = frameGrabber.getVideoFrameRate();

        if (!framesDir.exists()) framesDir.mkdir();

        int frameNumber = 0;

        for (int i = 0; i < frameGrabber.getLengthInVideoFrames(); i++) {
            Frame frame = frameGrabber.grab();

            if (frame.image != null){
                BufferedImage image = bimConverter.convert(frame);
                System.out.println(i + "/" + frameGrabber.getLengthInVideoFrames());
                ImageIO.write(image, "png", new File( framesDir + fs.getSeparator() + "frame" + frameNumber + ".png"));
                frameNumber++;
            }else {
                //recorder.record(frame);
            }
        }

        System.out.println("Lecture des images...");
        File[] files = framesDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            try {
                BufferedImage image = ImageIO.read(files[i]);
                instance.bufferedImages.add(image);
                System.out.println(i + "/" + files.length);
            } catch (final IOException e) {

            }
        }

        frameGrabber.stop();
        //recorder.close();

        System.out.println("Toutes les images ont été converties ! Place à la video");
        System.out.println("Lancement du son");
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                //Decoder decoder = new Decoder();
                //FileInputStream in = new FileInputStream(new File(audioPath));
                //BufferedInputStream bin = new BufferedInputStream(in, 128 * 1024);
                //decoder.play(file.getName(), bin);
                //in.close();

                //decoder.stop();
            }
        });
        t1.start();

        GameFrame gameFrame = GameFrame.get();
    }

    long launchTime = System.currentTimeMillis();
    long lastTime = System.currentTimeMillis();
    int fps = 0;
    float maxDeltaTime = 100f;

    public void gameLoop() {
        float minDeltaTime = (float) (1000 / videoFrameRate);

        long currentTime = System.currentTimeMillis();
        float deltaTime = ( currentTime -  lastTime) / 1000.0f;

        if ( deltaTime < minDeltaTime )
            deltaTime = minDeltaTime;
        else if ( deltaTime > maxDeltaTime )
            deltaTime = maxDeltaTime;
        lastTime = currentTime;



        fps++;
        if (System.currentTimeMillis() - launchTime > 1000){
            GameFrame.get().setTitle(GameFrame.title + " | fps: " + fps);
            launchTime += 1000;
            fps = 0;
        }

        try {
            Thread.sleep((long) deltaTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GamePane.get().repaint();
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    public static SwiftVideoPlayer getInstance() {
        return instance;
    }
}
