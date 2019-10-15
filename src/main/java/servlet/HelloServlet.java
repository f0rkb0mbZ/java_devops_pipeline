package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import servlet.DataBase;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MyServlet", urlPatterns = { "/hello" })
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DataBase db = new DataBase();
        List<String> arr = new ArrayList<String>();
        arr = db.getData();
        // ServletOutputStream out = resp.getOutputStream();
        PrintWriter out = resp.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println(
                "<link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css' type='text/css'>");
        out.println("<TITLE>Hello Servlet</TITLE>");
        out.println("</HEAD>");
        out.println("<BODY>");
        out.println("Hello Servlet");

        out.println("<div class='table-responsive'>");
        out.println("<Table>");
        out.println("<tr>");
        out.println("<th>Continent</th>");
        out.println("</tr>");

        int i = 0;
        while (arr.size() > i) {
            out.println("<tr>");
            out.printf("<td>%s</td>\n", arr.get(i));
            out.println("</tr>");
            i++;
        }

        out.println("</Table>");
        out.println("</div>");

        out.println("</BODY>");
        out.println("</HTML>");
    }

}
