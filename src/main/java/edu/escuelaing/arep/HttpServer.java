package edu.escuelaing.arep;

import java.net.*;
import java.nio.charset.Charset;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class HttpServer {
    private static final HttpServer _instance = new HttpServer();
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=cityname&appid="+"b11eace94f596afb1e99a0aa845f69c5";
    private static final String TEXT_MESSAGE_OK = "HTTP/1.1 200 OK\n"
                                                + "Content-Type: text/html\r\n"
                                                + "\r\n";
    private static final String JSON_MESSAGE = "HTTP/1.1 200 OK\n"
                                                + "Content-Type: application/json\r\n"
                                                + "\r\n";        
    private static final String HTTP_MESSAGE_NOT_FOUND = "HTTP/1.1 404 Not Found\n"
                                                + "Content-Type: text/html\r\n"
                                                + "\r\n";
                                        

    /**
     * Gets the current instance of the server
     * @return the current instance of the server
     */
    public static HttpServer getInstance(){
        return _instance;
    }

    /**
     * Class constructor
     */
    private HttpServer(){
        
    }

    /**
     * Begin to listen for multiusers requests in the port 35000
     * @throws IOException If the server is unable to listen for the current port 
     * @throws URISyntaxException If the formed URI is incorrect
     */
    public void start() throws IOException, URISyntaxException{
        ServerSocket serverSocket = null;
        int port = getPort();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port + ".");
            System.exit(1);
        }
        boolean running = true;
        while(running){
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            serverConnection(clientSocket);
        }
        serverSocket.close();
    }

    /**
     * Listen and handle the received requests
     * @param clientSocket current socket of the server
     * @throws IOException Any conflict related to the socket
     */
    public void serverConnection(Socket clientSocket) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        clientSocket.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.startsWith("GET")){
                System.out.println("Received from the client: " + inputLine);
                manageResource(clientSocket.getOutputStream(), inputLine);
            }
            if (!in.ready()) {
                break;
            }
        }
        out.close();
        in.close();
        clientSocket.close();
    }

    /**
     * Decide what kind of petion was received and handled according to its Content-Type
     * @param out the stream the resources need to display on the client
     * @param input the request
     */
    public void manageResource(OutputStream out, String input){
        String type = input.split(" ")[1].replace("/", "");
        if(type.length() == 0) type = "clima";
        if(type.equals("clima")){
            computeWelcomePage(out);
        } else if(type.contains("consulta?lugar=")){
            computeAPIResource(out, type);
        } else{
            default404HTMLResponse(out);
        }
    }


    /**
     * Display a simple html page to consult the api
     * @param out the stream the resource need to display on the client
     */
    public void computeWelcomePage(OutputStream out){
        String outputLine = TEXT_MESSAGE_OK;
        outputLine +=   "<!DOCTYPE html>\n"
                        + "<html>\n"
                        +       "<head>\n"
                        +           "<title>Weather Consult</title>\n"
                        +           "<meta charset=\"UTF-8\">\n"
                        +           "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.8\">\n"
                        +       "</head>\n"
                        +       "<body>\n"
                        +       "<div class='container center'>\n"
                        +           "<div class='card'>\n"
                        +           "<h1> <span class='dot'></span> CONSULT THE WHEATER API HERE <span class='dot'></span></h1>\n"
                        +           "<p>Type here the city you want to consult</p>\n"
                        +           "<div class='info-wrapper'>\n"
                        +           "<input id='inputField'>\n"
                        +           "<button id='submit'>Sent it</button>\n"
                        +       "</div>\n"
                        +       "<p><strong>Side Note: </strong> To go back from the resource just erase the last part of the URL until the last / inclusive</p>\n"
                        +      "</div>\n"
                        +      "</body>\n"
                        +       "<script>\n"
                        +           "document.addEventListener('DOMContentLoaded', () => {\n"
                        +    "document.getElementById('submit').addEventListener('click', () => {\n"
                        +        "var request = 'consulta?lugar=' + document.getElementById('inputField').value\n"
                        +        "window.location.replace(request);\n"
                        +    "})\n"
                        +"});\n"
                        +       "</script>\n"
                        + "</html>";
        try{
            out.write(outputLine.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read and write on screen the api response and returned in form of JSON
     * @param out the stream the resources need to display on the client
     * @param input the request
     */
    public void computeAPIResource(OutputStream out, String input){
        System.out.println("RECEIVED: " + input);
        String content = JSON_MESSAGE;
        try {
            input = input.replace("consulta?lugar=", "");
            InputStream is = new URL(WEATHER_URL.replaceFirst("cityname", input)).openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = br.read()) != -1) {
                sb.append((char) cp);
            }
            content += sb.toString();
            br.close();
            out.write(content.getBytes());
        } catch (IOException e) {
            System.err.format("Response not found %s%n", e);
            default404HTMLResponse(out);

        }
    }

    /**
     * Display a simple html page of 404 Error
     * @param out the stream the resource need to display on the client
     */
    private void default404HTMLResponse(OutputStream out){
        String outputline = HTTP_MESSAGE_NOT_FOUND;
        outputline +=     "<!DOCTYPE html>"
                        + "<html>"
                        +       "<head>"
                        +           "<title>404 Not Found</title>\n"
                        +           "<meta charset=\"UTF-8\">"
                        +           "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.8\">"
                        +           "<style type='text/css'>"
                        +               "h1{"
                        +                   "font-size: 150px;"
                        +                   "text-align: center;"
                        +           "</style>"
                        +       "</head>"
                        +       "<body>"
                        +           "<h1> Error 404 </h1>"
                        +       "</body>"
                        + "</html>";
        try {
            out.write(outputline.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method reads the default port as specified by the PORT variable in
     * the environment.
     *
     * Heroku provides the port automatically so you need this to run the
     * project on Heroku.
     */
    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 35000; //returns default port if heroku-port isn't set (i.e. on localhost)
    }

    public static void main(String[] args) throws IOException {
        try {
            HttpServer.getInstance().start();
        } catch (URISyntaxException e) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
