package com.podcast.app.domain.usecase;

import com.podcast.app.domain.repository.SettingsRepository;
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
public final class SaveSettingsUseCase_Factory implements Factory<SaveSettingsUseCase> {
  private final Provider<SettingsRepository> repositoryProvider;

  public SaveSettingsUseCase_Factory(Provider<SettingsRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SaveSettingsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SaveSettingsUseCase_Factory create(
      Provider<SettingsRepository> repositoryProvider) {
    return new SaveSettingsUseCase_Factory(repositoryProvider);
  }

  public static SaveSettingsUseCase newInstance(SettingsRepository repository) {
    return new SaveSettingsUseCase(repository);
  }
}
