package nl.yeex.configuration.client;

public class ConfigClientApp {

    public static void main(String[] argv)  {
        Client client = new Client();
//        final String host = "configserver-surprised-turtle.cfapps.io";
//        client.executeQuery(host, 80);
//        client.getModel(host, 80, "sampleconfig");
        client.executeDbPediaQuery();
    }
}
