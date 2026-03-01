package com.podcast.app.domain.usecase;

import com.podcast.app.domain.repository.HomeRepository;
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
public final class GetHomeSectionsUseCase_Factory implements Factory<GetHomeSectionsUseCase> {
  private final Provider<HomeRepository> repositoryProvider;

  public GetHomeSectionsUseCase_Factory(Provider<HomeRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetHomeSectionsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetHomeSectionsUseCase_Factory create(Provider<HomeRepository> repositoryProvider) {
    return new GetHomeSectionsUseCase_Factory(repositoryProvider);
  }

  public static GetHomeSectionsUseCase newInstance(HomeRepository repository) {
    return new GetHomeSectionsUseCase(repository);
  }
}
