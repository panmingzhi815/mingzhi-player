import com.alee.laf.WebLookAndFeel;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

/**
 * Created by panmingzhi on 2016/2/19 0019.
 */
public class Player {

    private JTabbedPane tabbedPane1;
    private JTextField textField1;
    private JButton 播放器Button;
    private JEditorPane editorPane1;
    private JPanel root;
    private JButton 转到Button;
    private JPanel webPanel;
    private JButton 停止Button;
    private JPanel videoPane;
    private JButton 主页Button;
    private WebEngine eng;
    private WebView view;

    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len = inStream.read(buffer)) !=-1 ){
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();//网页的二进制数据
        outStream.close();
        inStream.close();
        return data;
    }

    public Player() {
        转到Button.addActionListener(e -> {
            String text = textField1.getText();
            Platform.runLater(() -> eng.load(text));
        });
        播放器Button.addActionListener(e -> {
            tabbedPane1.setSelectedIndex(0);
            String text = textField1.getText();
//            VLCVideoUtil.play(videoPane,text);

            String url = "http://www.flvcd.com/parse.php?kw="+text;
            try {
                String parm = "qing=super&qtudou=null&qyouku=null&q56=null&qsohu=null&qletv=null&qqiyi=null&qcntv=null&qqq=null&qhunantv=null&qfun=null&qku6=null&qyinyuetai=null&qtangdou=null&qxunlei=null&qsina=null&qpptv=null&qpps=null&qm1905=null&qbokecc=null&q17173=null&qcuctv=null&q163=null&q51cto=null&qbaofeng=null&pop=yes&xia=auto&open=yes&adrandom=yes&Submit=%B1%A3%B4%E6";
                URL url1 = new URL("http://www.flvcd.com/myset.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setReadTimeout(2000);
                urlConnection.getOutputStream().write(parm.getBytes());
                readInputStream(urlConnection.getInputStream());

                String session_value = urlConnection.getHeaderField("Set-Cookie" );

                Connection connect = Jsoup.connect(url);
                connect.cookie("PHPSESSID",(session_value.split(";")[0]).split("=")[1]);
                Document document = connect.get();
                Elements a = document.getElementsByTag("a");
                for (Element element : a) {
                    System.out.println(element.attr("href"));
                    if (element.hasAttr("onclick")) {
                        System.out.println("play:"+element.attr("href"));
                        VLCVideoUtil.play(videoPane,element.attr("href"));
                        Platform.runLater(()-> view.toBack());
                        return;
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        主页Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Platform.runLater(()->eng.load("http://www.youku.com/"));
            }
        });
    }

    public static void main(String[] args) throws IOException {
        WebLookAndFeel.globalControlFont  = new FontUIResource("隶书",0, 12);
        WebLookAndFeel.install();

        JFrame frame = new JFrame("Player");
        frame.setIconImage(ImageIO.read(Player.class.getResourceAsStream("logo.png")));
        Player player = new Player();
        frame.setContentPane(player.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        player.includeJavafxWebView(player.webPanel);
    }

    public void includeJavafxWebView(JPanel webPanel){
        final JFXPanel webBrowser = new JFXPanel();
        webPanel.setLayout(new BorderLayout());
        webPanel.add(webBrowser, BorderLayout.CENTER);

        Platform.runLater(() -> {
            Group root = new Group();
            Scene scene = new Scene(root);
            webBrowser.setScene(scene);
            Double widthDouble = new Integer(webPanel.getWidth()).doubleValue();
            Double heightDouble = new Integer(webPanel.getHeight()).doubleValue();

            view = new WebView();
            view.setMinSize(widthDouble, heightDouble);
            view.setPrefSize(widthDouble, heightDouble);
            eng = view.getEngine();
            root.getChildren().add(view);

            webPanel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    Double widthDouble = new Integer(webPanel.getWidth()).doubleValue();
                    Double heightDouble = new Integer(webPanel.getHeight()).doubleValue();

                    view.setMinSize(widthDouble, heightDouble);
                    view.setPrefSize(widthDouble, heightDouble);
                }
            });

            eng.locationProperty().addListener((observable, oldValue, newValue) -> {
                textField1.setText(newValue);
            });
        });
    }

}
