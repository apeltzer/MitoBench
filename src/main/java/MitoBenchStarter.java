import javafx.application.Application;
import view.MitoBenchWindow;

/**
 * Created by neukamm on 03.11.16.
 */
public class MitoBenchStarter {

    /**
     * starts the main Mitobench window
     *
     * @param args
     */
    public static void main(String[] args)
    {
        new Thread(() -> Application.launch(MitoBenchWindow.class)).start();

    }
}
