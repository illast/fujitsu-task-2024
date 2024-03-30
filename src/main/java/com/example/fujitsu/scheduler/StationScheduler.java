package com.example.fujitsu.scheduler;

import com.example.fujitsu.model.Station;
import com.example.fujitsu.service.StationService;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@EnableScheduling
@RequiredArgsConstructor
@Component
public class StationScheduler {

    private final StationService stationService;

    private static final Set<String> VALID_STATION_NAMES = new HashSet<>(Set.of("Tallinn-Harku", "Tartu-Tõravere", "Pärnu"));
    private static final String URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    @Scheduled(cron = "*/10 * * * * *")
    public void getObservations() {
        try {
            Document document = getObservationsDocument();
            if (document != null) {
                processObservations(document);
            } else {
                System.out.println("Failed to retrieve observations document.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Document getObservationsDocument() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    String xmlResponse = response.body().string();
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    InputSource is = new InputSource(new StringReader(xmlResponse));
                    return builder.parse(is);
                } else {
                    System.out.println("Response body is null");
                }
            } else {
                System.out.println("Request failed with response code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processObservations(Document document) {
        Element observations = document.getDocumentElement();
        long timestamp_long = Long.parseLong(observations.getAttribute("timestamp"));
        LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp_long), ZoneId.systemDefault());
        NodeList nodeList = observations.getElementsByTagName("station");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            processStation(element, timestamp);
        }
    }

    private void processStation(Element element, LocalDateTime timestamp) {
        String name = element.getElementsByTagName("name").item(0).getTextContent();
        if (VALID_STATION_NAMES.contains(name)) {
            Station station = createStation(element, timestamp, name);
//            stationService.addStation(station);
            System.out.println(station);
        }
    }

    private Station createStation(Element element, LocalDateTime timestamp, String name) {
        Integer wmoCode = Integer.parseInt(element.getElementsByTagName("wmocode").item(0).getTextContent());
        Double airTemperature = Double.parseDouble(element.getElementsByTagName("airtemperature").item(0).getTextContent());
        Double windSpeed = Double.parseDouble(element.getElementsByTagName("windspeed").item(0).getTextContent());
        String phenomenon = element.getElementsByTagName("phenomenon").item(0).getTextContent();

        return Station.builder()
                .name(name)
                .wmoCode(wmoCode)
                .airTemperature(airTemperature)
                .windSpeed(windSpeed)
                .phenomenon(phenomenon)
                .timestamp(timestamp)
                .build();
    }
}
