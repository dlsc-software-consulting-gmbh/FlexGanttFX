/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.experimental;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.PopOver;

public class AccordionPopOver extends PopOver {

    public AccordionPopOver() {
		super();

		skinProperty().addListener(
				(value, oldSkin, newSkin) -> {
					StackPane stackPane = (StackPane) newSkin.getNode();
					stackPane.getStylesheets().add(
							AccordionPopOver.class.getResource(
									"accordion-popover.css").toExternalForm());
				});

		final BorderPane borderPane = new BorderPane();

		headerProperty().addListener(
				(value, oldNode, newNode) -> borderPane.setTop(newNode));

        Accordion accordion = new Accordion();
		borderPane.setCenter(accordion);

		Bindings.bindContentBidirectional(getPanes(), accordion.getPanes());
		Bindings.bindBidirectional(expandedPaneProperty(),
				accordion.expandedPaneProperty());

		setHeader(new PopOverHeader<Node>());

		footerProperty().addListener((value, oldNode, newNode) -> {
			borderPane.setBottom(newNode);

			if (newNode != null) {
				String style = "footer";
				if (!newNode.getStyleClass().contains(style)) {
					newNode.getStyleClass().add(style);
				}
			}
		});

		setContentNode(borderPane);
	}

	// header support

	private final ObjectProperty<Node> header = new SimpleObjectProperty<Node>(
			this, "header");

	public final ObjectProperty<Node> headerProperty() {
		return header;
	}

	public final Node getHeader() {
		return headerProperty().get();
	}

	public final void setHeader(Node node) {
		headerProperty().set(node);
	}

	// footer support

	private final ObjectProperty<Node> footer = new SimpleObjectProperty<Node>(
			this, "footer");

	public final ObjectProperty<Node> footerProperty() {
		return footer;
	}

	public final Node getFooter() {
		return footerProperty().get();
	}

	public final void setFooter(Node node) {
		footerProperty().set(node);
	}

	// panes

	private final ObservableList<TitledPane> panes = FXCollections
			.observableArrayList();

	public final ObservableList<TitledPane> getPanes() {
		return panes;
	}

	// Expanded pane support

	private final ObjectProperty<TitledPane> expandedPane = new SimpleObjectProperty<TitledPane>(
			this, "expandedPane");

	public final ObjectProperty<TitledPane> expandedPaneProperty() {
		return expandedPane;
	}

	public final void setExpandedPane(TitledPane titledPane) {
		expandedPaneProperty().set(titledPane);
	}

	public final TitledPane getExpanedPane() {
		return expandedPane.get();
	}
}
