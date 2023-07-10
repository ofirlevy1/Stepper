package MainStage.Components.UsersManagement;

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

public class AvailableUsersRefresher extends TimerTask {

    private Consumer<List<String>> usersListConsumer;
    private BooleanProperty shouldUpdate;

    public AvailableUsersRefresher(BooleanProperty autoUpdate, Consumer<List<String>> updateUsersList) {
        this.usersListConsumer=updateUsersList;
        this.shouldUpdate=autoUpdate;
    }

    @Override
    public void run() {
        if(!shouldUpdate.get())
            return;
        HttpClientUtil.runAsync(Constants.GET_USERS, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfUsersNames =response.body().string();
                String[] usersNames = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames,String[].class);
                usersListConsumer.accept(Arrays.asList(usersNames));
            }
        });

    }
}
