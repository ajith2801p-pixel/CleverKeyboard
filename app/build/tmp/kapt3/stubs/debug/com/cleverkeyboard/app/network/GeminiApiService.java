package com.cleverkeyboard.app.network;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J2\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\u00062\b\b\u0001\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\n\u00a8\u0006\u000b"}, d2 = {"Lcom/cleverkeyboard/app/network/GeminiApiService;", "", "generateContent", "Lretrofit2/Response;", "Lcom/cleverkeyboard/app/network/GenerateContentResponse;", "model", "", "apiKey", "request", "Lcom/cleverkeyboard/app/network/GenerateContentRequest;", "(Ljava/lang/String;Ljava/lang/String;Lcom/cleverkeyboard/app/network/GenerateContentRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface GeminiApiService {
    
    @retrofit2.http.POST(value = "v1beta/models/{model}:generateContent")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object generateContent(@retrofit2.http.Path(value = "model")
    @org.jetbrains.annotations.NotNull()
    java.lang.String model, @retrofit2.http.Query(value = "key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.network.GenerateContentRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.cleverkeyboard.app.network.GenerateContentResponse>> $completion);
}