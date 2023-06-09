package MainStage.Components.Main;

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

public class UserRolesPresentationRefresher  extends TimerTask {
    private Consumer<List<String>> rolesNamesConsumer;
    private BooleanProperty shouldUpdate;

    public  UserRolesPresentationRefresher(BooleanProperty autoUpdate, Consumer<List<String>> updateRolesNames){
        this.shouldUpdate=autoUpdate;
        this.rolesNamesConsumer =updateRolesNames;
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
                String jsonArrayOfString =response.body().string();
                String[] rolesNames = GSON_INSTANCE.fromJson(jsonArrayOfString,String[].class);
                rolesNamesConsumer.accept(Arrays.asList(rolesNames));
            }
        });
    }
}
