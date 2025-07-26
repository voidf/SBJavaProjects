package books;
import java.io.PrintStream;
import java.net.URLDecoder;

import jrails.JRouter;
import jrails.JServer;

public class Main {
    public static void main(String[] args) {
        // jrails.Model.reset();
        try {
            System.setErr(new PrintStream(System.err, true, "UTF-8"));
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            System.err.println("DECODETEST:" + URLDecoder.decode("%E5%88%86", "UTF-8"));
        } catch (Exception e) {
        }
        JRouter r = new JRouter();

        r.addRoute("GET", "/", BookController.class, "index");
        r.addRoute("GET", "/show", BookController.class, "show");
        r.addRoute("GET", "/new", BookController.class, "new_book");
        r.addRoute("GET", "/edit", BookController.class, "edit");
        r.addRoute("POST", "/create", BookController.class, "create");
        r.addRoute("POST", "/update", BookController.class, "update");
        r.addRoute("GET", "/destroy", BookController.class, "destroy"); // Should be DELETE but no way to do that with a
                                                                        // link
        JServer.start(r);
    }
}
