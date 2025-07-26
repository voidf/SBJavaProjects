package jrails;

public class Html {

    private final StringBuilder content;

    @Override
    public String toString() {
        return content.toString();
    }

    Html copy()
    {
        return new Html(this.toString());
    }

    public Html seq(Html h) {
        return copy().append(this.toString()).append(h.toString());
    }

    public Html br() {
        return copy().append("<br/>");
    }

    public Html t(Object o) {
        return new Html(o == null ? "null" :o.toString());
    }

    public Html() {
        this.content = new StringBuilder();
    }

    public Html(String text) {
        this.content = new StringBuilder(text);
    }

    // Appends HTML content
    private Html append(String html) {
        this.content.append(html);
        return this;
    }

    // HTML tag methods (these are non-destructive, return new Html objects)
    public Html p(Html child) {
        return copy().append("<p>").append(child.toString()).append("</p>");
    }

    public Html div(Html child) {
        return copy().append("<div>").append(child.toString()).append("</div>");
    }

    public Html strong(Html child) {
        return copy().append("<strong>").append(child.toString()).append("</strong>");
    }

    public Html h1(Html child) {
        return copy().append("<h1>").append(child.toString()).append("</h1>");
    }

    public Html tr(Html child) {
        return copy().append("<tr>").append(child.toString()).append("</tr>");
    }

    public Html th(Html child) {
        return copy().append("<th>").append(child.toString()).append("</th>");
    }

    public Html td(Html child) {
        return copy().append("<td>").append(child.toString()).append("</td>");
    }

    public Html table(Html child) {
        return copy().append("<table>").append(child.toString()).append("</table>");
    }

    public Html thead(Html child) {
        return copy().append("<thead>").append(child.toString()).append("</thead>");
    }

    public Html tbody(Html child) {
        return copy().append("<tbody>").append(child.toString()).append("</tbody>");
    }

    public Html link_to(String text, String url) {
        return copy().append("<a href=\"").append(url).append("\">").append(text).append("</a>");
    }

    public Html form(String action, Html child) {
        return copy().append("<form action=\"").append(action)
                .append("\" accept-charset=\"UTF-8\" method=\"post\">")
                .append(child.toString()).append("</form>");
    }

    public Html submit(String value) {
        return copy().append("<input type=\"submit\" value=\"").append(value).append("\"/>");
    }

    public Html textarea(String name, Html child) {
        return copy().append("<textarea name=\"").append(name).append("\">")
                .append(child.toString()).append("</textarea>");
    }

}
