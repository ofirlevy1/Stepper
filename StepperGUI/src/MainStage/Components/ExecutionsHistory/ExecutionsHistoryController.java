package MainStage.Components.ExecutionsHistory;

import Flow.Flow;
import MainStage.Components.Main.MainStepperController;
import RunHistory.FlowRunHistory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;

public class ExecutionsHistoryController {

    @FXML
    private TableView<FlowRunHistory> pastExecutionsTable;
    @FXML
    private TableColumn<FlowRunHistory, String> flowNameColumn;
    @FXML
    private TableColumn<FlowRunHistory, String> timeStampColumn;
    @FXML
    private TableColumn<FlowRunHistory, String> statusColumn;
    @FXML
    private FlowPane flowDetailsFlowPane;
    @FXML
    private FlowPane executionElementsFlowPane;
    @FXML
    private Button rerunFlowButton;
    @FXML
    private CheckBox successfulExecutionsFilterCheckBox;

    private MainStepperController mainStepperController;
    private boolean filterCheckboxMarked;
    private ObservableList<FlowRunHistory> flowRunHistoryObservableList;

    @FXML
    public void initialize(){
        flowNameColumn.setCellValueFactory(new PropertyValueFactory<>("flowName"));
        timeStampColumn.setCellValueFactory(new PropertyValueFactory<>("timeStamp"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        flowNameColumn.setCellFactory(column -> new TableCellWithHyperlink<>());
        filterCheckboxMarked=false;
        flowRunHistoryObservableList=FXCollections.observableArrayList();
    }

    @FXML
    void rerunFlowButtonAction(ActionEvent event) {

    }

    @FXML
    void successfulPastExecutionsFilter(ActionEvent event) {
        FilteredList<FlowRunHistory> flowRunHistories=new FilteredList<>(flowRunHistoryObservableList);
        if(filterCheckboxMarked){
            filterCheckboxMarked=false;
            flowRunHistories.setPredicate(flowRunHistory -> true);
            pastExecutionsTable.setItems(flowRunHistories);
        }
        else{
            filterCheckboxMarked=true;
            flowRunHistories.setPredicate(flowRunHistory -> flowRunHistory.getStatus()== Flow.Status.SUCCESS);
            pastExecutionsTable.setItems(flowRunHistories);
        }
    }

    public void setMainStepperController(MainStepperController mainStepperController) {
        this.mainStepperController = mainStepperController;
    }

    public void updateExecutionsTable(){
        FlowRunHistory flowRunHistory = mainStepperController.getStepperUIManager().getFlowsRunHistories().get(mainStepperController.getStepperUIManager().getFlowsRunHistories().size()-1);
        //pastExecutionsTable.getItems().add(flowRunHistory);
        flowRunHistoryObservableList.add(flowRunHistory);
        pastExecutionsTable.setItems(flowRunHistoryObservableList);
        filterCheckboxMarked=false;
        successfulExecutionsFilterCheckBox.setSelected(false);
    }

    private void updateFlowsDetails(FlowRunHistory flowRunHistory){
        flowDetailsFlowPane.getChildren().clear();
        flowDetailsFlowPane.getChildren().add(new Label(flowRunHistory.showExtensiveFlowHistory()));
    }

    private class TableCellWithHyperlink<T> extends javafx.scene.control.TableCell<T, String> {

        private final Hyperlink hyperlink;

        public TableCellWithHyperlink() {
            this.hyperlink = new Hyperlink();
            this.hyperlink.setOnAction(event -> updateFlowsDetails((FlowRunHistory)getTableView().getItems().get(getIndex())));
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                hyperlink.setText(item);
                setGraphic(hyperlink);
            }
        }
    }
}
