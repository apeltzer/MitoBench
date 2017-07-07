package view.charts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import view.menus.VisualizationMenu;
import view.table.controller.TableControllerUserBench;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neukamm on 29.11.16.
 */
public class StackedBar extends AChart{

    private List< XYChart.Series<String, Number>> seriesList = new ArrayList<>();
    private StackedBarChart<String, Number> sbc;
    private TabPane tabPane;
    private final Glow glow = new Glow(.5);
    private VisualizationMenu graphicsMenu;

    public StackedBar(String title, TabPane vBox, VisualizationMenu graphicsMenu) {
        super("", "Frequency in %", graphicsMenu.getLogClass());

        tabPane = vBox;
        this.graphicsMenu = graphicsMenu;

        // set autoranging to false to allow manual settings
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);

        sbc = new StackedBarChart<>(xAxis, yAxis);
        sbc.setTitle(title);
        sbc.prefWidthProperty().bind(tabPane.widthProperty());
        sbc.setAnimated(false);
        sbc.setCategoryGap(20);
        sbc.setLegendSide(Side.RIGHT);
        //sbc.setStyle("-fx-font-size: " + 20 + "px;");

        setContextMenu(sbc, vBox);

    }


    /**
     * This method adds data to the barplot as series
     *
     * @param data
     * @param name  name of the data set
     */
    public void addSeries(List<XYChart.Data<String, Number>> data, String name){
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        series.setName(name);

        for(int i = 0; i < data.size(); i++){
            series.getData().add(data.get(i));
        }

        this.seriesList.add(series);

    }


    /**
     * This method cleans up all data
     */
    public void clearData(){
        sbc.getData().clear();
        seriesList.clear();
        xAxis.getCategories().clear();
    }


    /**
     * This method adds a tooltip to the chart, which provides information such as the name of the Haplogroup and their
     * occurrences.
     *
     * @param
     */
    public void addTooltip(){

        for (final XYChart.Series<String, Number> series : sbc.getData()) {
            for (final XYChart.Data<String, Number> data : series.getData()) {
                Tooltip tooltip = new Tooltip();
                data.getNode().setOnMouseMoved(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        // +15 moves the tooltip 15 pixels below the mouse cursor;
                        tooltip.show(data.getNode(), event.getScreenX(), event.getScreenY() + 15);
                        tooltip.setText(series.getName() + " | " + data.getYValue().toString() + "%");
                    }
                });
                data.getNode().setOnMouseExited(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event){
                        tooltip.hide();
                    }
                });

            }
        }
    }


    /**
     * This method adds a listener to each part of the bar (representing one macro-HG of group).
     * By clicking on this part, a new barplot opens that contains only the haplogroups of the macrogroup.
     */
    public void addListener(){
        //now you can get the nodes.
        for (XYChart.Series<String,Number> serie: sbc.getData()){
            for (XYChart.Data<String, Number> item: serie.getData()){
                Node n = item.getNode();
                n.setEffect(null);
                n.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        n.setEffect(glow);
                    }
                });
                n.addEventHandler(MouseEvent.MOUSE_EXITED,
                        new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                n.setEffect(null);
                            }
                        });
                n.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if(MouseButton.PRIMARY.equals(e.getButton())){
                            try {
                                createSubBarPlot(item);
                            } catch (MalformedURLException e1) {
                                e1.printStackTrace();
                            }
                        }


                    }
                });
            }
        }
    }

    /**
     * This method gets all haplogroups that belongs to the macrogroup and are represented in a
     * group.
     *
     * @param item
     * @throws MalformedURLException
     */
    private void createSubBarPlot(XYChart.Data<String, Number> item)
            throws MalformedURLException {

        String hg = item.getNode().accessibleTextProperty().get().split(" ")[0].trim();
        String group = item.getXValue();

        graphicsMenu.initHaploBarchart("(sub-haplogroups of HG "+ hg +")");
        TableColumn haplo_col = graphicsMenu.getTableController().getTableColumnByName("Haplogroup");
        TableColumn group_col = graphicsMenu.getTableController().getTableColumnByName("Grouping");

        // filter haplo column, include only subgroups of selected Haplogroup
        List<String> sub_hgs = graphicsMenu.getTreeController().getTreeMap().get(hg);

        List<String> columnData = new ArrayList<>();
        for (Object tmp : graphicsMenu.getTableController().getTable().getItems()) {
            ObservableList row = (ObservableList) tmp;
            String hg_row = (String) haplo_col.getCellObservableValue(tmp).getValue();
            if(hg_row.contains("+")){
                hg_row = hg_row.split("\\+")[0];
            }

            if(sub_hgs.contains(hg_row) &&
                    group.equals(group_col.getCellObservableValue(tmp).getValue()))
                columnData.add((String)haplo_col.getCellObservableValue(tmp).getValue());
        }

        graphicsMenu.createHaploBarchart(haplo_col, columnData);
    }


    /**
     * Returns the given String with a fixed number of letters
     *
     * @param string
     * @param letters
     * @return A String with the given length followed by ...
     */
    public static String getMinString(String string, int letters) {
        if (string.length() < letters)
            return string;
        else
            return string.substring(0, letters) + "\n" + string.substring(letters);
    }

    /*

            GETTER and SETTER


     */

    public void setCategories(String[] groups){

        ObservableList categories = FXCollections.observableArrayList();
        for(String s : groups)
            categories.add(s);
            //categories.add(getMinString(s, 15));

        xAxis.setCategories(categories);
    }
    public List<XYChart.Series<String, Number>> getSeriesList() {
        return seriesList;
    }
    public StackedBarChart<String, Number> getSbc() {
        return sbc;
    }



}
