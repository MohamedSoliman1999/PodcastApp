package com.podcast.app.core.di;

import com.podcast.app.data.remote.api.SearchApi;
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
public final class NetworkModule_ProvideSearchApiFactory implements Factory<SearchApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideSearchApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public SearchApi get() {
    return provideSearchApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideSearchApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideSearchApiFactory(retrofitProvider);
  }

  public static SearchApi provideSearchApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideSearchApi(retrofit));
  }
}
