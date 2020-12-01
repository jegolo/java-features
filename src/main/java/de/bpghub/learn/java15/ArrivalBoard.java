package de.bpghub.learn.java15;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ArrivalBoard {

    private record Entry(String name, String type, String time, String origin, String track) {
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        var client = HttpClient.newBuilder()
                .build();
        var request = HttpRequest.newBuilder().GET()
                .uri(URI.create("https://api.deutschebahn.com/freeplan/v1/arrivalBoard/8000036?date=2020-11-30")).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Arrays.stream(response.body().split("},?"))
                .map(ArrivalBoard::createEntry).forEach(System.out::println);
    }

    public static Entry createEntry(String data) {
        var map = data.lines()
                .filter(s -> s.matches(".*:.*"))
                .map(s -> s.stripLeading()
                            .stripTrailing()
                            .replace(",","")
                            .replace("\"",""))
                .map(v -> v.split(":\s+")
                ).collect(Collectors.toMap(k -> k[0], v -> v[1]));
        return new Entry(map.get("name"),
                         map.get("type"),
                         map.get("dateTime"),
                         map.get("origin"),
                         map.get("track"));
    }
}
