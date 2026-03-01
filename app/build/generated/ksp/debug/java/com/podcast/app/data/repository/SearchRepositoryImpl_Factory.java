package com.podcast.app.data.repository;

import com.podcast.app.data.remote.api.SearchApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class SearchRepositoryImpl_Factory implements Factory<SearchRepositoryImpl> {
  private final Provider<SearchApi> apiProvider;

  public SearchRepositoryImpl_Factory(Provider<SearchApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public SearchRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static SearchRepositoryImpl_Factory create(Provider<SearchApi> apiProvider) {
    return new SearchRepositoryImpl_Factory(apiProvider);
  }

  public static SearchRepositoryImpl newInstance(SearchApi api) {
    return new SearchRepositoryImpl(api);
  }
}
