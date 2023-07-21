package MainStage.Components.FlowsExecution;

import Flow.FlowDescriptor;
import MainStage.Components.util.Constants;
import MainStage.Components.util.HttpClientUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static MainStage.Components.util.Constants.GSON_INSTANCE;

public class FlowExecutionStatusRefresher extends TimerTask {
    private Consumer<Boolean> checkOnFlowConsumer;
    private Consumer<String> flowExecutionStatusConsumer;
    private Consumer<String> clearStatusConsumer;
    private BooleanProperty shouldUpdate;
    private StringProperty endMessage;
    private String flowID;

    public FlowExecutionStatusRefresher(BooleanProperty autoUpdate, StringProperty endMessage, Consumer<Boolean> checkOnFlow, Consumer<String> updateFlowsStatusLabel, Consumer<String>clearStatus, String flowID) {
        this.shouldUpdate = autoUpdate;
        this.checkOnFlowConsumer=checkOnFlow;
        this.flowExecutionStatusConsumer = updateFlowsStatusLabel;
        this.clearStatusConsumer=clearStatus;
        this.flowID = flowID;
        this.endMessage=endMessage;
    }

    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            clearStatusConsumer.accept(endMessage.get());
            return;
        }

        Map<String,String> map=new HashMap<>();
        map.put("flow_id",flowID);
        String json = GSON_INSTANCE.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        String finalUrl = HttpUrl
                .parse(Constants.IS_FLOW_RUNNING)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsyncPost(finalUrl, body,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfFlowStatus = response.body().string();
                Boolean isFlowRunning = GSON_INSTANCE.fromJson(jsonArrayOfFlowStatus, Boolean.class);
                checkOnFlowConsumer.accept(isFlowRunning);
            }
        });

    }
}
