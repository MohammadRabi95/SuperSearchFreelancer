package com.itsoft.site.blocker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerCalls {

    private ServerCalls() { }

    public static ServerCalls getInstance()

    {
        if(instance == null) instance = new ServerCalls();
        return instance;
    }

    private static ServerCalls instance = null;

    public void GetKeywordData(final OnKeywordResult callback)

    {
        HttpApiInterface.getHttpService().getKeywordData().enqueue(new Callback<KeywordModel>()

        {
            @Override
            public void onResponse(Call<KeywordModel> call, Response<KeywordModel> response)

            {
                if(response.isSuccessful() && response.body() != null)

                {
                    callback.onSuccess(response.body());
                }

                else

                {
                    callback.onFailed(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<KeywordModel> call, Throwable t) {
                callback.onFailed(-1, t.getMessage());
            }
        });
    }

    public interface OnKeywordResult

    {
        void onSuccess(KeywordModel keywordModel);
        void onFailed(int errorCode, String errorMessage);
    }

}
