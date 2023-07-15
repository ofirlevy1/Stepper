package MainStage.Components.Statistics;

import Flow.FlowStatistics;
import MainStage.Components.util.Constants;
import MainStage.Components.util.HttpClientUtil;
import Steps.StepStatistics;
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

public class StatisticsTableRefresher extends TimerTask {

    private Consumer<List<FlowStatistics>> flowStatisticsConsumer;
    private Consumer<List<StepStatistics>> stepStatisticsConsumer;
    private BooleanProperty shouldUpdate;

    public StatisticsTableRefresher(BooleanProperty autoUpdate, Consumer<List<FlowStatistics>> updateStatisticsList, Consumer<List<StepStatistics>> stepStatisticsConsumer) {
        this.flowStatisticsConsumer =updateStatisticsList;
        this.stepStatisticsConsumer=stepStatisticsConsumer;
        this.shouldUpdate=autoUpdate;
    }

    @Override
    public void run() {
        if(!shouldUpdate.get())
            return;
        HttpClientUtil.runAsync(Constants.FLOW_STATISTICS, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==200) {
                    String jsonArrayOfRolesNames = response.body().string();
                    FlowStatistics[] flowStatistics = GSON_INSTANCE.fromJson(jsonArrayOfRolesNames, FlowStatistics[].class);
                    flowStatisticsConsumer.accept(Arrays.asList(flowStatistics));
                }
            }
        });

        HttpClientUtil.runAsync(Constants.STEP_STATISTICS, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==200){
                    String jsonArrayOfRolesNames = response.body().string();
                    StepStatistics[] stepStatistics = GSON_INSTANCE.fromJson(jsonArrayOfRolesNames, StepStatistics[].class);
                    stepStatisticsConsumer.accept(Arrays.asList(stepStatistics));
                }
            }
        });

    }
}
