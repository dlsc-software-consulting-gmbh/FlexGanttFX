/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.map;

import com.flexganttfx.emirates.model.Aircraft;
import com.flexganttfx.emirates.model.Flight;
import com.flexganttfx.model.repository.IntervalTreeActivityRepository;
import com.opencsv.CSVReader;
import net.miginfocom.swing.MigLayout;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOpenAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapComponent extends JPanel implements DropTargetListener {

	private static final long serialVersionUID = -3601701760102617385L;

	private static Image departureImage;

	private static Image arrivalImage;

	private static Map<String, Location> airportMap;

	private static List<String[]> data;

	static {
		try {
			departureImage = ImageIO.read(MapComponent.class
					.getResource("bullet_ball_glass_yellow.png"));
			arrivalImage = ImageIO.read(MapComponent.class
					.getResource("signal_flag_checkered.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try (CSVReader reader = new CSVReader(new InputStreamReader(
				MapComponent.class.getResourceAsStream("airports.csv")))) {

			airportMap = new HashMap<String, MapComponent.Location>();

			data = reader.readAll();
			for (String[] str : data) {
				String code = str[0];
				Location loc = new Location();
				loc.lat = Double.valueOf(str[5]);
				loc.lon = Double.valueOf(str[6]);
				airportMap.put(code, loc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JMapViewer viewer;
	private boolean linked;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MapComponent() {
		viewer = new JMapViewer();
		viewer.setZoomContolsVisible(false);
		viewer.setScrollWrapEnabled(false);

		JPanel header = new JPanel();
		header.setLayout(new FlowLayout(FlowLayout.RIGHT));

		final JComboBox tileSourceBox = new JComboBox(new TileSource[] {
				new MapQuestOsmTileSource(),
				new MapQuestOpenAerialTileSource(), new BingAerialTileSource(),
				new OsmTileSource.CycleMap(), new OsmTileSource.Mapnik() });

		tileSourceBox.setSelectedItem(viewer.getTileController()
				.getTileSource());
		tileSourceBox.addActionListener(evt -> {
			TileSource ts = (TileSource) tileSourceBox.getSelectedItem();
			viewer.setTileSource(ts);
		});

		header.add(tileSourceBox);

		final JToggleButton linkViews = new JToggleButton("Link Views", linked);
		linkViews.addActionListener(evt -> setLinked(linkViews.isSelected()));
		header.add(linkViews);

		setLayout(new MigLayout("insets 0 0 0 0, wrap 1, gapy 0", "[grow]",
				"[][grow]"));
		// add(header, "grow");
		add(viewer, "grow");

		new DropTarget(this, this);
	}

	protected void setLinked(boolean linked) {
		this.linked = linked;

		if (linked) {
			populateMapBasedOnTreeSelections();
		} else {
			clearMap();
		}
	}

	private void populateMapBasedOnTreeSelections() {
		// TreeTable table = ganttChart.getPrimaryTreeTable();
		// TreePath[] selection = table.getSelectionPaths();
		// clearMap();
		// if (selection != null) {
		// for (TreePath path : selection) {
		// DefaultGanttChartNode<?, ?> node = (DefaultGanttChartNode<?, ?>) path
		// .getLastPathComponent();
		// displayNode(node);
		// }
		// }
	}

	public void display(Aircraft aircraft) {
		clearMap();

		IntervalTreeActivityRepository<Flight> repository = (IntervalTreeActivityRepository<Flight>) aircraft
				.getRepository();

		List<Flight> activities = repository.getAllActivities();
		for (Flight flight : activities) {
			display(flight);
		}
	}

	public void display(List<Flight> flights) {
		clearMap();

		for (Flight flight : flights) {
			display(flight);
		}

		viewer.setDisplayToFitMapMarkers();
	}

	public void display(Flight flight) {
		String departureStation = flight.getDepartureAirport();
		String arrivalStation = flight.getArrivalAirport();
		Location departureLocation = airportMap.get(departureStation);
		Location arrivalLocation = airportMap.get(arrivalStation);

		List<Coordinate> points = new ArrayList<Coordinate>();

		if (departureLocation != null) {
			viewer.addMapMarker(new MapMarkerImage(departureLocation.lat,
					departureLocation.lon, departureImage, true));
			points.add(new Coordinate(departureLocation.lat,
					departureLocation.lon));
		}

		if (arrivalLocation != null) {
			viewer.addMapMarker(new MapMarkerImage(arrivalLocation.lat,
					arrivalLocation.lon, arrivalImage, false));
			points.add(new Coordinate(arrivalLocation.lat, arrivalLocation.lon));
		}

		if (departureLocation != null) {
			points.add(new Coordinate(departureLocation.lat,
					departureLocation.lon));
		}

		MapPolygonImpl polygon = new MapPolygonImpl(points);
		viewer.addMapPolygon(polygon);
	}

	private void clearMap() {
		viewer.removeAllMapMarkers();
		viewer.removeAllMapPolygons();
		viewer.removeAllMapRectangles();
	}

	static class Location {
		double lat;
		double lon;
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		// Transferable trans = dtde.getTransferable();
		// try {
		// Object data = trans.getTransferData(new DataFlavor(
		// DataFlavor.javaJVMLocalObjectMimeType));
		// if (data instanceof TreeTableNode) {
		// TreeTableNode node = (TreeTableNode) data;
		// DefaultGanttChartNode<?, ?> modelNode = (DefaultGanttChartNode<?,
		// ?>) node
		// .getModelNode();
		// displayNode(modelNode);
		// } else if (data instanceof ObjectBounds) {
		// ObjectBounds bounds = (ObjectBounds) data;
		// TimelineObjectPath<?> path = bounds.getPath();
		// if (path.getTimelineObject() instanceof FlightTimelineObject) {
		// FlightTimelineObject flightTimelineObject =
		// (FlightTimelineObject) path
		// .getTimelineObject();
		// displayRow(flightTimelineObject.getUserObject());
		// }
		// }
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	class MapMarkerImage extends MapMarkerDot {

		private Image image;
		private boolean center;

		public MapMarkerImage(double lat, double lon, Image image,
				boolean center) {
			super(lat, lon);

			this.image = image;
			this.center = center;
		}

		@Override
		public void paint(Graphics g, Point p, int i) {
			int w = image.getWidth(viewer);
			int h = image.getHeight(viewer);
			if (center) {
				g.drawImage(image, p.x - 8, p.y - 8, w, h, viewer);
			} else {
				g.drawImage(image, p.x - 4, p.y - h, w, h, viewer);
			}
		}
	}

	// @Override
	// public void valueChanged(TimelineObjectSelectionEvent evt) {
	// if (linked) {
	// clearMap();
	//
	// for (LayerContainer lc : ganttChart.getLayerContainers()) {
	// for (ILayer layer : lc.getLayers()) {
	// ITimelineObjectSelectionModel selectionModel = lc
	// .getSelectionModel(layer);
	// @SuppressWarnings("rawtypes")
	// Iterator<TimelineObjectPath> selection = selectionModel
	// .getSelection();
	// while (selection.hasNext()) {
	// TimelineObjectPath<?> path = selection.next();
	// if (path.getTimelineObject() instanceof FlightTimelineObject) {
	// FlightTimelineObject flight = (FlightTimelineObject) path
	// .getTimelineObject();
	// ROW row = flight.getUserObject();
	// displayRow(row);
	// }
	// }
	// }
	// }
	// }
	// }
}
