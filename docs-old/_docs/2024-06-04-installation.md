---
layout: doc
title: Installation
date: 2021-01-27 8:39:00 +0600
post_image: assets/images/service-icon3.png
category_name: 1. Getting Started
tags: [Getting Started]
toc: true
---
# Installation

The following steps describe how to install FlexGanttFX in your project. 

## Install Java
With Java 11 and higher JavaFX is no longer bundled with the JDKs from Oracle, so you have to make sure to 
download and install a JDK 11+ distribution that does include it, e.g. from Azul.

## Download the Distribution
Go to the downloads section of FlexGanttFX.com and download the latest release. The download file will be a 
ZIP archive containing the required JAR files, demos, tutorials, API documentation, etc...

## Unpack the Distribution
Unzip the distribution to your local file system. Once unpacked you will see the following content: 

![Installation Directory](/assets/images/docs/directory.png)

The distribution contains the following subfolders:

- css - copies of the stylesheets used by FlexGanttFX (the originals are included in the JAR file)
- docs - the API documentation of FlexGanttFX
- ext - third-party JAR files required for running FlexGanttFX
- legal - the license agreements as PDF files
- lib - the FlexGanttFX libraries
- tutorial - files to get you started

## Add JAR files to Classpath
Assuming that you downloaded release 11.12.5 then add the following files (located in the distribution's lib folder) 
to your classpath.

- flexganttfx-core-11.12.8.jar - contains various utility classes and the licensing support
- flexganttfx-model-11.12.8.jar - all classes related to the data model (activities, rows, repositories)
- flexganttfx-view-11.12.8.jar - the view classes, such as the actual Gantt Chart control
- flexganttfx-extras-11.12.8.jar - additional classes such as a toolbar and a statusbar

Add the files located in the ext folder to your classpath.
- controlsfx.jar - the distribution of the ControlsFX project
- license4j.jar - code for supporting the licensing concepts

## Create Application Class
The following listing shows the most basic setup required to launch a Gantt chart user interface.

~~~ java

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.flexganttfx.view.GanttChart;

public class MyFirstGanttChart extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // <- Our Gantt chart
        GanttChart<?> gantt = new GanttChart<>();
     
        Scene scene = new Scene(gantt);
     
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.sizeToScene();
        stage.show();   
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

~~~