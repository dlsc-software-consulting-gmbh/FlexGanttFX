[![JFXCentral](https://img.shields.io/badge/Find_me_on-JFXCentral-blue?logo=googlechrome&logoColor=white)](https://www.jfx-central.com/libraries/flexganttfx)

## Build Instructions ##

Simple run "mvn install" on the parent project **FlexGanttFX**. Once completed you will find several assemblies in the **FlexGanttFXAssembly** project.

* flexganttfx-x.y.z-SNAPSHOT-bin -> contains the binary release as found on the website
* flexganttfx-x.y.z-SNAPSHOT-src -> contains the source code release

## Projects ##

The most important projects are:

* FlexGanttFX (parent project)
* FlexGanttFXCore (licensing, logging)
* FlexGanttFXModel (model classes, rows, activities, repositories, etc...)
* FlexGanttFXView (all primary controls: GanttChart, etc...)
* FlexGanttFXExtras (several extra controls, statusbar, toolbar, radar, layers)
