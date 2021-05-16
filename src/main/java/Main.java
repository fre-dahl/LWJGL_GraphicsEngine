import v2.core.WinConfig;
import v2.core.Window;

public class Main {

    public static void main(String[] args) {

        Window window = Window.get();
        window.start(new WinConfig());
    }
}
