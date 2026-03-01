package com.podcast.app.ui.screens.settings;

import com.podcast.app.domain.usecase.GetSettingsUseCase;
import com.podcast.app.domain.usecase.SaveSettingsUseCase;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<GetSettingsUseCase> getSettingsUseCaseProvider;

  private final Provider<SaveSettingsUseCase> saveSettingsUseCaseProvider;

  public SettingsViewModel_Factory(Provider<GetSettingsUseCase> getSettingsUseCaseProvider,
      Provider<SaveSettingsUseCase> saveSettingsUseCaseProvider) {
    this.getSettingsUseCaseProvider = getSettingsUseCaseProvider;
    this.saveSettingsUseCaseProvider = saveSettingsUseCaseProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(getSettingsUseCaseProvider.get(), saveSettingsUseCaseProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<GetSettingsUseCase> getSettingsUseCaseProvider,
      Provider<SaveSettingsUseCase> saveSettingsUseCaseProvider) {
    return new SettingsViewModel_Factory(getSettingsUseCaseProvider, saveSettingsUseCaseProvider);
  }

  public static SettingsViewModel newInstance(GetSettingsUseCase getSettingsUseCase,
      SaveSettingsUseCase saveSettingsUseCase) {
    return new SettingsViewModel(getSettingsUseCase, saveSettingsUseCase);
  }
}
