package MainStage.Components.FlowsExecution;

import Flow.FlowDescriptor;
import MainStage.Components.util.Constants;
import MainStage.Components.util.HttpClientUtil;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static MainStage.Components.util.Constants.GSON_INSTANCE;

public class FlowExecutionStatusRefresher extends TimerTask {
    private Consumer<List<Integer>> flowExecutionStatusConsumer;
    private Consumer<String> clearStatusConsumer;
    private BooleanProperty shouldUpdate;
    private String flowID;

    public FlowExecutionStatusRefresher(BooleanProperty autoUpdate, Consumer<List<Integer>> updateFlowsList, Consumer<String> clearStatus, String flowID) {
        this.shouldUpdate = autoUpdate;
        this.flowExecutionStatusConsumer = updateFlowsList;
        this.clearStatusConsumer=clearStatus;
        this.flowID = flowID;
    }

    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            clearStatusConsumer.accept("");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.FLOW_STATUS)
                .newBuilder()
                .addQueryParameter("flow_id", flowID)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfFlowStatus = response.body().string();
                Integer[] flowStatus = GSON_INSTANCE.fromJson(jsonArrayOfFlowStatus, Integer[].class);
                flowExecutionStatusConsumer.accept(Arrays.asList(flowStatus));
            }
        });

    }
}
