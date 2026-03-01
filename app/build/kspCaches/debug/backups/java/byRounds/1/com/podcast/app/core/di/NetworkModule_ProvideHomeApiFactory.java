package com.podcast.app.core.di;

import com.podcast.app.data.remote.api.HomeApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class NetworkModule_ProvideHomeApiFactory implements Factory<HomeApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideHomeApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public HomeApi get() {
    return provideHomeApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideHomeApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideHomeApiFactory(retrofitProvider);
  }

  public static HomeApi provideHomeApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideHomeApi(retrofit));
  }
}
