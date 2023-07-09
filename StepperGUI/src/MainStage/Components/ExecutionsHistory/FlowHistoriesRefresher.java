package MainStage.Components.ExecutionsHistory;

import Flow.FlowDescriptor;
import MainStage.Components.util.Constants;
import MainStage.Components.util.HttpClientUtil;
import RunHistory.FlowRunHistory;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static MainStage.Components.util.Constants.GSON_INSTANCE;

public class FlowHistoriesRefresher extends TimerTask {
    private Consumer<List<FlowRunHistory>> flowHistoriesListConsumer;
    private BooleanProperty shouldUpdate;

    public  FlowHistoriesRefresher(BooleanProperty autoUpdate, Consumer<List<FlowRunHistory>> updateFlowsList){
        this.shouldUpdate=autoUpdate;
        this.flowHistoriesListConsumer=updateFlowsList;
    }
    @Override
    public void run() {
        if(!shouldUpdate.get())
            return;
        HttpClientUtil.runAsync(Constants.FLOW_HISTORIES, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfFlowHistories =response.body().string();
                FlowRunHistory[] flowRunHistories = GSON_INSTANCE.fromJson(jsonArrayOfFlowHistories,FlowRunHistory[].class);
                flowHistoriesListConsumer.accept(Arrays.asList(flowRunHistories));
            }
        });
    }
}
