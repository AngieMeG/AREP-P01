package edu.escuelaing.arep;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Unit test for simple App.
 * @author Angie Medina
 */
public class AppTest {
    private static final String URL_STRING = "https://weatherconsult.herokuapp.com/";
    private static final String URL_STRING_JSON = "https://weatherconsult.herokuapp.com/consulta?lugar=";


    /**
     * Test if the url is been deployed by heroku
     */
    @Test
    public void shouldFindPage(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(URL_STRING).openStream()));
        } catch(IOException e) {
            fail("Resource: " + URL_STRING + " was not found.");
        }
    }

    /**
     * Test the static resource that is html
     */
    @Test
    public void shouldReturnHTMLContent(){
        try{
            URL urlWelcomePage = new URL(URL_STRING);
            URLConnection u  = urlWelcomePage.openConnection();
            String type = u.getHeaderField("Content-Type");
            Assert.assertEquals("text/html", type);
        } catch(IOException e) {
            fail("Resource: " + URL_STRING + " was not found.");
        }
    }

    /**
     * Test the format of the request
     */
    @Test
    public void shouldReturnJSONContent(){
        try{
            URL urlWelcomePage = new URL(URL_STRING_JSON + "london");
            URLConnection u  = urlWelcomePage.openConnection();
            String type = u.getHeaderField("Content-Type");
            Assert.assertEquals("application/json", type);
        } catch(IOException e) {
            fail("Resource: " + URL_STRING_JSON + " was not found.");
        }
    }
}
