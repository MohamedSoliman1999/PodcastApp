package com.podcast.app.domain.usecase;

import com.podcast.app.domain.repository.SearchRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SearchUseCase_Factory implements Factory<SearchUseCase> {
  private final Provider<SearchRepository> repositoryProvider;

  public SearchUseCase_Factory(Provider<SearchRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SearchUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SearchUseCase_Factory create(Provider<SearchRepository> repositoryProvider) {
    return new SearchUseCase_Factory(repositoryProvider);
  }

  public static SearchUseCase newInstance(SearchRepository repository) {
    return new SearchUseCase(repository);
  }
}
