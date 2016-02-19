import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by panmingzhi on 2016/2/20 0020.
 */
public class VLCVideoUtil {
    static {
        new NativeDiscovery().discover();
    }

    public static void play(JPanel jPanel, String url) {
        Component[] components = jPanel.getComponents();
        if (components.length > 0) {
            EmbeddedMediaPlayerComponent embeddedMediaPlayerComponent = (EmbeddedMediaPlayerComponent)components[0];
            embeddedMediaPlayerComponent.getMediaPlayer().stop();
            embeddedMediaPlayerComponent.getMediaPlayer().release();
            jPanel.remove(embeddedMediaPlayerComponent);
        }
        jPanel.setVisible(true);
        jPanel.setLayout(new BorderLayout());
        jPanel.setBackground(Color.black);
        EmbeddedMediaPlayerComponent embeddedMediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        jPanel.add(embeddedMediaPlayerComponent, BorderLayout.CENTER);
        embeddedMediaPlayerComponent.getMediaPlayer().prepareMedia(url);
        embeddedMediaPlayerComponent.getMediaPlayer().play();
    }

    public static void stop(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
    }
}
