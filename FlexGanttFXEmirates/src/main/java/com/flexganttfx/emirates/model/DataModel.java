/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.model;

import com.flexganttfx.emirates.EmiratesApp;
import com.flexganttfx.emirates.model.Flight.ServiceType;
import com.flexganttfx.model.Layer;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataModel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(DataModel.class.getName());
    private Layer capacityLayer = new Layer("Capacity");
    private Map<ServiceType, Layer> layerMap = new HashMap<>();
    private List<ModelObject<?, ?, ?>> rows = new ArrayList<>();

    public DataModel(DataModel.DataSet dataSet, DoubleProperty progress) throws IOException {
        switch (dataSet) {
            case SMALL:
                loadDataSet("data-small.zip", 22235, progress);
                break;
            case MEDIUM:
                loadDataSet("data-medium.zip", 148208, progress);
                break;
            case LARGE:
                loadDataSet("data-large.zip", 313189, progress);
                break;
        }
    }

    public Collection<Layer> getLayers() {
        return layerMap.values();
    }

    private void loadDataSet(final String zipArchive, final int numberOfFlightsInFile, DoubleProperty progress) throws IOException {
        ZipInputStream zin;
        try {
            zin = new ZipInputStream(EmiratesApp.class.getResourceAsStream(zipArchive));

            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.getName().startsWith("data-") && ze.getName().endsWith(".txt")) {
                    File file = new File(System.getProperty("user.home"), ze.getName());
                    if (!file.exists()) {
                        LOGGER.info("Unzipping " + ze.getName());

                        System.out.println("Extracting data file " + ze.getName() + " into your home directory.<br>This might take a while");

                        LOGGER.info("Unzipping to " + file.getAbsolutePath());

                        FileOutputStream fout = new FileOutputStream(file);

                        int size;
                        byte[] buffer = new byte[2048];

                        BufferedOutputStream bos = new BufferedOutputStream(fout, buffer.length);

                        while ((size = zin.read(buffer, 0, buffer.length)) != -1) {
                            bos.write(buffer, 0, size);
                        }

                        bos.flush();
                        bos.close();

                        zin.closeEntry();
                        fout.close();

                        LOGGER.info("Done unzipping!");
                    } else {
                        System.out.println("Data file exists, no need to extract from archive.");
                    }

                    try {
                        unmarshal(new FileReader(file), numberOfFlightsInFile + 100, progress); // 100 important
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }

                    LOGGER.info("returned from unmarshalling");
                }
            }
            LOGGER.info("yahoo");
        } catch (Exception e) {
            LOGGER.info("exception");
            e.printStackTrace();
        }

        LOGGER.info("done unzipping and unmarshalling");
    }

    private void unmarshal(FileReader reader, final int numberOfFlightsInFile, DoubleProperty progress) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();

        int counter = 0;
        while (line != null) {
            StringTokenizer st = new StringTokenizer(line, ",");

            String groupName = st.nextToken();
            String aircraftName = st.nextToken();
            String flightNo = st.nextToken();
            String serviceType = st.nextToken();
            String departureAirport = st.nextToken();
            String departureTime = st.nextToken();
            String arrivalAirport = st.nextToken();
            String arrivalTime = st.nextToken();

            if (!getGroupAircrafts().keySet().contains(groupName)) {
                getGroupAircrafts().put(groupName, new HashMap<>());
                Group groupRow = new Group(groupName);
                getRows().add(groupRow);
            }

            Map<String, Aircraft> groupAircrafts = getGroupAircrafts().computeIfAbsent(groupName, key -> new HashMap<>());
            Aircraft aircraft = groupAircrafts.computeIfAbsent(aircraftName, key -> {
                Aircraft ac = new Aircraft(aircraftName);
                getRows().add(ac);
                return ac;
            });

            Flight flight = new Flight();
            flight.setFlightNo(flightNo);
            flight.setAircraft(aircraftName);
            flight.setArrivalAirport(arrivalAirport);
            flight.setDepartureAirport(departureAirport);
            flight.setStartTime(Instant.parse(departureTime));
            flight.setEndTime(Instant.parse(arrivalTime));
            flight.setDuration(Duration.between(flight.getStartTime(), flight.getEndTime()));
            flight.setServiceType(ServiceType.valueOf(serviceType));

            Layer layer = layerMap.computeIfAbsent(flight.getServiceType(), key -> new Layer(flight.getServiceType().toString()));

            aircraft.addActivity(layer, flight);

            line = bufferedReader.readLine();

            final int fCounter = counter++;
            Platform.runLater(() -> {
                double v = (double) fCounter / (double) numberOfFlightsInFile;
                progress.set(v);
            });
        }

        getGroupAircrafts().values().forEach(map -> map.values().forEach(aircraft -> aircraft.updateInnerLines()));

        reader.close();

        LOGGER.info("done reading");
    }

    private final Map<String, Aircraft> aircrafts = new HashMap<>();

    public Map<String, Aircraft> getAircrafts() {
        return aircrafts;
    }

    private HashMap<String, Map<String, Aircraft>> groupAircrafts = new HashMap<>();

    public HashMap<String, Map<String, Aircraft>> getGroupAircrafts() {
        return groupAircrafts;
    }

    public List<ModelObject<?, ?, ?>> getRows() {
        return rows;
    }

    public Collection<Aircraft> getAircrafts(String group) {
        Map<String, Aircraft> map = getGroupAircrafts().get(group);
        return map.values();
    }

    public Layer getCapacityLayer() {
        return capacityLayer;
    }

    public enum DataSet {
        SMALL("Small Data Set"), MEDIUM("Medium Data Set"), LARGE(
                "Large Data Set");

        private String displayName;

        DataSet(String name) {
            this.displayName = name;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
