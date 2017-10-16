import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
public class Audio
{
    private Clip audioFile;
    public String Path;
    public boolean error = false;
    public double ClipLength = 0;
    public double ClipStart = 0;
    public boolean play = false;
    public boolean Loop = false;
    public Audio(String fileName, double cS, double cL)
    {
        Path = fileName;
        ClipStart = cS * 1000000;
        ClipLength = cL * 1000000;
        Refresh();
    }
    public void Play()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    play = true;
                    if (!Loop)
                    {
                        audioFile.start();
                    }
                    else
                    {
                        audioFile.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                    audioFile.addLineListener(new LineListener()
                    {
                        public void update(LineEvent event)
                        {
                            if (event.getType() != Type.STOP)
                            {
                                return;
                            }
                            //play = false;
                        }
                    });
                }
                catch (Exception e)
                {
                    
                }
            }
        }).start();
    }
    public void Stop()
    {
        audioFile.stop();
        audioFile.setFramePosition(0);
        play = false;
    }
    public boolean IsPlaying()
    {
        return audioFile.isRunning();
    }
    public void Refresh()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(new File(Path));
                    audioFile = AudioSystem.getClip();
                    audioFile.open(ais);
                    audioFile.setMicrosecondPosition((long)ClipStart);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    error = true;
                }   
            }           
        }).start();
    }
    public double GetPosition()
    {
        return audioFile.getMicrosecondPosition();
    }
}