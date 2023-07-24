package MainStage.Components.FlowsExecution;

import Flow.Flow;
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
    private Consumer<Flow.Status> checkOnFlowConsumer;
    private BooleanProperty shouldUpdate;
    private StringProperty endMessage;
    private String flowID;

    public FlowExecutionStatusRefresher(BooleanProperty autoUpdate, StringProperty endMessage, Consumer<Flow.Status> checkOnFlow, String flowID) {
        this.shouldUpdate = autoUpdate;
        this.checkOnFlowConsumer=checkOnFlow;
        this.flowID = flowID;
        this.endMessage=endMessage;
    }

    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }

        Map<String,String> map=new HashMap<>();
        map.put("flow_id",flowID);
        String json = GSON_INSTANCE.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        String finalUrl = HttpUrl
                .parse(Constants.FLOW_STATUS)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsyncPost(finalUrl, body,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("check");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfFlowStatus = response.body().string();
                Flow.Status flowStatus = GSON_INSTANCE.fromJson(jsonArrayOfFlowStatus, Flow.Status.class);
                checkOnFlowConsumer.accept(flowStatus);
            }
        });

    }
}
