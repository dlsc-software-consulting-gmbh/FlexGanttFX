[![JFXCentral](https://img.shields.io/badge/Find_me_on-JFXCentral-blue?logo=googlechrome&logoColor=white)](https://www.jfx-central.com/libraries/flexganttfx)

Find more information and demos on the FlexGanttFX website at http://flexganttfx.com

## License Notice for FlexGanttFX ##

This library is dual-licensed:

1) Open Source License (AGPLv3)
   You may use this library under the terms of the GNU Affero General Public License v3.

2) Commercial License
   If you want to use this library in a proprietary application,
   or offer it as part of a commercial product or SaaS,
   you must obtain a commercial license.

Contact: dlemmermann@gmail.com
Read more: https://www.flexganttfx.com/pages/licensing/

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
