package nl.yeex.configuration;

import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.fuseki.system.FusekiLogging;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;


public class DatasetServer {

    private static final String DATAPROVIDER_URL = "https://www.afm.nl/export.aspx?type=8a46a4ef-f196-4467-a7ab-1ae1cb58f0e7&format=xml";
    private static final int PORT = 8080;
    private static final int DELAY_HOUR = 60 * 60 * 1000;
    private static final int PERIOD_HOUR = 60 * 60 * 1000;
    private Logger logger = Logger.getLogger(DatasetServer.class.getName());

    public static void main (String args[]) {
        configureLogging();
        String staticFileBase = (args.length > 0)? args[0]: null;
        new DatasetServer(staticFileBase);
    }

    public DatasetServer(String staticFileBase) {

        File xmlFile = getDataFromProvider(DATAPROVIDER_URL, "short-", ".xml");
        Dataset shortDataset = getShortSellDataset(xmlFile);

        final FusekiServer.Builder fusekiServerBuilder = FusekiServer.create()
                .port(PORT)
                .enablePing(true)
                .enableStats(true);
        if (shortDataset != null) {
            logger.info("Adding short dataset");
            fusekiServerBuilder.add("short", shortDataset);
        }
        if (staticFileBase != null)  {
            logger.info("Adding staticFileBase " + staticFileBase);
            fusekiServerBuilder.staticFileBase(staticFileBase);
        }
        final FusekiServer server =  fusekiServerBuilder.build();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                server.stop() ;
            }
        });
        server.start() ;

        final String uri = server.getJettyServer().getURI().toString();
        scheduleDatasetUpdates(uri, "short", DELAY_HOUR, PERIOD_HOUR);
    }

    private Dataset getShortSellDataset(File datafile)  {
        try{
            final String resource = getResource("short.xslt");
            if (resource == null)  {
                logger.error("Stylesheet not found.");
                throw new RuntimeException("Stylesheet not found.");
            }
            File stylesheet = new File(new URI(resource));
            return transformXmlToRdf(datafile, stylesheet);
        } catch (URISyntaxException e) {
            logger.error("Invalid URL", e);
        }
        return null;
    }

    private void scheduleDatasetUpdates(final String uri, final String datasetName, int delay, int period) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                logger.info("Start update task");
                File datafile = getDataFromProvider(DATAPROVIDER_URL, "short-", ".xml");
                Dataset updatedDataset = getShortSellDataset(datafile);
                final String datasetUpdateUri = uri + "/" + datasetName + "/data";
                logger.info("Update uri: "+ datasetUpdateUri);
                DatasetAccessor datasetAccessor = DatasetAccessorFactory.createHTTP(datasetUpdateUri);
                datasetAccessor.putModel(updatedDataset.getDefaultModel());
                logger.info("End update task");
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, delay, period);
    }


    public File getDataFromProvider(final String urlString, final String fileName, final String suffix)  {
        try {
            logger.info("Try to retrieve data from provider " + urlString);
            URL url = new URL(urlString);
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            File downloadedData = File.createTempFile(fileName, suffix);
            logger.info("Writing temporary file (received from provider)" + downloadedData.getAbsolutePath());
            FileOutputStream fileOutputStream = new FileOutputStream(downloadedData);
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileChannel.close();
            return downloadedData;
        } catch (IOException e) {
            logger.error("Failed to retrieve data from URL", e);
        }
        return null;
    }

    public  Dataset transformXmlToRdf(File datafile, File stylesheet) {
        try {
            StreamSource stylesource = new StreamSource(stylesheet);
            Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(new StreamSource(datafile), result);
            final File rdfOutputFile = File.createTempFile("short", ".rdf");
            logger.info("Writing temporary file (generated rdf)" + rdfOutputFile.getAbsolutePath());
            final FileWriter fileWriter = new FileWriter(rdfOutputFile);
            fileWriter.write(result.getWriter().toString());
            fileWriter.flush();

            Dataset dataset = DatasetFactory.createTxnMem();
            Model model = RDFDataMgr.loadModel(rdfOutputFile.toURI().toString());
            dataset.setDefaultModel(model);
            return dataset;

        } catch (TransformerConfigurationException e) {
            logger.error("Transformation failed", e);
        } catch (TransformerException e) {
            logger.error("Transformation failed", e);
        } catch (IOException e) {
            logger.error("I/O failed", e);
        }
        return null;
    }

    private static Dataset getDataset(String name)  {
        Dataset dataset = DatasetFactory.createTxnMem();
        Model model = RDFDataMgr.loadModel(getResource(name));
        dataset.setDefaultModel(model);
        return dataset;
    }

    private static String getResource(String name) {
        ClassLoader loader = DatasetServer.class.getClassLoader();
        if (loader == null)  {
            return ClassLoader.getSystemResource(name).toString();
        } else {
            final URL resource = loader.getResource(name);
            return (resource != null)? resource.toString(): null;
        }
    }

    private static void configureLogging() {
        FusekiLogging.setLogging();
        LogCtl.setLevel(Fuseki.serverLogName,  "TRACE");
        LogCtl.setLevel(Fuseki.actionLogName,  "TRACE");
        LogCtl.setLevel(Fuseki.requestLogName, "TRACE");
        LogCtl.setLevel(Fuseki.adminLogName,   "TRACE");
        LogCtl.setLevel("org.eclipse.jetty",   "INFO");
    }
}
