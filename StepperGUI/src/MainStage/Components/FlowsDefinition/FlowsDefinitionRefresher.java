package MainStage.Components.FlowsDefinition;

import Flow.FlowDescriptor;
import MainStage.Components.util.Constants;
import MainStage.Components.util.HttpClientUtil;
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

public class FlowsDefinitionRefresher extends TimerTask {

    private Consumer<List<FlowDescriptor>> flowDescriptorsListConsumer;
    private BooleanProperty shouldUpdate;

    public FlowsDefinitionRefresher(BooleanProperty autoUpdate, Consumer<List<FlowDescriptor>> updateFlowsList) {
        this.shouldUpdate=autoUpdate;
        this.flowDescriptorsListConsumer=updateFlowsList;
    }

    @Override
    public void run() {
        if(!shouldUpdate.get())
            return;
        HttpClientUtil.runAsync(Constants.FLOWS_DESCRIPTORS, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfFlowDescriptors =response.body().string();
                FlowDescriptor[] flowDescriptors = GSON_INSTANCE.fromJson(jsonArrayOfFlowDescriptors,FlowDescriptor[].class);
                flowDescriptorsListConsumer.accept(Arrays.asList(flowDescriptors));
            }
        });


    }
}
