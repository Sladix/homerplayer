package com.studio4plus.homerplayer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;

import com.studio4plus.homerplayer.analytics.AnalyticsTracker;
import com.studio4plus.homerplayer.analytics.StatsLogger;
import com.studio4plus.homerplayer.concurrency.BackgroundExecutor;
import com.studio4plus.homerplayer.ui.SoundBank;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import de.greenrobot.event.EventBus;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides @ApplicationScope
    Context provideContext() {
        return application;
    }

    @Provides
    Resources provideResources(Context context) {
        return context.getResources();
    }

    @Provides
    Locale provideCurrentLocale(Resources resources) {
        return resources.getConfiguration().locale;
    }

    @Provides @Reusable
    AudioManager providerAudioManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides @Reusable
    SamplesMap provideSamples() {
        return new SamplesMap();
    }

    @Provides
    EventBus provideEventBus() {
        // TODO: provide the EventBus to all classes via Dagger and then switch to a private instance.
        return EventBus.getDefault();
    }

    @Provides @Singleton
    StatsLogger provideStatsLogger(Context context) {
        return new StatsLogger(context);
    }

    @Provides @Singleton
    AnalyticsTracker provideAnalyticsTracker(
            StatsLogger statsLogger, GlobalSettings globalSettings, EventBus eventBus) {
        return new AnalyticsTracker(statsLogger, globalSettings, eventBus);
    }

    @Provides @Singleton
    SoundBank provideSoundBank(Resources resources) {
        return new SoundBank(resources);
    }

    @Provides @Singleton @Named("IO_EXECUTOR")
    BackgroundExecutor provideIoExecutor(Context applicationContext) {
        HandlerThread ioThread = new HandlerThread("IO");
        ioThread.start();
        Handler ioHandler = new Handler(ioThread.getLooper());
        return new BackgroundExecutor(new Handler(applicationContext.getMainLooper()), ioHandler);
    }
}
