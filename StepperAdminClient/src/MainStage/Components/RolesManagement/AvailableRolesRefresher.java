package MainStage.Components.RolesManagement;

import MainStage.Components.util.Constants;
import MainStage.Components.util.HttpClientUtil;
import com.google.gson.Gson;
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

public class AvailableRolesRefresher extends TimerTask {

    private Consumer<List<String>> rolesListConsumer;
    private BooleanProperty shouldUpdate;
    public AvailableRolesRefresher(BooleanProperty autoUpdate, Consumer<List<String>> updateRolesList) {
        this.rolesListConsumer=updateRolesList;
        this.shouldUpdate=autoUpdate;
    }

    @Override
    public void run() {
        if(!shouldUpdate.get())
            return;
        HttpClientUtil.runAsync(Constants.GET_ROLES, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfRolesNames=response.body().string();
                String[] rolesNames= GSON_INSTANCE.fromJson(jsonArrayOfRolesNames,String[].class);
                rolesListConsumer.accept(Arrays.asList(rolesNames));
            }
        });

    }
}
