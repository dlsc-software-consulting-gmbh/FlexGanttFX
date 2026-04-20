This sample focuses on printing a Gantt chart. It shows a simple workflow where the chart is rendered to an image snapshot, scaled to the printable area, and then sent to the JavaFX printing API.

```java 
private void print() {
    MSProjectGanttChart newChart = new MSProjectGanttChart();

    Scene scene = new Scene(newChart, 2000, 1000);
    newChart.getGraphics().getRowPanes().forEach(pane -> pane.getCanvas().draw());
    Stage stage = new Stage();
    stage.setScene(scene);
    stage.sizeToScene();
    newChart.applyCss();

    SnapshotParameters params = new SnapshotParameters();
    WritableImage image = new WritableImage((int) newChart.getWidth(), (int) newChart.getHeight());

    WritableImage snapshot = newChart.snapshot(params, image);
    ImageView node = new ImageView(snapshot);

    Printer printer = Printer.getDefaultPrinter();
    PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
    double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInParent().getWidth();
    double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
    double scale = Math.min(scaleX, scaleY);
    node.getTransforms().add(new Scale(scale, scale));

    PrinterJob job = PrinterJob.createPrinterJob();
    if (job != null) {
        boolean okPageSetup = job.showPageSetupDialog(gc.getScene().getWindow());
        if (okPageSetup) {
            boolean okPrintDialog = job.showPrintDialog(gc.getScene().getWindow());
            if (okPrintDialog) {
                boolean success = job.printPage(node);
                if (success) {
                    job.endJob();
                }
            }
        }
    }
}
```
