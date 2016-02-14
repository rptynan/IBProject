package twikfeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TwikfeedServlet
 */
@WebServlet("/TwikfeedServlet")
public class TwikfeedServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TwikfeedServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("text/html");
	    // Here is the decoding of the request
	    
	    String serverAddress = "";
	    String type = request.getParameter("Type");
	    if(type.equals("Trends")){
	        serverAddress = "Trends?stuff";
	    }
	    
	    if(type.equals("Articles")){
	        serverAddress = "Articles?id=" + request.getParameter("id");
	        
	    }
	    if(type.equals("Tweets")){
            serverAddress = "Tweets?id=" + request.getParameter("id");
            
        }
	    if(!serverAddress.equals("")){
            BufferedReader r = null;
            try {
                r = new BufferedReader(new InputStreamReader(new URL(
                        "http://localhost:90/" + serverAddress).openStream()));
            } catch (IOException e) {
               
                e.printStackTrace();
            }
            String str = null;
            StringBuilder sb = new StringBuilder(32768);
            try {
                while ((str = r.readLine()) != null) {
                    
                    sb.append(str);
                }
            } finally {
                r.close();
            }
            System.out.println(sb.toString());
           
            response.getWriter().println(
                    sb.toString());
	    }else  response.getWriter().println(
                "[]");
	    
	}

}
