package in.rohitgupta.sscmtspractice;

import android.app.Activity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public final class AdsManager {
    // Official Google test unit. Replace only after the published app is approved in AdMob.
    private static final String TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111";
    private AdView banner;
    private ConsentInformation consentInformation;
    private boolean mobileAdsStartRequested;
    private boolean mobileAdsReady;
    private Runnable stateChanged;

    public void initialize(Activity activity, Runnable onStateChanged) {
        stateChanged = onStateChanged;
        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        ConsentRequestParameters params = new ConsentRequestParameters.Builder().build();

        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity, formError -> {
                    startAdsWhenAllowed(activity);
                    notifyStateChanged(activity);
                }),
                requestError -> {
                    // A previous valid choice can still permit ads when a refresh temporarily fails.
                    startAdsWhenAllowed(activity);
                    notifyStateChanged(activity);
                }
        );

        // A valid choice from an earlier launch can be used immediately.
        startAdsWhenAllowed(activity);
    }

    public void attachTestBanner(Activity activity, ViewGroup parent) {
        if (!mobileAdsReady || consentInformation == null || !consentInformation.canRequestAds()) return;
        destroy();
        FrameLayout holder = new FrameLayout(activity);
        holder.setMinimumHeight(dp(activity, 54));
        holder.setForegroundGravity(Gravity.CENTER);

        banner = new AdView(activity);
        banner.setAdSize(AdSize.BANNER);
        banner.setAdUnitId(TEST_BANNER_ID);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        holder.addView(banner, params);
        parent.addView(holder, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(activity, 54)
        ));
        banner.loadAd(new AdRequest.Builder().build());
    }

    public boolean isPrivacyOptionsRequired() {
        return consentInformation != null
                && consentInformation.getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
    }

    public void showPrivacyOptions(Activity activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, formError -> {
            startAdsWhenAllowed(activity);
            notifyStateChanged(activity);
        });
    }

    private synchronized void startAdsWhenAllowed(Activity activity) {
        if (consentInformation == null || !consentInformation.canRequestAds() || mobileAdsStartRequested) return;
        mobileAdsStartRequested = true;
        new Thread(() -> MobileAds.initialize(activity, status -> {
            mobileAdsReady = true;
            notifyStateChanged(activity);
        })).start();
    }

    private void notifyStateChanged(Activity activity) {
        if (stateChanged != null) activity.runOnUiThread(stateChanged);
    }

    public void destroy() {
        if (banner != null) {
            banner.destroy();
            banner = null;
        }
    }

    private int dp(Activity activity, int value) {
        return Math.round(value * activity.getResources().getDisplayMetrics().density);
    }
}
