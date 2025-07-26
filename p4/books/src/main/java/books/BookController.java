package books;

import java.util.List;
import java.util.Map;

import jrails.Controller;
import jrails.Html;
import jrails.Model;

public class BookController extends Controller {

    public static Html index(Map<String, String> params) {
        List<Book> books = Model.all(Book.class);
        // System.out.println("ALLCOUNT:" + books.size());
        var out = BookView.index(books);
        // System.err.println("OUT:" + out);
        return out;
    }

    public static Html show(Map<String, String> params) {
        int id = Integer.parseInt(params.get("id"));
        Book b = Model.find(Book.class, id);
        return BookView.show(b);
    }

    public static Html new_book(Map<String, String> params) {
        Book b = new Book();
        b.title = "";
        b.author = "";
        b.num_copies = 0;
        return BookView.new_book(b);
    }

    public static Html edit(Map<String, String> params) {
        int id = Integer.parseInt(params.get("id"));
        Book b = Model.find(Book.class, id);
        return BookView.edit(b);
    }

    public static Html create(Map<String, String> params) {
        System.out.println("enter create " + params.toString());
        Book b = new Book();
        b.title = params.get("title");
        b.author = params.get("author");
        // System.err.println("SB1" + b.toString());
        var num_copies_str = params.get("num_copies");
        if (num_copies_str != null && !num_copies_str.isEmpty()) {
            b.num_copies = Integer.parseInt(num_copies_str);
        } else {
            b.num_copies = 0;
        }
        // System.err.println("SB!" + b.toString());
        b.save();
        return BookView.show(b);
    }

    public static Html update(Map<String, String> params) {
        int id = Integer.parseInt(params.get("id"));
        Book b = Model.find(Book.class, id);
        b.title = params.get("title");
        b.author = params.get("author");
        var num_copies_str = params.get("num_copies");
        if (num_copies_str != null && !num_copies_str.isEmpty()) {
            b.num_copies = Integer.parseInt(num_copies_str);
        } else {
            b.num_copies = 0;
        }
        // b.num_copies = Integer.parseInt(params.get("num_copies"));
        b.save();
        return BookView.show(b);
    }

    public static Html destroy(Map<String, String> params) {
        int id = Integer.parseInt(params.get("id"));
        Book b = Model.find(Book.class, id);
        b.destroy();
        List<Book> books = Model.all(Book.class);
        return BookView.index(books);
    }
}
