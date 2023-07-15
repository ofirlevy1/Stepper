package MainStage.Components.Main;

import MainStage.Components.util.Constants;
import MainStage.Components.util.HttpClientUtil;
import Users.UserDescriptor;
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

public class UserRolesPresentationRefresher  extends TimerTask {
    private Consumer<UserDescriptor> rolesNamesConsumer;
    private BooleanProperty shouldUpdate;
    private String userName;

    public  UserRolesPresentationRefresher(BooleanProperty autoUpdate, Consumer<UserDescriptor> updateRolesNames, String userName){
        this.shouldUpdate=autoUpdate;
        this.rolesNamesConsumer =updateRolesNames;
        this.userName=userName;
    }
    @Override
    public void run() {
        if(!shouldUpdate.get())
            return;
        String finalUrl = HttpUrl
                .parse(Constants.GET_USER_DESCRIPTION)
                .newBuilder()
                .addQueryParameter("target_user", userName)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonUserDescriptor =response.body().string();
                UserDescriptor userDescriptor = GSON_INSTANCE.fromJson(jsonUserDescriptor,UserDescriptor.class);
                rolesNamesConsumer.accept(userDescriptor);
            }
        });
    }
}
