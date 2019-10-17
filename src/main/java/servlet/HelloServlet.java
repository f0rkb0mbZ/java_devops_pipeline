package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import servlet.DataBase;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MyServlet", urlPatterns = { "/hello" })
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DataBase db = new DataBase();
        ArrayList<ArrayList<String>> totalData = new ArrayList<ArrayList<String>>();
        totalData = db.getData();
        // ServletOutputStream out = resp.getOutputStream();
        PrintWriter out = resp.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println(
                "<link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css' type='text/css'>");
        out.println("</HEAD>");
        out.println("<BODY>");
        out.println("<div class='container'>");
        out.println("<h2>DataBase Checking</h2>");
        out.println("<table class=\"table table-bordered\">");
        out.println("<thead><tr><th>Continent</th><th>Region</th><th>Local Name</th></tr></thead>");
        out.println("<tbody>");
        int i = 0;
        while (totalData.get(0).size() > i) {
            out.println("<tr>");
            out.printf("<td>%s</td>\n", totalData.get(0).get(i));
            out.printf("<td>%s</td>\n", totalData.get(1).get(i));
            out.printf("<td>%s</td>\n", totalData.get(2).get(i));
            out.println("</tr>");
            i++;
        }
        out.println("</tbody>");
        out.println("</table>");
        out.println("</div>");
        out.println("</BODY>");
        out.println("</HTML>");
    }

}