/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.model;

import com.flexganttfx.emirates.EmiratesApp;
import com.flexganttfx.emirates.model.Flight.ServiceType;
import com.flexganttfx.extras.GanttChartStatusBar;
import com.flexganttfx.model.Layer;
import javafx.application.Platform;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataModel extends HashMap<Group, Map<String, Aircraft>> {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(DataModel.class
			.getName());

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

	private Instant start;

	private Instant end;

	private Layer capacityLayer = new Layer("Capacity");

	private Map<ServiceType, Layer> layerMap = new HashMap<>();

	private GanttChartStatusBar<?> statusBar;

	private List<ModelObject<?,?,?>> rows = new ArrayList<>();

	public DataModel(DataSet dataSet, GanttChartStatusBar<?> statusBar)
			throws IOException {
		this.statusBar = statusBar;

		switch (dataSet) {
		case SMALL:
			loadDataSet("flights-small.zip", 17785);
			break;
		case MEDIUM:
			loadDataSet("flights-medium.zip", 116423);
			break;
		case LARGE:
			loadDataSet("flights-large.zip", 244373);
			break;
		}

		for (String s : Flight.serviceTypes) {
			LOGGER.info("service type: " + s);
		}

		keySet().forEach(group -> {
			rows.add(group);
			rows.addAll(getAircrafts(group));
		});
	}

	public Collection<Layer> getLayers() {
		return layerMap.values();
	}

	private void loadDataSet(final String zipArchive, final int numberOfFlightsInFile) throws IOException {
		ZipInputStream zin = null;
		try {
			zin = new ZipInputStream(
					EmiratesApp.class.getResourceAsStream(zipArchive));

			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				if (ze.getName().endsWith(".xml")) {
					File file = new File(System.getProperty("user.home"),
							ze.getName());
					if (!file.exists()) {
						LOGGER.info("Unzipping " + ze.getName());

						System.out
								.println("Extracting data file "
										+ ze.getName()
										+ " into your home directory.<br>This might take a while");

						LOGGER.info("Unzipping to " + file.getAbsolutePath());

						FileOutputStream fout = new FileOutputStream(file);

						int size;
						byte[] buffer = new byte[2048];

						BufferedOutputStream bos = new BufferedOutputStream(
								fout, buffer.length);

						while ((size = zin.read(buffer, 0, buffer.length)) != -1) {
							bos.write(buffer, 0, size);
						}

						bos.flush();
						bos.close();

						zin.closeEntry();
						fout.close();

						LOGGER.info("Done unzipping!");
					} else {
						System.out
								.println("Data file exists, no need to extract from archive.");
					}

					try {
						unmarshal(new FileReader(file), numberOfFlightsInFile);
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

	private void unmarshal(FileReader reader, final int numberOfFlightsInFile)
			throws JAXBException, IOException {

		JAXBContext context = JAXBContext.newInstance(ROWDATA.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		final Map<String, List<Flight>> flights = new HashMap<>();

		unmarshaller.setListener(new Unmarshaller.Listener() {

			ZonedDateTime startTime = ZonedDateTime.now();

			int counter = 0;

			@Override
			public void afterUnmarshal(Object target, Object parent) {
				if (target instanceof ROW) {
					ROW row = (ROW) target;
					String resourceName = row.getResource();
					Group group = new Group(new StringTokenizer(resourceName,
							"-").nextToken());
					Map<String, Aircraft> aircraftMap = get(group);
					if (aircraftMap == null) {
						aircraftMap = new HashMap<>();
						put(group, aircraftMap);
					}

					Aircraft aircraftRow = aircraftMap.get(resourceName);
					if (aircraftRow == null) {
						aircraftRow = new Aircraft(row);
						aircraftMap.put(resourceName, aircraftRow);
					}

					Flight flight = new Flight(row);
					List<Flight> flightList = flights.get(resourceName);
					if (flightList == null) {
						flightList = new ArrayList<>();
						flights.put(resourceName, flightList);
					}

					if (!flight.isInvalid()) {
						flightList.add(flight);
					}

					Instant startTime = flight.getStartTime();
					Instant endTime = flight.getStartTime();

					if (start == null
							|| Instant.from(startTime).isBefore(start)) {
						start = Instant.from(startTime);
					}

					if (end == null || Instant.from(endTime).isAfter(end)) {
						end = Instant.from(endTime);
					}
				} else if (target instanceof ROWDATA) {
					LOGGER.info("done parsing xml file");
					LOGGER.info("setting start time to " + startTime);
				}

				counter++;

				Platform.runLater(() -> statusBar.setProgress((double) counter
						/ (double) numberOfFlightsInFile));
			}
		});

		unmarshaller.unmarshal(reader);
		reader.close();

		LOGGER.info("horizon: " + start + " to " + end);
		for (Group group : keySet()) {
			Map<String, Aircraft> map = get(group);
			for (Aircraft aircraft : map.values()) {
				LOGGER.info("looking up flights for aircraft "
						+ aircraft.getName());
				List<Flight> flightList = flights.get(aircraft.getName());
				if (flightList != null) {
					for (Flight flight : flightList) {

						Layer layer = layerMap.get(flight.getServiceType());
						if (layer == null) {
							layer = new Layer(flight.getServiceType()
									.toString());
							layerMap.put(flight.getServiceType(), layer);
						}

						aircraft.addActivity(layer, flight);
					}

					aircraft.updateInnerLines();
				}
			}
		}

		LOGGER.info("done unmarshallling");
	}

	public Set<Group> getGroups() {
		return keySet();
	}

	public List<ModelObject<?,?,?>> getRows() {
		return rows;
	}

	public Collection<Aircraft> getAircrafts(Group group) {
		Map<String, Aircraft> map = get(group);
		return map.values();
	}

	public Instant getStartTime() {
		return start;
	}

	public Instant getEndTime() {
		return end;
	}

	public Layer getCapacityLayer() {
		return capacityLayer;
	}
}
