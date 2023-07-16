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
import java.util.TimerTask;
import java.util.function.Consumer;

import static MainStage.Components.util.Constants.GSON_INSTANCE;

public class UserRolesPresentationRefresher  extends TimerTask {
    private Consumer<UserDescriptor> rolesNamesConsumer;
    private Consumer<Boolean> isManagerConsumer;

    private BooleanProperty shouldUpdate;
    private String userName;

    public  UserRolesPresentationRefresher(BooleanProperty autoUpdate, Consumer<UserDescriptor> updateRolesNames,Consumer<Boolean> isManger, String userName){
        this.shouldUpdate=autoUpdate;
        this.rolesNamesConsumer =updateRolesNames;
        this.userName=userName;
        this.isManagerConsumer=isManger;
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

        String finalUrl2 = HttpUrl
                .parse(Constants.MANAGER)
                .newBuilder()
                .addQueryParameter("target_user", userName)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl2, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonManager =response.body().string();
                Boolean isManager = GSON_INSTANCE.fromJson(jsonManager,Boolean.class);
                isManagerConsumer.accept(isManager);
            }
        });
    }
}
