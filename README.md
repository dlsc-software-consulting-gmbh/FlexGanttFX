[![JFXCentral](https://img.shields.io/badge/Find_me_on-JFXCentral-blue?logo=googlechrome&logoColor=white)](https://www.jfx-central.com/libraries/flexganttfx)

Find more information and demos on the FlexGanttFX website at https://flexganttfx.com

## License Notice for FlexGanttFX ##

The **FlexGanttFX** software library is distributed under a **dual licensing model**.

1. **Commercial Use**  
   Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.  
   The applicable terms and conditions are set forth [in this document](commercial-enterprise-license.pdf).

2. **Open Source Use**  
   For use in open source projects, FlexGanttFX is made available under the **Apache License, Version 2.0**.  
   The full text of the license is [available here](http://www.apache.org/licenses/LICENSE-2.0).

By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.

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
